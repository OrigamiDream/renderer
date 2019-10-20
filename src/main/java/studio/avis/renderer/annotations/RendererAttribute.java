package studio.avis.renderer.annotations;

import java.lang.annotation.*;

@Repeatable(RendererAttributes.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RendererAttribute {

    String key();

    Class<?> valueType() default String.class;

    String textValue() default "";

    boolean booleanValue() default false;

    int intValue() default 0;

    double doubleValue() default 0d;

    float floatValue() default 0f;

}
