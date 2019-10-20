package studio.avis.renderer.annotations;

import studio.avis.renderer.Renderer;
import studio.avis.renderer.impl.SelectRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {

    String id() default "";

    /**
     * Override <input> form name if not null.
     */
    String name() default "";

    /**
     * Default value when the request is empty.
     */
    String selected() default "";

    /**
     * Classes
     */
    String classappend() default "";

    /**
     * Options for select
     */
    Option[] options() default {};

    Class<? extends Renderer> renderer() default SelectRenderer.class;

}
