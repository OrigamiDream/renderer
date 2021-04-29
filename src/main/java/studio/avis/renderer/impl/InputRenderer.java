package studio.avis.renderer.impl;

import org.springframework.web.multipart.MultipartFile;
import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.TagBuilder;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.components.BaseComponent;

import java.util.Optional;

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
        TagBuilder builder = TagBuilder.builder("input", true);

        builder.attribute("name", name)
                .attribute("id", input.id(), true)
                .attribute("type", input.type())
                .attribute("placeholder", input.placeholder(), true)
                .attribute("class", input.classappend(), true);

        if(input.checked()) {
            builder.attribute("checked");
        }
        if(input.readonly()) {
            builder.attribute("readonly");
        }

        if(field.getFieldType() != MultipartFile.class) {
            String value = Optional.ofNullable(field.getValue())
                    .map(Object::toString)
                    .orElse(null);

            if(value == null || value.isEmpty() || input.type().equalsIgnoreCase("radio") || input.type().equalsIgnoreCase("checkbox")) {
                value = input.value();
            }
            builder.attribute("value", value);
        }
        return builder.build(attribute);
    }

}
