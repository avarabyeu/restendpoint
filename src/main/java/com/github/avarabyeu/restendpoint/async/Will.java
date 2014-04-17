package com.github.avarabyeu.restendpoint.async;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Useful wrapper around Google's {@link com.google.common.util.concurrent.ListenableFuture}
 *
 * @see {@link com.github.avarabyeu.restendpoint.async.Wills}
 * @param <T> Type of object to be returned
 * @author Andrey Varabyeu
 */
public interface Will<T> extends ListenableFuture<T> {

    /**
     * Blocks current thread until future object is availible or some exception thrown
     *
     * @return
     * @see {@link com.google.common.util.concurrent.ListenableFuture#isDone()}
     */
    T obtain();

    /**
     * Adds callback to future object. Will be executed if future object is availible
     *
     * @param action Action to be performed on future result
     * @return This object
     */
    Will<T> whenDone(Action<T> action);

    /**
     * Adds callback to future object. Will be executed if some exception is thrown
     *
     * @param action Action to be performed on future exception
     * @return This object
     */
    Will<T> whenFailed(Action<Throwable> action);

    /**
     * Adds {@link com.google.common.util.concurrent.FutureCallback} for future object.
     * Will be executed when future result is availible or some exception thrown
     *
     * @param callback {@link com.google.common.util.concurrent.ListenableFuture} callback
     * @return This object
     */
    Will<T> callback(FutureCallback<T> callback);

    /**
     * Creates new {@link com.github.avarabyeu.restendpoint.async.Will} containing transformed result of this {@link com.github.avarabyeu.restendpoint.async.Will} result using provided function
     *
     * @param function Transformation Function
     * @param <R>      Type of new Will
     * @return New Will
     */
    <R> Will<R> map(Function<? super T, ? extends R> function);

    /**
     * Creates new {@link com.github.avarabyeu.restendpoint.async.Will} containing transformed result of this {@link com.github.avarabyeu.restendpoint.async.Will} result using provided function
     *
     * @param function Will of transformation function
     * @param <R>      Type of new Will
     * @return New Will
     */
    <R> Will<R> flatMap(Function<? super T, Will<R>> function);

}
