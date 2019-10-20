package studio.avis.renderer.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import studio.avis.renderer.components.BaseComponent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class BaseComponentInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Map<String, Object> attributeReplacements = new HashMap<>();
        Enumeration<String> attributeNames = request.getAttributeNames();
        while(attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attribute = request.getAttribute(attributeName);
            if(attribute instanceof BaseComponent) {
                attributeReplacements.put(attributeName, ((BaseComponent) attribute).toString(request));
            }
        }
        attributeReplacements.forEach(request::setAttribute);

        if(modelAndView != null) {
            Map<String, Object> replacements = new HashMap<>();
            for(Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
                if(entry.getValue() instanceof BaseComponent) {
                    replacements.put(entry.getKey(), ((BaseComponent) entry.getValue()).toString(request));
                }
            }
            modelAndView.getModel().putAll(replacements);
        }
    }

}
