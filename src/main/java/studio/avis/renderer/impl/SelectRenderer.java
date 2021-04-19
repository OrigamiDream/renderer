package studio.avis.renderer.impl;

import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.TagBuilder;
import studio.avis.renderer.annotations.Option;
import studio.avis.renderer.annotations.Select;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.TranslatableComponent;

import java.util.Arrays;
import java.util.List;

public class SelectRenderer implements Renderer<Object> {

    public static final String SELECT_ATTRIBUTE = InputRenderer.class.getName() + ".attribute";
    public static final String SELECT_ATTRIBUTE_OPTIONS = SelectRenderer.class.getName() + ".attribute.options";

    @Override
    public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, Object component, RendererField field) {
        Select select;
        if(attribute.contains(SELECT_ATTRIBUTE)) {
            select = attribute.get(SELECT_ATTRIBUTE);
        } else {
            select = field.annotated(Select.class);
        }

        String name = chooseOr(field.getFieldName(), select.name());

        TagBuilder parent = TagBuilder.builder("select");

        parent.attribute("name", name)
                .attribute("id", select.id(), true)
                .attribute("class", select.classappend(), true);

        List<Option> options;
        if(attribute.contains(SELECT_ATTRIBUTE_OPTIONS)) {
            options = attribute.get(SELECT_ATTRIBUTE_OPTIONS);
        } else {
            options = Arrays.asList(select.options());
        }

        for(Option option : options) {
            TagBuilder child = TagBuilder.builder("option");
            child.attribute("value", option.value());
            if(option.disabled()) {
                child.attribute("disabled");
            }

            String value = field.getValue().toString();
            if(value == null || value.isEmpty()) {
                value = select.selected();
            }
            if(value.equalsIgnoreCase(option.value())) {
                child.attribute("selected");
            }
            child.text(new TranslatableComponent(option.text()));
            parent.html(child);
        }
        return parent.build(attribute);
    }

}
