package studio.avis.renderer;

import studio.avis.renderer.components.BaseComponent;

public interface TagBuilder {

    static TagBuilder builder(String tagName) {
        return new TagBuilderImpl(tagName);
    }

    static TagBuilder builder(String tagName, boolean selfClosing) {
        return new TagBuilderImpl(tagName, selfClosing);
    }

    default TagBuilder attribute(String attribute) {
        return attribute(attribute, (String) null);
    }

    default TagBuilder attribute(String attribute, String value) {
        return attribute(attribute, value, false);
    }

    TagBuilder attribute(String attribute, String value, boolean allowEmptyAttributeValue);

    default TagBuilder attribute(String attribute, BaseComponent value) {
        return attribute(attribute, value, false);
    }

    TagBuilder attribute(String attribute, BaseComponent value, boolean allowEmptyAttributeValue);

    TagBuilder text(String text);

    TagBuilder text(BaseComponent text);

    TagBuilder html(TagBuilder builder);

    BaseComponent build(RenderingAttribute renderingAttribute);

}
