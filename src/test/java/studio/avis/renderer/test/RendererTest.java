package studio.avis.renderer.test;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import studio.avis.renderer.*;
import studio.avis.renderer.annotations.Input;
import studio.avis.renderer.annotations.Option;
import studio.avis.renderer.annotations.Select;
import studio.avis.renderer.annotations.TextArea;
import studio.avis.renderer.components.BaseComponent;
import studio.avis.renderer.components.Component;
import studio.avis.renderer.components.TextComponent;
import studio.avis.renderer.components.TranslatableComponent;
import studio.avis.renderer.impl.InputRenderer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RendererTest {

    @SpringBootApplication
    @Import(RendererStudio.class)
    public static class RendererWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(RendererStudioTest.RendererWebApplication.class, args);
        }

    }

    @Data
    class RendererRequest {

        @Input(id = "id_email", value = "Enter email here.")
        private String email = "";

        @TextArea(id = "id_textarea")
        private String contents = "Enter contents here.";

        @Select(selected = "BTC", options = {
                @Option(text = "Choose desired coin", value = "", disabled = true),
                @Option(text = "Bitcoin", value = "BTC"),
                @Option(text = "Ethereum", value = "ETH")
        })
        private String coinType = "";

        @Input(id = "id_nickname", renderer = TableRowRenderer.class)
        private String nickname = "";

        @Input(id = "id_displayName", renderer = DisplayNameRenderer.class)
        private String displayName = "OrigamiDream";

        private boolean displayNameDuplicationValidation = false;

    }

    static class DisplayNameRenderer implements Renderer<RendererRequest> {

        @Override
        public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, RendererRequest component, RendererField field) {
            BaseComponent rendered = renderer.render(attribute.getHttpServletRequest(), component, field.getField(), Input.class);
            BaseComponent button = new Component("<button");
            String textKey;
            if(component.isDisplayNameDuplicationValidation()) {
                addAttribute(button, "class", "success");
                textKey = "junit.button.available";
            } else {
                addAttribute(button, "class", "blue");
                textKey = "junit.button.available.check";
            }
            button.addAll(new TextComponent(">"), new TranslatableComponent(textKey), new TextComponent("</button>"));
            return new Component(rendered, new TextComponent(" "), button);
        }

    }

    static class TableRowRenderer implements Renderer<RendererRequest> {

        @Override
        public BaseComponent render(ComponentRenderer renderer, RenderingAttribute attribute, RendererRequest component, RendererField field) {
            BaseComponent rendered = renderer.render(attribute.getHttpServletRequest(), component, field.getField(), Input.class, InputRenderer.class);
            BaseComponent baseComponent = new Component();
            baseComponent.addAll(new TextComponent("<tr>"),
                                 new Component(new TextComponent("<th scope=\"row\">"), new TranslatableComponent(attribute.getAttributes().get("title").toString()), new TextComponent("</th>")),
                                 new TextComponent("<td>"), rendered, new TextComponent("</td>"),
                                 new TextComponent("</tr>"));
            return baseComponent;
        }
    }

    @TestConfiguration
    public static class RendererConfigTest {

        @Bean
        public RendererRegistry rendererRegistry() {
            RendererRegistry registry = new RendererRegistry();
            registry.registerRenderer(new TableRowRenderer());
            registry.registerRenderer(new DisplayNameRenderer());
            return registry;
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

    @Autowired
    private ComponentRenderer componentRenderer;

    @Test
    public void rendererTest() {
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();

        RendererRequest rendererRequest = new RendererRequest();
        List<BaseComponent> baseComponents = componentRenderer.render(httpServletRequest, rendererRequest, ImmutableMap.builder().put("title", "junit.nickname").build());

        Assert.assertArrayEquals(baseComponents.stream().map(component -> component.toString(Locale.ENGLISH)).toArray(), new Object[] {
                "<input name=\"email\" id=\"id_email\" type=\"text\" value=\"Enter email here.\">",
                "<textarea name=\"contents\" id=\"id_textarea\">Enter contents here.</textarea>",
                "<select name=\"coinType\"><option value=\"\" disabled>Choose desired coin</option><option value=\"BTC\" selected>Bitcoin</option><option value=\"ETH\">Ethereum</option></select>",
                "<tr><th scope=\"row\">Nickname</th><td><input name=\"nickname\" id=\"id_nickname\" type=\"text\" value=\"\"></td></tr>",
                "<input name=\"displayName\" id=\"id_displayName\" type=\"text\" value=\"OrigamiDream\"> <button class=\"blue\">Check Availability</button>"
        });

        rendererRequest.setDisplayNameDuplicationValidation(true);
        BaseComponent displayNameComponent = componentRenderer.render(httpServletRequest, rendererRequest, "displayName");

        Assert.assertEquals(displayNameComponent.toString(Locale.KOREAN), "<input name=\"displayName\" id=\"id_displayName\" type=\"text\" value=\"OrigamiDream\"> <button class=\"success\">사용 가능</button>");
    }
}
