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

    default void addAttribute(BaseComponent baseComponent, String attribute) {
        addAttribute(baseComponent, attribute, null, false);
    }

    default void addAttribute(BaseComponent baseComponent, String attribute, String value) {
        addAttribute(baseComponent, attribute, value, false);
    }

    default void addAttribute(BaseComponent baseComponent, String attribute, BaseComponent value, RenderingAttribute renderingAttribute) {
        addAttribute(baseComponent, attribute, value, false, renderingAttribute);
    }

    default void addAttribute(BaseComponent baseComponent, String attribute, BaseComponent value, boolean limited, RenderingAttribute renderingAttribute) {
        addAttribute(baseComponent, attribute, value.toString(renderingAttribute.getHttpServletRequest()), limited);
    }

    default BaseComponent parseExpression(String input) {
        if(input.startsWith("{") && input.endsWith("}")) {
            return new TranslatableComponent(input.replace("{", "").replace("}", ""));
        } else {
            return new TextComponent(input);
        }
    }

    default void addAttribute(BaseComponent baseComponent, String attribute, String value, boolean limited) {
        if((value != null && value.length() > 0) || !limited) {
            StringBuilder builder = new StringBuilder();
            builder.append(" ").append(attribute);
            if(value != null) {
                builder.append("=\"").append(value).append("\"");
            }
            baseComponent.add(new TextComponent(builder.toString()));
        }
    }
}
