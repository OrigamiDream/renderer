package studio.avis.renderer.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class BindingResultInterceptor implements HandlerInterceptor {

    public static final String BINDING_RESULT_KEY = BindingResultInterceptor.class.getName() + ".key";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        List<BindingResult> bindingResults = new ArrayList<>();
        if(modelAndView != null) {
            for(Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
                if(entry.getKey().startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                    bindingResults.add((BindingResult) entry.getValue());
                }
            }
        }
        request.setAttribute(BINDING_RESULT_KEY, bindingResults);
    }
}
