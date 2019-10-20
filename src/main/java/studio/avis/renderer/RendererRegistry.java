package studio.avis.renderer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RendererRegistry
 *
 * Register custom rendering-annotations and custom renderer instance here.
 * Rendering-annotations have to contain renderer() method which returns Class<\\? extends Renderer>.
 * Defining default return type is recommended if your rendering-annotation's
 * usage is so various or the annotation is full with expandability.
 */
public class RendererRegistry {

    private final Set<Class<? extends Annotation>> annotations = new HashSet<>();
    private final Set<Renderer> renderers = new HashSet<>();

    public void registerAnnotation(Class<? extends Annotation> annotation) {
        Optional<RuntimeException> handler = handleAnnotationAvailability(annotation);
        if(handler.isPresent()) {
            throw handler.get();
        }
        annotations.add(annotation);
    }

    public void registerAnnotations(Class<? extends Annotation>... annotations) {
        Optional<RuntimeException> handler = handleAnnotationAvailability(annotations);
        if(handler.isPresent()) {
            throw handler.get();
        }
        this.annotations.addAll(new ArrayList<>(Arrays.asList(annotations)));
    }

    private Optional<RuntimeException> handleAnnotationAvailability(Class<? extends Annotation>... annotations) {
        Multimap<AnnotationAvailability, Class<? extends Annotation>> availabilityMap = ArrayListMultimap.create();
        for(Class<? extends Annotation> annotation : annotations) {
            AnnotationAvailability availability = checkAnnotationAvailability(annotation);
            if(availability != AnnotationAvailability.AVAILABLE) {
                availabilityMap.put(availability, annotation);
            }
        }

        if(availabilityMap.isEmpty()) {
            return Optional.empty();
        } else {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for(AnnotationAvailability availability : availabilityMap.keySet()) {
                Collection<Class<? extends Annotation>> unavailableAnnotations = availabilityMap.get(availability);

                if(first) {
                    first = false;
                } else {
                    builder.append(", ");
                }

                String reason = "";
                switch (availability) {
                    case NO_DECLARED_METHOD:
                        reason = " " + (unavailableAnnotations.size() == 1 ? "doesn't" : "don't") + " have renderer() method";
                        break;

                    case ILLEGAL_RETURN_TYPE:
                        reason = "'s renderer() method does not return Class<? extends Renderer>";
                        break;
                }

                builder.append(unavailableAnnotations.stream().map(annotationType -> "@" + annotationType.getSimpleName()).collect(Collectors.joining(", ")) + reason);
            }
            return Optional.of(new IllegalArgumentException(builder.toString()));
        }
    }

    private AnnotationAvailability checkAnnotationAvailability(Class<? extends Annotation> annotation) {
        try {
            Method method = annotation.getDeclaredMethod("renderer");
            method.setAccessible(true);
            if(!method.getGenericReturnType().getTypeName().contains("java.lang.Class<? extends studio.avis.renderer.Renderer>")) {
                return AnnotationAvailability.ILLEGAL_RETURN_TYPE;
            }
        } catch (NoSuchMethodException e) {
            return AnnotationAvailability.NO_DECLARED_METHOD;
        }
        return AnnotationAvailability.AVAILABLE;
    }

    private enum AnnotationAvailability {

        AVAILABLE,
        NO_DECLARED_METHOD,
        ILLEGAL_RETURN_TYPE

    }

    public void registerRenderer(Renderer renderer) {
        renderers.add(renderer);
    }

    public void registerRenderers(Renderer... renderers) {
        this.renderers.addAll(new ArrayList<>(Arrays.asList(renderers)));
    }

    Set<Renderer> getRenderers() {
        return renderers;
    }

    Set<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }
}
