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
}
