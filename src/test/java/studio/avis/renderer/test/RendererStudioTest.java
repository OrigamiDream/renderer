package studio.avis.renderer.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import studio.avis.renderer.ComponentRenderer;
import studio.avis.renderer.RendererStudio;
import studio.avis.renderer.RendererStudioConfig;
import studio.avis.renderer.interceptors.BaseComponentInterceptor;
import studio.avis.renderer.interceptors.BindingResultInterceptor;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RendererStudioTest {

    @SpringBootApplication
    @Import(RendererStudio.class)
    public static class RendererWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(RendererWebApplication.class, args);
        }

    }

    @Autowired
    private RendererStudio rendererStudio;

    @Autowired
    private RendererStudioConfig rendererStudioConfig;

    @Autowired
    private BaseComponentInterceptor baseComponentInterceptor;

    @Autowired
    private BindingResultInterceptor bindingResultInterceptor;

    @Autowired
    private ComponentRenderer componentRenderer;

    @Test
    public void rendererStudioTest() {
        Assert.notNull(rendererStudio, "RendererStudio is not null.");
    }

    @Test
    public void rendererStudioConfigTest() {
        Assert.notNull(rendererStudioConfig, "RendererStudioConfig is not null.");
    }

    @Test
    public void baseComponentInterceptorTest() {
        Assert.notNull(baseComponentInterceptor, "BaseComponentInterceptor is not null.");
    }

    @Test
    public void bindingResultInterceptorTest() {
        Assert.notNull(bindingResultInterceptor, "BindingResultInterceptor is not null.");
    }

    @Test
    public void componentRendererTest() {
        Assert.notNull(componentRenderer, "ComponentRenderer is not null.");
    }

}
