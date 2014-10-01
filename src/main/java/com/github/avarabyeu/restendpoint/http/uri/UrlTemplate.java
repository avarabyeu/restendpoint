package com.github.avarabyeu.restendpoint.http.uri;

import com.google.common.base.Joiner;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template of http URL abstraction and builder. In charge of path variables parsing and replacement<br>
 * Implementation of UrlTemplate is immutable and might be used by several threads
 *
 * @author Andrey Vorobyov
 */
public class UrlTemplate {

    /* Path variable Regular Expression */
    private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{(.*?)\\}");

    /* URL template */
    private String template;

    /* List of names of found path variables */
    private List<String> pathVariables;

    private UrlTemplate(String template) {

        this.pathVariables = new LinkedList<String>();
        this.template = template;

        parsePathVariables();

    }

    /**
     * Creates new instance of template
     *
     * @param template
     * @return
     */
    public static UrlTemplate create(String template) {
        return new UrlTemplate(template);
    }

    /**
     * @param name name of path variable
     * @return TRUE in case if provided variable is defined in template
     */
    public boolean hasPathVariable(String name) {
        return pathVariables.contains(name);
    }


    /**
     * Creates new merger. Leaves template instance immutable
     *
     * @return
     */
    public Merger merge() {
        return new Merger(this.template);
    }

    private void parsePathVariables() {
        Matcher m = PATH_VARIABLE_PATTERN.matcher(template);
        while (m.find()) {
            pathVariables.add(m.group(1));
        }
    }

    /**
     * Merges template. Merger is mutable and cannot be used by several threads
     */
    public class Merger {

        private StringBuilder template;

        private Merger(String template) {
            this.template = new StringBuilder(template);
        }

        public Merger expand(Map<String, Object> pathParameters) {
            Matcher m = PATH_VARIABLE_PATTERN.matcher(template);

            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                Object replacement = pathParameters.get(m.group(1));
                if (null == replacement) {
                    throw new RuntimeException("Unknown path variable: " + m.group(1));
                }
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
            }
            m.appendTail(sb);

            this.template = new StringBuilder(sb);
            return this;
        }

        public Merger appendQueryParameters(@Nullable Map<String, ?> parameters) {
            if (null == parameters || parameters.isEmpty()) {
                return this;
            }

            if (template.indexOf("?") != -1) {
                template.append("&");
            }
            Joiner.on('&').withKeyValueSeparator("=").appendTo(template, parameters);
            return this;
        }

        public String build() {
            return this.template.toString();
        }
    }


}
