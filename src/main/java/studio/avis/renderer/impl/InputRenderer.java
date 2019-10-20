package studio.avis.renderer.impl;

import org.springframework.web.multipart.MultipartFile;
import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;
import studio.avis.renderer.components.TextComponent;

public class InputRenderer implements Renderer<Object> {

    public static final String INPUT_ATTRIBUTE = InputRenderer.class.getName() + ".attribute";

    @Override
    public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, Object component, RendererField field) {
        Input input;
        if(attribute.contains(INPUT_ATTRIBUTE)) {
            input = attribute.get(INPUT_ATTRIBUTE);
        } else {
            input = field.annotated(Input.class);
        }

        String name = input.name().isEmpty() ? field.getFieldName() : input.name();

        BaseComponent baseComponent = new Component();
        baseComponent.add(new TextComponent("<input"));

        addAttribute(baseComponent, "name", name);
        addAttribute(baseComponent, "id", input.id(), true);
        addAttribute(baseComponent, "type", input.type());
        addAttribute(baseComponent, "placeholder", parseExpression(input.placeholder()), true, attribute);
        addAttribute(baseComponent, "class", input.classappend(), true);
        if(input.checked()) {
            addAttribute(baseComponent, "checked");
        }
        if(input.readonly()) {
            addAttribute(baseComponent, "readonly");
        }

        if(field.getFieldType() != MultipartFile.class) {
            String value = field.getValue().toString();
            if(value == null || value.isEmpty() || input.type().equalsIgnoreCase("radio") || input.type().equalsIgnoreCase("checkbox")) {
                value = input.value();
            }
            addAttribute(baseComponent, "value", value);
        }
        baseComponent.add(new TextComponent(">"));
        return baseComponent;
    }

}
