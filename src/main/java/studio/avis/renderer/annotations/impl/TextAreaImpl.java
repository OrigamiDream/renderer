package studio.avis.renderer.annotations.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.annotations.TextArea;
import studio.avis.renderer.annotations.interfaces.ITextArea;

import java.lang.annotation.Annotation;

@AllArgsConstructor
@Setter
public class TextAreaImpl implements ITextArea {

    private String id;
    private String name;
    private String placeholder;
    private String classappend;
    private int rows;
    private String value;
    private Class<? extends Renderer> renderer;
    private Class<? extends Annotation> annotationType;

    public static TextAreaImpl from(TextArea textArea) {
        return new TextAreaImpl(textArea.id(), textArea.name(), textArea.placeholder(), textArea.classappend(), textArea.rows(), textArea.value(), textArea.renderer(), textArea.annotationType());
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
    public String placeholder() {
        return placeholder;
    }

    @Override
    public String classappend() {
        return classappend;
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public String value() {
        return value;
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
