package studio.avis.renderer.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererRegistry;
import studio.avis.renderer.RendererStudio;
import studio.avis.renderer.impl.InputRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RendererRegistryTest {

    @SpringBootApplication
    @Import(RendererStudio.class)
    public static class RendererWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(RendererStudioTest.RendererWebApplication.class, args);
        }

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoSuchRendererAnnotation {

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface OtherNoSuchRendererAnnotation {

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IllegalReturnTypeAnnotation {

        int renderer() default 0;

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface OtherIllegalReturnTypeAnnotation {

        int renderer() default 0;

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LegalAnnotation {

        Class<? extends Renderer> renderer() default InputRenderer.class;

    }

    @Test
    public void rendererRegistryTest() {
        RendererRegistry registry = new RendererRegistry();

        assertRegistration(() -> {
            registry.registerAnnotations(LegalAnnotation.class);
        }, null);

        assertRegistration(() -> {
            registry.registerAnnotation(IllegalReturnTypeAnnotation.class);
        }, "@IllegalReturnTypeAnnotation's renderer() method does not return Class<? extends Renderer>");

        assertRegistration(() -> {
            registry.registerAnnotations(IllegalReturnTypeAnnotation.class, OtherIllegalReturnTypeAnnotation.class);
        }, "@IllegalReturnTypeAnnotation, @OtherIllegalReturnTypeAnnotation's renderer() method does not return Class<? extends Renderer>");

        assertRegistration(() -> {
            registry.registerAnnotations(NoSuchRendererAnnotation.class);
        }, "@NoSuchRendererAnnotation doesn't have renderer() method");

        assertRegistration(() -> {
            registry.registerAnnotations(NoSuchRendererAnnotation.class, OtherNoSuchRendererAnnotation.class);
        }, "@NoSuchRendererAnnotation, @OtherNoSuchRendererAnnotation don't have renderer() method");
    }

    private void assertRegistration(Runnable runnable, String to) {
        try {
            runnable.run();
            if(to == null) {
                Assert.assertTrue(true);
            }
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), to);
        }
    }
}
