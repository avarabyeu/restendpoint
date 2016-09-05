package com.github.avarabyeu.router;

import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@link Route} based on regular expressions
 * <p>
 * Find '{XXX}' constructions which represents path variables
 * and replaced them with '(?<XXX>.*)' which is named group.
 * After check for match and if TRUE obtains values of path variables
 *
 * @author Andrei Varabyeu
 * @see <a href="http://www.regular-expressions.info/named.html">Named Groups</a>
 */
public class RegexpRoute extends Route {

    private static final Pattern PATH_VARIABLES_PATTERN = Pattern.compile("\\{(.*?)\\}");

    private final Pattern pathPattern;
    private final List<String> pathVariableNames;

    public RegexpRoute(Request.Method method, String path) {
        this(Optional.ofNullable(method), path);
    }

    public RegexpRoute(Optional<Request.Method> method, String path) {
        super(method);

        /* find path variables */
        Matcher m = PATH_VARIABLES_PATTERN.matcher(path);
        StringBuffer urlPattern = new StringBuffer();
        this.pathVariableNames = new LinkedList<>();
        while (m.find()) {
            /* replace them with named groups and remember variable names */
            String varName = m.group(1);
            pathVariableNames.add(varName);
            m.appendReplacement(urlPattern, "(?<$1>.*?)");
        }
        m.appendTail(urlPattern);

        this.pathPattern = Pattern.compile(urlPattern.toString());
    }

    @Override
    public boolean matches(Request request) {
        if (!super.matches(request)) {
            return false;
        }

        Matcher m = pathPattern.matcher(request.getRequestUri());
        boolean matches = m.matches();
        if (matches) {
            Map<String, String> vars = this.pathVariableNames.stream()
                    .collect(Collectors.toMap(name -> name, m::group));
            request.setPathVariables(
                    Collections.unmodifiableMap(vars));
        }
        return matches;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pathPattern", pathPattern)
                .toString();
    }
}
