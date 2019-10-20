package studio.avis.renderer.annotations;

import studio.avis.renderer.Renderer;
import studio.avis.renderer.impl.TextAreaRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TextArea {

    String id() default "";

    /**
     * Override <textarea> form name if not null.
     */
    String name() default "";

    /**
     * Placeholder
     */
    String placeholder() default "";

    /**
     * Classes
     */
    String classappend() default "";

    /**
     * Rows
     */
    int rows() default -1;

    /**
     * Default value when the request is empty.
     */
    String value() default "";

    Class<? extends Renderer> renderer() default TextAreaRenderer.class;

}
