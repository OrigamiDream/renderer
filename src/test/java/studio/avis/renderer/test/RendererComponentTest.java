package studio.avis.renderer.test;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.Renderer;
import studio.avis.renderer.RendererField;
import studio.avis.renderer.RendererStudio;
import studio.avis.renderer.RenderingAttribute;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.annotations.TextArea;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RendererComponentTest {

    @SpringBootApplication
    @Import(RendererStudio.class)
    public static class RendererComponentTestWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(RendererComponentTestWebApplication.class, args);
        }

    }

    @Data
    static class DuplicatedRendererAnnotationRequestTest {

        @Input(id = "email", placeholder = "Enter email here.")
        @TextArea(id = "contents")
        private String email;

    }

    @Autowired
    private ComponentRenderer renderer;

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatedRendererAnnotationRequest() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        DuplicatedRendererAnnotationRequestTest request = new DuplicatedRendererAnnotationRequestTest();
        renderer.render(httpServletRequest, request, "email");

        Assert.fail();
    }

    @Data
    static class InvalidFieldNameRequestTest {

        @TextArea(id = "contents")
        private String email;

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFieldNameRequest() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        InvalidFieldNameRequestTest request = new InvalidFieldNameRequestTest();
        renderer.render(httpServletRequest, request, "contents");

        Assert.fail();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NoRendererAnnotation {

    }

    static class NoRendererAnnotationRequestTest {

        @NoRendererAnnotation
        private String sampleField;

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoRendererAnnotation() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        NoRendererAnnotationRequestTest request = new NoRendererAnnotationRequestTest();
        renderer.render(httpServletRequest, request, "sampleField");

        Assert.fail();
    }

    static class NoSpecificAnnotationRequestTest {

        @TextArea(id = "contents")
        private String email;

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSpecificAnnotationRequest() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        NoSpecificAnnotationRequestTest request = new NoSpecificAnnotationRequestTest();
        renderer.render(httpServletRequest, request, "email", Input.class);

        Assert.fail();
    }

    static class InvalidRendererRequestTest {

        @InvalidRendererAnnotation
        private String sample;

    }

    static class InvalidRenderer implements Renderer<Object> {

        private final int num;

        public InvalidRenderer(int i) {
            this.num = i;
        }

        @Override
        public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, Object component, RendererField field) {
            return new Component("<p>INVALID RENDERER #" + num + "</p>");
        }

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface InvalidRendererAnnotation {

        Class<? extends Renderer> renderer() default InvalidRenderer.class;

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRendererRequest() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        InvalidRendererRequestTest request = new InvalidRendererRequestTest();
        renderer.render(httpServletRequest, request, "sample");

        Assert.fail();
    }

    static class RendererWithBeanRequestTest {

        @RendererWithBeanAnnotation
        private String sample;

    }

    static class RendererWithBean implements Renderer<Object> {

        private final MessageSource source;

        RendererWithBean(MessageSource source) {
            this.source = source;
        }

        @Override
        public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, Object component, RendererField field) {
            return new Component("<p>INVALID RENDERER: " + source.getMessage("junit.nickname", null, attribute.getHttpServletRequest().getLocale()) + "</p>");
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RendererWithBeanAnnotation {

        Class<? extends Renderer> renderer() default RendererWithBean.class;

    }

    @Test
    public void testInvalidRendererRequestWithValidBean() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        RendererWithBeanRequestTest request = new RendererWithBeanRequestTest();
        BaseComponent component = renderer.render(httpServletRequest, request, "sample");

        Assert.assertEquals(component.toString(Locale.ENGLISH), "<p>INVALID RENDERER: Nickname</p>");
    }

}
