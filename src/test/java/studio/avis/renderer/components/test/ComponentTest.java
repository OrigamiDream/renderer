package studio.avis.renderer.components.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.junit4.SpringRunner;
import studio.avis.renderer.RendererStudio;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;
import studio.avis.renderer.components.TextComponent;
import studio.avis.renderer.components.TranslatableComponent;
import studio.avis.renderer.test.RendererStudioTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ComponentTest {

    @SpringBootApplication
    @Import(RendererStudio.class)
    public static class RendererWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(RendererStudioTest.RendererWebApplication.class, args);
        }

    }

    @TestConfiguration
    public static class I18NConfigTest {

        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setDefaultEncoding("UTF-8");
            messageSource.setBasename("classpath:/i18n-test/messages");
            messageSource.setFallbackToSystemLocale(false);

            return messageSource;
        }

    }

    @Test
    public void textComponentTest() {
        BaseComponent baseComponent = new TextComponent("Hello World");

        Assert.assertEquals(baseComponent.toString(Locale.ENGLISH), "Hello World");
    }

    @Test
    public void translatableComponentTest() {
        BaseComponent baseComponent = new TranslatableComponent("junit.hello-world", "Dave", "WFINCO");

        Assert.assertEquals(baseComponent.toString(Locale.ENGLISH), "Hello Dave, to the WFINCO!");
    }

    @Test
    public void componentTest() {
        Component componentA = new Component("Hello World");
        Component componentB = new Component(new TextComponent("Hello World"));
        Component componentC = new Component(new Component("Hello"), new Component(" World"));
        Component componentD = new Component(new Component("Hello"), new TextComponent(" World"));
        Component componentE = new Component(new Component("Oh, "), new TranslatableComponent("junit.hello-world", "Dave", new TextComponent("World")));
        Component componentF = new Component(new Component("Oh, "), new TranslatableComponent("junit.hello-world", "Dave", new TranslatableComponent("junit.nickname")));

        Assert.assertEquals(componentA.toString(Locale.ENGLISH), "Hello World");
        Assert.assertEquals(componentB.toString(Locale.ENGLISH), "Hello World");
        Assert.assertEquals(componentC.toString(Locale.ENGLISH), "Hello World");
        Assert.assertEquals(componentD.toString(Locale.ENGLISH), "Hello World");
        Assert.assertEquals(componentE.toString(Locale.ENGLISH), "Oh, Hello Dave, to the World!");
        Assert.assertEquals(componentF.toString(Locale.ENGLISH), "Oh, Hello Dave, to the Nickname!");
    }

    @Test
    public void baseComponentTest() {
        BaseComponent component = new Component("Hello World");
        Assert.assertEquals(component.toString(Locale.ENGLISH), "Hello World");
        Assert.assertEquals(component.toString("en_US"), "Hello World");
    }

    @Test
    public void translatableComponentInvalidKey() {
        BaseComponent invalidKey = new TranslatableComponent("junit.helloworld");

        Assert.assertEquals(invalidKey.toString(Locale.ENGLISH), "junit.helloworld");
    }

    @Test
    public void baseComponentAddTest() {
        BaseComponent component = new Component("Hello");
        component.add(" World");

        Assert.assertEquals(component.toString(), "Hello World");

        component = new Component();
        component.addAll("Hello", " ", "World");

        Assert.assertEquals(component.toString(), "Hello World");
    }

    @Test
    public void baseComponentConstructorObjectTest() {
        Object a = "Hello";
        BaseComponent b = new Component(" World");

        BaseComponent component = new Component(a, b);

        Assert.assertEquals(component.toString(), "Hello World");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void textComponentAddTest() {
        BaseComponent component = new TextComponent("Hello");
        component.add(new Component(" World"));

        Assert.fail();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void textComponentAddAllVarargTest() {
        BaseComponent component = new TextComponent("Hello");
        component.addAll(new Component(" "), new Component("World"));

        Assert.fail();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void textComponentAddAllListTest() {
        List<BaseComponent> components = new ArrayList<>();
        components.add(new Component(" "));
        components.add(new Component("World"));

        BaseComponent component = new TextComponent("Hello");
        component.addAll(components);

        Assert.fail();
    }

    @Test
    public void translatableComponentExtraTest() {
        TranslatableComponent component = new TranslatableComponent("junit.hello-world", "John", "World");
        Object[] extras = component.getExtra();
        Assert.assertEquals(extras[0], "John");
        Assert.assertEquals(extras[1], "World");
    }
}
