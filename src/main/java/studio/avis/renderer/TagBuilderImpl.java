package studio.avis.renderer;

import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;
import studio.avis.renderer.components.TextComponent;
import studio.avis.renderer.components.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

class TagBuilderImpl implements TagBuilder {

    private static class AttributeValue {

        public String key;
        public BaseComponent value;
        public boolean allowEmptyAttributeValue;

    }

    private static class PairSelector<A, B> {
        private final A a;
        private final B b;

        public static <A, B> PairSelector<A, B> of(A a, B b) {
            return new PairSelector<>(a, b);
        }

        private PairSelector(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public void computeIfPresent(Consumer<A> consumerA, Consumer<B> consumerB) {
            if(a != null) {
                consumerA.accept(a);
            } else {
                consumerB.accept(b);
            }
        }

    }

    private final String tagName;
    private final boolean selfClosing;
    private final List<AttributeValue> attributes = new ArrayList<>();
    private final List<PairSelector<BaseComponent, TagBuilder>> innerTags = new ArrayList<>();

    TagBuilderImpl(String tagName) {
        this(tagName, false);
    }

    TagBuilderImpl(String tagName, boolean selfClosing) {
        this.tagName = tagName;
        this.selfClosing = selfClosing;
    }

    private BaseComponent checkTranslatable(String s) {
        if(s != null) {
            if(s.startsWith("{") && s.endsWith("}")) {
                return new TranslatableComponent(s.replace("{", "").replace("}", ""));
            } else {
                return new TextComponent(s);
            }
        }
        return null;
    }

    @Override
    public TagBuilder attribute(String attribute, String value, boolean allowEmptyAttributeValue) {
        return attribute(attribute, checkTranslatable(value), allowEmptyAttributeValue);
    }

    @Override
    public TagBuilder attribute(String attribute, BaseComponent value, boolean allowEmptyAttributeValue) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.key = attribute;
        attributeValue.value = value;
        attributeValue.allowEmptyAttributeValue = allowEmptyAttributeValue;
        attributes.add(attributeValue);
        return this;
    }

    @Override
    public TagBuilder text(String text) {
        return text(checkTranslatable(text));
    }

    @Override
    public TagBuilder text(BaseComponent text) {
        innerTags.add(PairSelector.of(text, null));
        return this;
    }

    @Override
    public TagBuilder html(TagBuilder builder) {
        innerTags.add(PairSelector.of(null, builder));
        return this;
    }

    @Override
    public BaseComponent build(RenderingAttribute renderingAttribute) {
        BaseComponent component = new Component();
        component.add(new TextComponent("<" + tagName));
        attributes.forEach(attributeValue -> {
            String value = Optional.ofNullable(attributeValue.value)
                    .map(c -> c.toString(renderingAttribute.getHttpServletRequest()))
                    .orElse(null);

            if((value != null && value.length() > 0) || !attributeValue.allowEmptyAttributeValue) {
                StringBuilder builder = new StringBuilder();
                builder.append(" ").append(attributeValue.key);
                if(value != null) {
                    builder.append("=\"").append(value).append("\"");
                }
                component.add(new TextComponent(builder.toString()));
            }
        });
        if(selfClosing) {
            component.add(new TextComponent(">"));
        } else {
            component.add(new TextComponent(">"));
            innerTags.forEach(selector -> selector.computeIfPresent(
                    component::add,
                    builder -> component.add(builder.build(renderingAttribute))));
            component.add(new TextComponent("</" + tagName + ">"));
        }
        return component;
    }

}
