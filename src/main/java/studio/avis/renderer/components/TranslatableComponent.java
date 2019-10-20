package studio.avis.renderer.components;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class TranslatableComponent implements BaseComponent {

    private final String key;
    private final Object[] extra;

    public TranslatableComponent(String key) {
        this.key = key;
        this.extra = new Object[0];
    }

    public TranslatableComponent(String key, Object... extra) {
        this.key = key;
        this.extra = extra;
    }

    public String getKey() {
        return key;
    }

    public Object[] getExtra() {
        return extra;
    }

    @Override
    public String toString(Locale locale) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Optional<WebApplicationContext> contextOptional = Optional.ofNullable(RequestContextUtils.findWebApplicationContext(httpServletRequest));

        if(contextOptional.isPresent()) {
            WebApplicationContext context = contextOptional.get();
            MessageSource messageSource = context.getBean(MessageSource.class);
            try {
                String message = messageSource.getMessage(getKey(), null, locale);
                if(message.isEmpty()) {
                    return key;
                }
                if(extra != null) {
                    for(int i = 0; i < extra.length; i++) {
                        Object object = extra[i];
                        if(object instanceof BaseComponent) {
                            message = message.replace("{" + i + "}", ((BaseComponent) object).toString(locale));
                        } else {
                            message = message.replace("{" + i + "}", object.toString());
                        }
                    }
                }
                return message;
            } catch(NoSuchMessageException e) {
                return key;
            }
        }
        return getKey();
    }

    @Override
    public String toString() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return toString(RequestContextUtils.getLocale(httpServletRequest));
    }

    @Override
    public BaseComponent add(String text) {
        throw new UnsupportedOperationException("TranslatableComponent is not mutable. Unsupported operation.");
    }

    @Override
    public BaseComponent addAll(String... texts) {
        throw new UnsupportedOperationException("TranslatableComponent is not mutable. Unsupported operation.");
    }

    @Override
    public BaseComponent add(BaseComponent component) {
        throw new UnsupportedOperationException("TranslatableComponent is not mutable. Unsupported operation.");
    }

    @Override
    public BaseComponent addAll(BaseComponent... components) {
        throw new UnsupportedOperationException("TranslatableComponent is not mutable. Unsupported operation.");
    }

    @Override
    public BaseComponent addAll(List<BaseComponent> components) {
        throw new UnsupportedOperationException("TranslatableComponent is not mutable. Unsupported operation.");
    }
}
