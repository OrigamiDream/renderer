package studio.avis.renderer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import studio.avis.renderer.interceptors.BaseComponentInterceptor;
import studio.avis.renderer.interceptors.BindingResultInterceptor;

@Configuration
public class RendererStudioConfig extends WebMvcConfigurerAdapter {

    private final BindingResultInterceptor bindingResultInterceptor;
    private final BaseComponentInterceptor baseComponentInterceptor;

    public RendererStudioConfig(BindingResultInterceptor bindingResultInterceptor, BaseComponentInterceptor baseComponentInterceptor) {
        this.bindingResultInterceptor = bindingResultInterceptor;
        this.baseComponentInterceptor = baseComponentInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bindingResultInterceptor).addPathPatterns("/**");
        registry.addInterceptor(baseComponentInterceptor).addPathPatterns("/**");
    }
}
