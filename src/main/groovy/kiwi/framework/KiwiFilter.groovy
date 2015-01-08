package kiwi.framework

import java.util.regex.Pattern

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class KiwiFilter implements Filter {
    private static final CLASS_PARAMETER = 'class'
    private static def initialized = false

    void init(FilterConfig conf) {
        synchronized (KiwiFilter.class) {
            if (!initialized) {
                runStarter(conf)
                initialized = true
            }
        }
    }

    private def runStarter(conf) {
        def className = conf.getInitParameter(CLASS_PARAMETER)
        if (className != null) {
            def starter = Class.forName(className).newInstance()
            starter.start()
        }
    }

    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        def (closure, matcher) = findClosure(req)
        if (closure)
            execute(closure, matcher, req, resp)
        else
            chain.doFilter(req, resp)
    }

    private def findClosure(req) {
        switch (req.method) {
            case 'GET':
                return findClosure(req, Kiwi.gets)
            case 'POST':
                return findClosure(req, Kiwi.posts)
            case 'PUT':
                return findClosure(req, Kiwi.puts)
            case 'DELETE':
                return findClosure(req, Kiwi.deletes)
        }
    }

    private def findClosure(req, spec) {
        def uri = req.requestURI - req.contextPath
        for (route in spec.keySet()) {
            switch (route) {
                case String:
                    if (route == uri || matches(route, uri, req))
                        return [spec[route], null]
                    break
                case Pattern:
                    def matcher = route.matcher(uri)
                    if (matcher.matches())
                        return [spec[route], matcher]
                    break
                default:
                    throw new AssertionError("Not support the type of route: ${route.class.name}")
            }
        }
        return [null, null]
    }

    // matches: route='/hello/:name', uri='/hello/world'
    private def matches(route, uri, req) {
        def routeParts = route.split('/')
        def uriParts = uri.split('/')
        if (routeParts.size() != uriParts.size())
            return false
        for (i in 1..<routeParts.size()) {
            if (routeParts[i].charAt(0) == ':')
                continue
            if (routeParts[i] != uriParts[i])
                return false
        }
        setParams(req, routeParts, uriParts)
        return true
    }

    private def setParams(req, routeParts, uriParts) {
        for (i in 1..<routeParts.size())
            if (routeParts[i].charAt(0) == ':')
                req.setAttribute(routeParts[i], uriParts[i])
    }

    private def execute(closure, matcher, req, resp) {
        closure.delegate = resp
        def result = (matcher == null) ? closure(req) : closure(req, matcher)
        switch (result) {
            case String:
            case GString:
                resp.writer.write(result.toString())
                break
            case URI:
                def dispatcher = req.getRequestDispatcher(result.toString())
                dispatcher.forward(req, resp)
                break
        }
    }

    void destroy() {}
}
