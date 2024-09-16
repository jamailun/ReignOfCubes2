package fr.jamailun.reignofcubes2.api.configuration;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Getter
public record PropertyObject(Object owner, Field field, PersistedProperty annotation) {

    public String section() {
        return annotation().section();
    }

    public String name() {
        return annotation().name();
    }

    public String path() {
        return (section().isEmpty() ? "" : section() + ".") + name();
    }

    public boolean is(Class<?> clazz) {
        return Objects.equals(clazz, field.getType());
    }

    public void set(@Nullable Object value) {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set value (" + owner + "." + field + " := " + value + ")", e);
        }
    }

    public Object get() {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get value (" + owner + "." + field + ")", e);
        }
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(get());
    }

    public <T> boolean isListOf(@NotNull Class<T> clazz) {
        return field.getType().equals(List.class) && annotation.type().equals(clazz);
    }

}
