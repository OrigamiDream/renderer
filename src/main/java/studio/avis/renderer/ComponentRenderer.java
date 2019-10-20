package studio.avis.renderer;

import org.springframework.stereotype.Component;
import studio.avis.renderer.annotations.RendererAttribute;
import studio.avis.renderer.annotations.RendererAttributes;
import studio.avis.renderer.components.BaseComponent;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ComponentRenderer
 *
 * Transform fields in HTML form request instance assembled by Spring, and which annotated
 * with rendering-annotation that is registered in RendererRegistry or an instance which
 * defined with Bean in other configuration into auto-generated HTML markup.
 */
@Component
public class ComponentRenderer {

    private static ComponentRenderer instance;

    private final RendererRegistry defaultRendererRegistry = new RendererRegistry();
    private final Optional<RendererRegistry> rendererRegistry;

    public ComponentRenderer(Optional<RendererRegistry> rendererRegistry) {
        this.rendererRegistry = rendererRegistry;

        instance = this;
    }

    public static ComponentRenderer getInstance() {
        return instance;
    }

    public RendererRegistry registry() {
        return rendererRegistry.orElse(defaultRendererRegistry);
    }

    enum FieldAnnotationInvalidError {

        INVALID_SPECIFIC_ANNOTATION,
        INVALID_ANNOTATION_IN_REGISTRY,
        SUCCESS

    }

    private FieldAnnotationInvalidError extractFromField(Field field, @Nullable Class<? extends Annotation> specificAnnotation, AtomicReference<Annotation> annotationWrapper) {
        if(specificAnnotation != null) {
            Annotation annotation = field.getAnnotation(specificAnnotation);
            if(annotation != null) {
                annotationWrapper.set(annotation);
                return FieldAnnotationInvalidError.SUCCESS;
            }
            return FieldAnnotationInvalidError.INVALID_SPECIFIC_ANNOTATION;
        }

        for(Class<? extends Annotation> annotationType : registry().getAnnotations()) {
            Annotation annotation = field.getAnnotation(annotationType);
            if(annotation != null) {
                annotationWrapper.set(annotation);
                return FieldAnnotationInvalidError.SUCCESS;
            }
        }

        for(Class<? extends Annotation> annotationType : Renderers.ANNOTATIONS) {
            Annotation annotation = field.getAnnotation(annotationType);
            if(annotation != null) {
                annotationWrapper.set(annotation);
                return FieldAnnotationInvalidError.SUCCESS;
            }
        }
        return FieldAnnotationInvalidError.INVALID_ANNOTATION_IN_REGISTRY;
    }

    private Renderer findRendererFrom(Class<? extends Renderer> rendererType) {
        for(Renderer renderer : registry().getRenderers()) {
            if(rendererType.isInstance(renderer)) {
                return renderer;
            }
        }

        for(Renderer renderer : Renderers.getRenderers()) {
            if(rendererType.isInstance(renderer)) {
                return renderer;
            }
        }
        throw new IllegalArgumentException("Renderer '" + rendererType.getSimpleName() + "' is not valid in registry.");
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

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName) {
        return render(httpServletRequest, component, fieldName, new HashMap<>(), null, null);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Map<Object, Object> attributes) {
        return render(httpServletRequest, component, fieldName, attributes, null, null);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Class<? extends Annotation> specificAnnotation) {
        return renderInternal(specificAnnotation, fieldName, component, rendererType -> render(httpServletRequest, component, fieldName, specificAnnotation, rendererType));
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        return render(httpServletRequest, component, fieldName, new HashMap<>(), specificAnnotation, specificRendererType);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Map<Object, Object> attributes, Class<? extends Annotation> specificAnnotation, Class<? extends Renderer> specificRendererType) {
        try {
            Field field = component.getClass().getDeclaredField(fieldName);
            return render(httpServletRequest, component, field, attributes, specificAnnotation, specificRendererType);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field) {
        return render(httpServletRequest, component, field, new HashMap<>(), null, null);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Map<Object, Object> attributes) {
        return render(httpServletRequest, component, field, attributes, null, null);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Class<? extends Annotation> specificAnnotation) {
        return renderInternal(specificAnnotation, field.getName(), component, rendererType -> render(httpServletRequest, component, field, specificAnnotation, rendererType));
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        return render(httpServletRequest, component, field, new HashMap<>(), specificAnnotation, specificRendererType);
    }

    public BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Map<Object, Object> attributes, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        try {
            field.setAccessible(true);
            return renderField(new RenderingAttribute(httpServletRequest, attributes != null ? attributes : new HashMap<>()), field, component, specificAnnotation, specificRendererType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BaseComponent> render(HttpServletRequest httpServletRequest, Object component) {
        return render(httpServletRequest, component, new HashMap<>());
    }

    public List<BaseComponent> render(HttpServletRequest httpServletRequest, Object component, @Nullable Map<Object, Object> attributes) {
        return renderComponent(new RenderingAttribute(httpServletRequest, attributes != null ? attributes : new HashMap<>()), component);
    }

    private List<BaseComponent> renderComponent(RenderingAttribute attribute, Object component) {
        return Arrays.stream(component.getClass().getDeclaredFields()).filter(field -> extractFromField(field, null, new AtomicReference<>()) == FieldAnnotationInvalidError.SUCCESS).map(field -> {
            try {
                field.setAccessible(true);
                return renderField(attribute, field, component, null, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    private BaseComponent renderField(RenderingAttribute attribute, Field field, Object component, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) throws IllegalAccessException {
        AtomicReference<Annotation> annotationWrapper = new AtomicReference<>();
        FieldAnnotationInvalidError result = extractFromField(field, specificAnnotation, annotationWrapper);

        Annotation annotation;
        switch (result) {
            case INVALID_SPECIFIC_ANNOTATION:
                throw new IllegalArgumentException("Field '" + field.getName() + "' doesn't have @" + specificAnnotation.getSimpleName() + " annotation.");

            case INVALID_ANNOTATION_IN_REGISTRY:
                throw new IllegalArgumentException("Annotation of the field '" + field.getName() + "' is not valid in registry.");

            default:
                throw new IllegalArgumentException("Unknown result error type: " + result.name());

            case SUCCESS:
                annotation = annotationWrapper.get();
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
            Renderer renderer = findRendererFrom(specificRendererType != null ? specificRendererType : rendererType.get());

            if(renderer == null) {
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
            return renderer.render(this, attribute, component, rendererField);
        } else {
            throw new IllegalArgumentException("Renderer Type is invalid in annotation @" + annotation.getClass().getSimpleName() + " at field '" + field.getName() + "' in component '" + component.getClass().getSimpleName() + "'.");
        }
    }
}
