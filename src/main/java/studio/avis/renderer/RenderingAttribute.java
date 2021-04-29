package studio.avis.renderer;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class RenderingAttribute {

    private final HttpServletRequest httpServletRequest;
    private final Map<Object, Object> attributes;

    public RenderingAttribute(HttpServletRequest httpServletRequest, Map<Object, Object> attributes) {
        this.httpServletRequest = httpServletRequest;
        this.attributes = new HashMap<>(attributes);
    }

    public <T> T get(Object key) {
        return (T) attributes.get(key);
    }

    public boolean contains(Object key) {
        return attributes.containsKey(key);
    }

    public <T> T get(Object key, Class<T> type) {
        return get(key);
    }

    public <T> T put(Object key, Object value) {
        return (T) attributes.put(key, value);
    }

}
