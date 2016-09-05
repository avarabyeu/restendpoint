package com.github.avarabyeu.router;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Useful internal utilities
 *
 * @author Andrei Varabyeu
 */
class Utils {

    private static final ResourceBundle MIME_UTILS_BUNDLE = ResourceBundle
            .getBundle("com/github/avarabyeu/router/mime");

    private static final Map<String, String> MIME_TYPES = Utils.enumerationAsStream(MIME_UTILS_BUNDLE.getKeys())
            .collect(Collectors.toMap(Object::toString,
                    MIME_UTILS_BUNDLE::getString));

    static String resolveMimeType(String filename) {
        return MIME_TYPES
                .get(com.google.common.io.Files.getFileExtension(filename));
    }

    static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }

                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }
}
