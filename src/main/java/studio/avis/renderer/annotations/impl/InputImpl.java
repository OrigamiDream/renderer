package studio.avis.renderer.annotations.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.annotations.interfaces.IInput;

import java.lang.annotation.Annotation;

@AllArgsConstructor
@Setter
public class InputImpl implements IInput {

    private String id;
    private String name;
    private String type;
    private String classappend;
    private String placeholder;
    private String value;
    private boolean checked;
    private boolean readonly;
    private Class<? extends Renderer> renderer;
    private Class<? extends Annotation> annotationType;

    public static InputImpl from(Input input) {
        return new InputImpl(input.id(), input.name(), input.type(), input.classappend(), input.placeholder(), input.value(), input.checked(), input.readonly(), input.renderer(), input.annotationType());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String classappend() {
        return classappend;
    }

    @Override
    public String placeholder() {
        return placeholder;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean checked() {
        return checked;
    }

    @Override
    public boolean readonly() {
        return readonly;
    }

    @Override
    public Class<? extends Renderer> renderer() {
        return renderer;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }
}
