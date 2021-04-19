package studio.avis.renderer;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import studio.avis.renderer.annotations.RendererAttribute;
import studio.avis.renderer.annotations.RendererAttributes;
import studio.avis.renderer.components.BaseComponent;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ComponentRenderer
 *
 * Transform fields in HTML form request instance assembled by Spring, and which annotated
 * with rendering-annotation that contains renderer method or an instance which
 * defined with Bean in other configuration into auto-generated HTML markup.
 */
@Component
public class ComponentRendererImpl implements ComponentRenderer {

    private static ComponentRenderer instance;

    private final Map<Class<? extends Renderer>, Renderer> cachedRenderers = new HashMap<>();

    public ComponentRendererImpl() {
        instance = this;
    }

    public static ComponentRenderer getInstance() {
        return instance;
    }

    enum FieldAnnotationInvalidError {

        INVALID_SPECIFIC_ANNOTATION,
        RENDERER_METHOD_NOT_FOUND,
        MULTIPLE_RENDERER_ANNOTATIONS,
        SUCCESS

    }

    @Override
    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Class<? extends Annotation> specificAnnotation) {
        return renderInternal(specificAnnotation, fieldName, component, rendererType -> render(httpServletRequest, component, fieldName, specificAnnotation, rendererType));
    }

    @Override
    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Map<Object, Object> attributes, Class<? extends Annotation> specificAnnotation, Class<? extends Renderer> specificRendererType) {
        try {
            Field field = component.getClass().getDeclaredField(fieldName);
            return render(httpServletRequest, component, field, attributes, specificAnnotation, specificRendererType);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Class<? extends Annotation> specificAnnotation) {
        return renderInternal(specificAnnotation, field.getName(), component, rendererType -> render(httpServletRequest, component, field, specificAnnotation, rendererType));
    }

    @Override
    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Map<Object, Object> attributes, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        try {
            field.setAccessible(true);
            return renderField(new RenderingAttribute(httpServletRequest, attributes != null ? attributes : new HashMap<>()), field, component, Optional.ofNullable(specificAnnotation), Optional.ofNullable(specificRendererType));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BaseComponent> render(HttpServletRequest httpServletRequest, Object component, @Nullable Map<Object, Object> attributes) {
        return renderComponent(new RenderingAttribute(httpServletRequest, attributes != null ? attributes : new HashMap<>()), component);
    }

    private FieldAnnotationInvalidError extractFromField(Field field, Optional<Class<? extends Annotation>> specificAnnotation, Optional<AtomicReference<Annotation>> annotationWrapper) {
        Predicate<Annotation> hasRenderer = annotation -> {
            try {
                annotation.annotationType().getDeclaredMethod("renderer");
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        };
        if(specificAnnotation.isPresent()) {
            return specificAnnotation.map(field::getAnnotation).map(annotation -> {
                annotationWrapper.ifPresent(wrapper -> wrapper.set(annotation));
                return hasRenderer.test(annotation) ? FieldAnnotationInvalidError.SUCCESS : FieldAnnotationInvalidError.RENDERER_METHOD_NOT_FOUND;
            }).orElse(FieldAnnotationInvalidError.INVALID_SPECIFIC_ANNOTATION);
        }
        List<Annotation> stream = Arrays.stream(field.getAnnotations()).filter(hasRenderer).collect(Collectors.toList());
        if(stream.size() > 1) {
            return FieldAnnotationInvalidError.MULTIPLE_RENDERER_ANNOTATIONS;
        } else if(stream.size() == 0) {
            return FieldAnnotationInvalidError.RENDERER_METHOD_NOT_FOUND;
        } else {
            Optional<Annotation> optional = stream.stream().findFirst();
            optional.ifPresent(annotation -> {
                annotationWrapper.ifPresent(wrapper -> wrapper.set(annotation));
            });
            return FieldAnnotationInvalidError.SUCCESS;
        }
    }

    private Optional<Class<? extends Renderer>> getDefaultRendererType(Class<? extends Annotation> specificAnnotation) {
        Optional<Class<? extends Renderer>> defaultRendererType = Optional.empty();
        try {
            Method render = specificAnnotation.getDeclaredMethod("renderer");
            defaultRendererType = Optional.ofNullable((Class<? extends Renderer>) render.getDefaultValue());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return defaultRendererType;
    }

    private <T> T renderInternal(Class<? extends Annotation> specificAnnotation, @Nullable String fieldName, Object component, Function<Class<? extends Renderer>, T> callback) {
        Optional<Class<? extends Renderer>> defaultRendererType = getDefaultRendererType(specificAnnotation);
        if(defaultRendererType.isPresent()) {
            return callback.apply(defaultRendererType.get());
        } else {
            if(fieldName != null) {
                throw new IllegalArgumentException("Default Renderer Type is invalid in annotation @" + specificAnnotation.getSimpleName() + " at field '" + fieldName + "' in component '" + component.getClass().getSimpleName() + "'.");
            } else {
                throw new IllegalArgumentException("Default Renderer Type is invalid in annotation @" + specificAnnotation.getSimpleName() + " in one of fields in component '" + component.getClass().getSimpleName() + "'.");
            }
        }
    }

    private List<BaseComponent> renderComponent(RenderingAttribute attribute, Object component) {
        return Arrays.stream(component.getClass().getDeclaredFields()).filter(field -> extractFromField(field, Optional.empty(), Optional.empty()) == FieldAnnotationInvalidError.SUCCESS).map(field -> {
            try {
                field.setAccessible(true);
                return renderField(attribute, field, component, Optional.empty(), Optional.empty());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    private BaseComponent renderField(RenderingAttribute attribute, Field field, Object component, Optional<Class<? extends Annotation>> specificAnnotation, Optional<Class<? extends Renderer>> specificRendererType) throws IllegalAccessException {
        Optional<AtomicReference<Annotation>> annotationWrapper = Optional.of(new AtomicReference<>());
        FieldAnnotationInvalidError result = extractFromField(field, specificAnnotation, annotationWrapper);

        Annotation annotation;
        switch (result) {
            case INVALID_SPECIFIC_ANNOTATION:
                throw new IllegalArgumentException("Field '" + field.getName() + "' doesn't have @" + specificAnnotation.map(Class::getSimpleName).orElse("INVALID_ANNOTATION") + " annotation.");

            case RENDERER_METHOD_NOT_FOUND:
                Optional<String> name = Optional.ofNullable(annotationWrapper.get().get()).map(o -> o.annotationType().getSimpleName());
                throw new IllegalArgumentException("Annotation '" + name.orElse("INVALID_ANNOTATION") + "' doesn't have 'renderer()' method.");

            case MULTIPLE_RENDERER_ANNOTATIONS:
                throw new IllegalArgumentException("There are multiple annotations that have 'renderer()' method on field '" + field.getName() + "'.");

            default:
                throw new IllegalArgumentException("Unknown result error type: " + result.name());

            case SUCCESS:
                annotation = annotationWrapper.get().get();
                break;
        }

        Object value = field.get(component);

        RendererField rendererField = new RendererField(field, value);

        Optional<Class<? extends Renderer>> rendererType = Optional.empty();
        RendererAttribute[] attributes = new RendererAttribute[0];
        try {
            Method render = annotation.annotationType().getDeclaredMethod("renderer");
            render.setAccessible(true);
            rendererType = Optional.ofNullable((Class<? extends Renderer>) render.invoke(annotation));

            RendererAttributes rendererAttributes = field.getAnnotation(RendererAttributes.class);
            if(rendererAttributes != null) {
                attributes = rendererAttributes.value();
            } else {
                RendererAttribute rendererAttribute = field.getAnnotation(RendererAttribute.class);
                if(rendererAttribute != null) {
                    attributes = new RendererAttribute[] { rendererAttribute };
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if(rendererType.isPresent()) {
            Optional<Renderer> renderer = findOrCreateRenderer(specificRendererType.orElse(rendererType.get()));
            if(!renderer.isPresent()) {
                throw new IllegalArgumentException("Renderer " + rendererType.get().getSimpleName() + " is not valid in registry.");
            }

            for(RendererAttribute rendererAttribute : attributes) {
                Object attributeValue;
                if(rendererAttribute.valueType() == String.class) {
                    attributeValue = rendererAttribute.textValue();
                } else if(rendererAttribute.valueType() == boolean.class) {
                    attributeValue = rendererAttribute.booleanValue();
                } else if(rendererAttribute.valueType() == int.class) {
                    attributeValue = rendererAttribute.intValue();
                } else if(rendererAttribute.valueType() == double.class) {
                    attributeValue = rendererAttribute.doubleValue();
                } else if(rendererAttribute.valueType() == float.class) {
                    attributeValue = rendererAttribute.floatValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type in @RendererAttribute: " + rendererAttribute.valueType().getSimpleName());
                }
                attribute.put(rendererAttribute.key(), attributeValue);
            }
            return renderer.get().render(this, attribute, component, rendererField);
        } else {
            throw new IllegalArgumentException("Renderer Type is invalid in annotation @" + annotation.getClass().getSimpleName() + " at field '" + field.getName() + "' in component '" + component.getClass().getSimpleName() + "'.");
        }
    }

    private <T> Optional<T> findBean(Class<? extends T> type) {
        try {
            return Optional.of(ApplicationContextProvider.getContext().getBean(type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private <T> Optional<T> createInstanceFromConstructor(Constructor<T> constructor, Object[] parameters) {
        try {
            constructor.setAccessible(true);
            return Optional.of(constructor.newInstance(parameters));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<Renderer> findOrCreateRenderer(Class<? extends Renderer> rendererType) {
        if(cachedRenderers.containsKey(rendererType)) {
            return Optional.of(cachedRenderers.get(rendererType));
        }
        Optional<Renderer> renderer = findOrCreateRenderer0(rendererType);
        renderer.ifPresent(rd -> cachedRenderers.computeIfAbsent(rendererType, o -> rd));
        return renderer;
    }

    private Optional<Renderer> findOrCreateRenderer0(Class<? extends Renderer> rendererType) {
        if(ApplicationContextProvider.getContext() == null) {
            return Optional.ofNullable(findOrCreateRendererConstructors(rendererType).orElseThrow(() -> new IllegalArgumentException("ApplicationContext is not available")));
        }
        Optional<Renderer> renderer = findBean(rendererType);
        return renderer.isPresent() ? renderer : findOrCreateRendererConstructors(rendererType);
    }

    private Optional<Renderer> findOrCreateRendererConstructors(Class<? extends Renderer> rendererType) {
        Constructor<?>[] constructors = rendererType.getDeclaredConstructors();
        Optional<Constructor<?>> emptyConstructor = Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny();

        if(emptyConstructor.isPresent()) {
            return emptyConstructor.map(constructor -> {
                try {
                    constructor.setAccessible(true);
                    return (Renderer) constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
        ApplicationContext context = ApplicationContextProvider.getContext();
        if(context == null) {
            throw new IllegalStateException("ApplicationContext is not valid.");
        }

        for(Constructor<?> constructor : constructors) {
            Stream<Optional<?>> stream = Arrays.stream(constructor.getParameterTypes()).map(this::findBean);
            if(stream.allMatch(Optional::isPresent)) {
                return (Optional<Renderer>) createInstanceFromConstructor(constructor, stream.map(Optional::get).toArray());
            }
        }
        return Optional.empty();
    }
}
