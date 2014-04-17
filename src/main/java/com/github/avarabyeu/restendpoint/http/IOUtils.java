package com.github.avarabyeu.restendpoint.http;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO Helper. Added to avoid dependency to similar Apache commons-io library
 * 
 * @author Andrei Varabyeu
 * 
 */
public class IOUtils {

	/**
	 * Closes Resource without throwing any errors
	 * 
	 * @param closeable
	 */
	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
}
