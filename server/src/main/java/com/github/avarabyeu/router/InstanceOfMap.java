package com.github.avarabyeu.router;

import com.google.common.collect.ForwardingMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Contains object as values and it's classes as keys
 * Extends default {@code {java.util.Map<Class<? extends T>, T>}} map with {@link #getInstanceOf(Class)} method
 * which first calls {@link #get(Object)} (and returns value if found) and after goes through entries and
 * tries to find values using {@link java.lang.Class#isAssignableFrom(Class)} construction
 *
 * @author Andrei Varabyeu
 */
public final class InstanceOfMap<T, V> extends ForwardingMap<Class<? extends T>, V> {

    private Map<Class<? extends T>, V> delegate;

    private InstanceOfMap(Map<Class<? extends T>, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Map<Class<? extends T>, V> delegate() {
        return delegate;
    }

    @Override
    public V put(@Nullable Class<? extends T> clazz, @Nullable V value) {
        return delegate.put(clazz, value);
    }

    public V getInstanceOf(Class<? extends T> clazz) {
        if (delegate.containsKey(clazz)) {
            return delegate.get(clazz);
        } else {
            return delegate.entrySet().stream()
                    .filter(entry -> clazz.isAssignableFrom(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        InstanceOfMap<?, ?> that = (InstanceOfMap<?, ?>) o;
        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delegate);
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    public static <T, V> Builder<T, V> builder() {
        return new Builder<>();
    }

    public static <T, V> InstanceOfMap<T, V> empty() {
        return new InstanceOfMap<>(Collections.emptyMap());
    }

    public static class Builder<T, V> {

        public InstanceOfMap<T, V> fromMap(Map<Class<? extends T>, V> map) {
            return new InstanceOfMap<>(map);
        }

    }

}
