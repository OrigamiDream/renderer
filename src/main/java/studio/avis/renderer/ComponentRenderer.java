package studio.avis.renderer;

import studio.avis.renderer.components.BaseComponent;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ComponentRenderer {

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName) {
        return render(httpServletRequest, component, fieldName, new HashMap<>(), null, null);
    }

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Map<Object, Object> attributes) {
        return render(httpServletRequest, component, fieldName, attributes, null, null);
    }

    BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Class<? extends Annotation> specificAnnotation);

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        return render(httpServletRequest, component, fieldName, new HashMap<>(), specificAnnotation, specificRendererType);
    }

    BaseComponent render(HttpServletRequest httpServletRequest, Object component, String fieldName, @Nullable Map<Object, Object> attributes, Class<? extends Annotation> specificAnnotation, Class<? extends Renderer> specificRendererType);

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field) {
        return render(httpServletRequest, component, field, new HashMap<>(), null, null);
    }

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Map<Object, Object> attributes) {
        return render(httpServletRequest, component, field, attributes, null, null);
    }

    BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Class<? extends Annotation> specificAnnotation);

    default BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType) {
        return render(httpServletRequest, component, field, new HashMap<>(), specificAnnotation, specificRendererType);
    }

    BaseComponent render(HttpServletRequest httpServletRequest, Object component, Field field, @Nullable Map<Object, Object> attributes, @Nullable Class<? extends Annotation> specificAnnotation, @Nullable Class<? extends Renderer> specificRendererType);

    default List<BaseComponent> render(HttpServletRequest httpServletRequest, Object component) {
        return render(httpServletRequest, component, new HashMap<>());
    }

    List<BaseComponent> render(HttpServletRequest httpServletRequest, Object component, @Nullable Map<Object, Object> attributes);


}