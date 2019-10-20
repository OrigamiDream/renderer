package studio.avis.renderer.annotations;

import studio.avis.renderer.Renderer;
import studio.avis.renderer.impl.InputRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Input {

    String id() default "";

    /**
     * Override <input> form name if not null.
     */
    String name() default "";

    /**
     * Input type
     */
    String type() default "text";

    /**
     * Classes
     */
    String classappend() default "";

    /**
     * Placeholder
     */
    String placeholder() default "";

    /**
     * Default value when the request is empty.
     */
    String value() default "";

    /**
     * RADIO TYPE ONLY
     */
    boolean checked() default false;

    boolean readonly() default false;

    Class<? extends Renderer> renderer() default InputRenderer.class;

}
