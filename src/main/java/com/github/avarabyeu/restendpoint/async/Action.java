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
