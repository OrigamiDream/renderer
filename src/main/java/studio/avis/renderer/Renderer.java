package studio.avis.renderer;

import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.TextComponent;
import studio.avis.renderer.components.TranslatableComponent;

/**
 * Renderer
 *
 * Generic T have to be HTML form request instance assembled by Spring.
 *
 * Create BaseComponent what contains generated HTML markup code which
 * attributed from a rendering-annotation from HTML form request instance.
 */
public interface Renderer<T> {

    BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, T component, RendererField field);

    /**
     * Returns "b" if "a" is null.
     */
    default <T> T chooseOr(T a, T b) {
        return a != null ? a : b;
    }

    /**
     * Returns "b" if "a" is null or empty (zero-length).
     */
    default String chooseOr(String a, String b) {
        return a != null && a.length() > 0 ? a : b;
    }

}
