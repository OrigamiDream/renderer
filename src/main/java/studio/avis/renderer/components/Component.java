package studio.avis.renderer.components;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class Component implements BaseComponent {

    private final List<BaseComponent> components;

    public Component() {
        this.components = new ArrayList<>();
    }

    public Component(String... text) {
        this.components = new ArrayList<>();
        addAll(Arrays.stream(text).map(TextComponent::new).collect(Collectors.toList()));
    }

    public Component(Object... objects) {
        this.components = new ArrayList<>();
        for(Object object : objects) {
            if(object instanceof BaseComponent) {
                add((BaseComponent) object);
            } else {
                add(new TextComponent(object.toString()));
            }
        }
    }

    public Component(List<BaseComponent> components) {
        this.components = new ArrayList<>();
        addAll(components);
    }

    public Component(BaseComponent component) {
        this(new ArrayList<>());
        add(component);
    }

    public Component(BaseComponent... components) {
        this(new ArrayList<>());
        addAll(components);
    }

    @Override
    public BaseComponent add(String text) {
        this.components.add(new TextComponent(text));
        return this;
    }

    @Override
    public BaseComponent addAll(String... texts) {
        this.components.add(new TextComponent(texts));
        return this;
    }

    @Override
    public BaseComponent add(BaseComponent component) {
        this.components.add(component);
        return this;
    }

    @Override
    public BaseComponent addAll(BaseComponent... components) {
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    @Override
    public BaseComponent addAll(List<BaseComponent> components) {
        this.components.addAll(components);
        return this;
    }

    @Override
    public String toString(Locale locale) {
        StringBuilder builder = new StringBuilder();
        components.forEach(component -> builder.append(component.toString(locale)));
        return builder.toString();
    }

    @Override
    public String toString() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return toString(RequestContextUtils.getLocale(httpServletRequest));
    }
}
