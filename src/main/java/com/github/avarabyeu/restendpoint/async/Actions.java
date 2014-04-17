package com.github.avarabyeu.restendpoint.async;

/**
 * Set of utilities to simplify working with {@link com.github.avarabyeu.restendpoint.async.Action}
 *
 * @author Andrei Varabyeu
 */
public final class Actions {

    private Actions() {
        //statics only
    }

    /**
     * Object-type Action which does nothing. Just a holder when we need to provide NOP action
     */
    private static Action<Object> NOTHING = new Action<Object>() {
        public void apply(Object a) {
        }
    };

    /**
     * Casts {@link #NOTHING} to needed type
     *
     * @param <E> Type of Action to be returned
     * @return
     */
    public static <E> Action<E> nothing() {
        @SuppressWarnings("unchecked")
        Action<E> result = (Action<E>) NOTHING;
        return result;
    }
}
