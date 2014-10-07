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

package com.github.avarabyeu.restendpoint.http;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Guice Injector for testing purposes<br>
 * Simple Singleton implementation
 * 
 * @author Andrei Varabyeu
 * 
 */
public class Injector {

	/** Guice Injector */
	private com.google.inject.Injector injector;

	private static Injector instance;

	private Injector() {
		injector = Guice.createInjector(new GuiceTestModule());
	}

	public static synchronized Injector getInstance() {
		return null == instance ? instance = new Injector() : instance;
	}

	public <T> T getBean(Class<T> type) {
		return injector.getInstance(type);
	}

    public <T> T getBean(String name, Class<T> type) {
        return getBean(Key.get(type, Names.named(name)));
    }

	public <T> T getBean(Key<T> key) {
        return injector.getInstance(key);
	}

    public void injectMembers(Object object){
        this.injector.injectMembers(object);
    }
}
