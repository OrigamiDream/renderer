package studio.avis.renderer.impl;

import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.annotations.Option;
import studio.avis.renderer.annotations.Select;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;
import studio.avis.renderer.components.TextComponent;
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

        BaseComponent baseComponent = new Component();
        baseComponent.add(new TextComponent("<select"));

        addAttribute(baseComponent, "name", name);
        addAttribute(baseComponent, "id", select.id(), true);
        addAttribute(baseComponent, "class", select.classappend(), true);

        baseComponent.add(new TextComponent(">"));

        List<Option> options;
        if(attribute.contains(SELECT_ATTRIBUTE_OPTIONS)) {
            options = attribute.get(SELECT_ATTRIBUTE_OPTIONS);
        } else {
            options = Arrays.asList(select.options());
        }

        for(Option option : options) {
            Component optionComponent = new Component("<option");
            addAttribute(optionComponent, "value", option.value());
            if(option.disabled()) {
                addAttribute(optionComponent, "disabled");
            }

            String value = field.getValue().toString();
            if(value == null || value.isEmpty()) {
                value = select.selected();
            }
            if(value.equalsIgnoreCase(option.value())) {
                addAttribute(optionComponent, "selected");
            }
            optionComponent.add(">").add(new TranslatableComponent(option.text())).add("</option>");

            baseComponent.add(optionComponent);
        }

        baseComponent.add(new TextComponent("</select>"));
        return baseComponent;
    }

}
