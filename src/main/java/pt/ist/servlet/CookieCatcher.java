package pt.ist.servlet;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter( urlPatterns = { "/*" }, servletNames = { "CookieCatcher" } )
public class CookieCatcher implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CookieCatcher.class);
    public static final String NEW_COOKIE_LOG =
            "Found new Cookie: name = {} ; value = {} ; isHttpOnly = {} ; secure = {} ; maxAge = {} ; version = {} ; domain = {} ; path = {} ; comment = {}";

    private CookieHandler defaultCookieHandler = null;
    private Set<String> names = null;
    private Set<String> hashes = null;
    private Set<String> blacklist;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        
        defaultCookieHandler = CookieHandler.getDefault();
        CookieHandler.setDefault(new CookieHandler() {
            @Override
            public void put(final URI uri, final Map<String, List<String>> responseHeaders) throws IOException {
                if (defaultCookieHandler != null) {
                    defaultCookieHandler.put(uri, responseHeaders);
                }
            }

            @Override
            public Map<String, List<String>> get(final URI uri, final Map<String, List<String>> requestHeaders) throws IOException {
                return defaultCookieHandler == null ? Collections.emptyMap() : defaultCookieHandler.get(uri, requestHeaders);
            }
        });

        names = new HashSet<>();
        hashes = new HashSet<>();
        blacklist = CookieCatcherConfiguration.blacklist();

        logger.info("CookieCatcher up and running.");
    }

    @Override
    public void destroy() {
        CookieHandler.setDefault(defaultCookieHandler);
        names = null;
        hashes = null;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        logCookieInfo((HttpServletRequest) request);
        if (chain != null) {
            chain.doFilter(request, response);
        }
    }

    private void logCookieInfo(final HttpServletRequest request) {
        if (request == null || request.getCookies() == null) {
            return;
        }
        for (final Cookie cookie : request.getCookies()) {
            final String name = cookie.getName();
            if (logger.isInfoEnabled()) {
                synchronized (names) {
                    if (!names.contains(name)) {
                        logger.info("Found new Cookie name: {}", name);
                        names.add(name);
                    }
                }
            }
            boolean isBlacklisted = blacklist.contains(name);
            
            if (logger.isDebugEnabled() || isBlacklisted) {
                synchronized (hashes) {
                    final String value = cookie.getValue();
                    final String hash = hashFor(name, value);
                    if (!hashes.contains(hash)) {
                        final boolean httpOnly = cookie.isHttpOnly();
                        final boolean secure = cookie.getSecure();
                        final int maxAge = cookie.getMaxAge();
                        final int version = cookie.getVersion();
                        final String domain = cookie.getDomain();
                        final String path = cookie.getPath();
                        final String comment = cookie.getComment();
                        log(name, value, httpOnly, secure, maxAge, version, domain, path, comment, isBlacklisted);
                        hashes.add(hash);
                    }
                }
            }
        }
    }

    private void log(String name, String value, boolean httpOnly, boolean secure, int maxAge, int version, String domain,
            String path, String comment, boolean warn) {
        if (warn) {
            logger.warn(NEW_COOKIE_LOG, name, value, httpOnly, secure, maxAge, version, domain, path, comment);
        } else {
            logger.debug(NEW_COOKIE_LOG, name, value, httpOnly, secure, maxAge, version, domain, path, comment);
        }
    }

    private String hashFor(final String name, final String value) {
        return name + "_" + value;
    }

}
