package studio.avis.renderer.annotations.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.annotations.Option;
import studio.avis.renderer.annotations.Select;
import studio.avis.renderer.annotations.interfaces.ISelect;

import java.lang.annotation.Annotation;

@AllArgsConstructor
@Setter
public class SelectImpl implements ISelect {

    private String id;
    private String name;
    private String selected;
    private String classappend;
    private Option[] options;
    private Class<? extends Renderer> renderer;
    private Class<? extends Annotation> annotationType;

    public static SelectImpl from(Select select) {
        return new SelectImpl(select.id(), select.name(), select.selected(), select.classappend(), select.options(), select.renderer(), select.annotationType());
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
    public String selected() {
        return selected;
    }

    @Override
    public String classappend() {
        return classappend;
    }

    @Override
    public Option[] options() {
        return options;
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
