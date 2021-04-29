package studio.avis.renderer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class RendererField {

    private final Field field;
    private final Object value;

    public RendererField(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public <T extends Annotation> T annotated(Class<T> annotationType) {
        return field.getAnnotation(annotationType);
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getFieldType() {
        return field.getType();
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> type) {
        return (T) value;
    }
}
