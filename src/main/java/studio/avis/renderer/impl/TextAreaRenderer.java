package studio.avis.renderer.impl;

import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.TagBuilder;
import studio.avis.renderer.annotations.TextArea;
import studio.avis.renderer.components.BaseComponent;

public class TextAreaRenderer implements Renderer<Object> {

    public static final String TEXT_AREA_ATTRIBUTE = TextAreaRenderer.class.getName() + ".attribute";

    @Override
    public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, Object component, RendererField field) {
        TextArea textArea;
        if(attribute.contains(TEXT_AREA_ATTRIBUTE)) {
            textArea = attribute.get(TEXT_AREA_ATTRIBUTE);
        } else {
            textArea = field.annotated(TextArea.class);
        }

        String name = textArea.name().isEmpty() ? field.getFieldName() : textArea.name();

        TagBuilder builder = TagBuilder.builder("textarea");

        builder.attribute("name", name)
                .attribute("id", textArea.id(), true)
                .attribute("placeholder", textArea.placeholder(), true)
                .attribute("class", textArea.classappend(), true);

        if(textArea.rows() != -1) {
            builder.attribute("rows", String.valueOf(textArea.rows()));
        }

        String value = field.getValue(String.class);
        if(value == null || value.isEmpty()) {
            value = textArea.value();
        }
        builder.text(value);
        return builder.build(attribute);
    }

}
