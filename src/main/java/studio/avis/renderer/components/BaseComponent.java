package studio.avis.renderer.components;

import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public interface BaseComponent {

    String toString(Locale locale);

    default String toString(HttpServletRequest httpServletRequest) {
        return toString(RequestContextUtils.getLocale(httpServletRequest));
    }

    default String toString(String locale) {
        return toString(new Locale(locale));
    }

    BaseComponent add(String text);

    BaseComponent addAll(String... texts);

    BaseComponent add(BaseComponent component);

    BaseComponent addAll(BaseComponent... components);

    BaseComponent addAll(List<BaseComponent> components);

}
