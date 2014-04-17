package com.github.avarabyeu.restendpoint.async;

/**
 * Representation of some action which may be applied on object
 *
 * @param <T> Type of object action may be applied to
 * @author Andrei Varabyeu
 */
public interface Action<T> {

    /**
     * Applies some action on provided object
     *
     * @param t Object action will be applied to
     */
    void apply(T t);

}
