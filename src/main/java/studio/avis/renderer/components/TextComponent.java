package studio.avis.renderer.components;

import java.util.List;
import java.util.Locale;

public class TextComponent implements BaseComponent {

    private final StringBuilder builder;

    public TextComponent(String text) {
        this.builder = new StringBuilder();
        add(text);
    }

    public TextComponent(String... texts) {
        this.builder = new StringBuilder();
        addAll(texts);
    }

    @Override
    public String toString(Locale locale) {
        return builder.toString();
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public BaseComponent add(String text) {
        builder.append(text);
        return this;
    }

    @Override
    public BaseComponent addAll(String... texts) {
        for(String text : texts) {
            add(text);
        }
        return this;
    }

    @Override
    public BaseComponent add(BaseComponent component) {
        throw new UnsupportedOperationException("TextComponent does only support literal types.");
    }

    @Override
    public BaseComponent addAll(BaseComponent... components) {
        throw new UnsupportedOperationException("TextComponent does only support literal types.");
    }

    @Override
    public BaseComponent addAll(List<BaseComponent> components) {
        throw new UnsupportedOperationException("TextComponent does only support literal types.");
    }

}
