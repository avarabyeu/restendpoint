/*
 * Copyright (C) 2014 Andrei Varabyeu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
