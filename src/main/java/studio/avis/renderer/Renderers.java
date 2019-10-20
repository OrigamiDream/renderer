package studio.avis.renderer;

import com.google.common.collect.ImmutableList;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.annotations.Select;
import studio.avis.renderer.annotations.TextArea;
import studio.avis.renderer.impl.InputRenderer;
import studio.avis.renderer.impl.SelectRenderer;
import studio.avis.renderer.impl.TextAreaRenderer;

import java.lang.annotation.Annotation;
import java.util.List;

public class Renderers {

    public static final List<Class<? extends Annotation>> ANNOTATIONS = ImmutableList.<Class<? extends Annotation>>builder()
            .add(Input.class)
            .add(TextArea.class)
            .add(Select.class)
            .build();

    private static final List<Renderer> RENDERERS = ImmutableList.<Renderer>builder()
            .add(new InputRenderer())
            .add(new TextAreaRenderer())
            .add(new SelectRenderer())
            .build();

    private Renderers() {
    }

    public static List<Renderer> getRenderers() {
        return RENDERERS;
    }

    public static <T extends Renderer> T getRenderer(Class<T> rendererType) {
        for(Renderer renderer : RENDERERS) {
            if(rendererType.isInstance(renderer)) {
                return (T) renderer;
            }
        }
        return null;
    }

}
