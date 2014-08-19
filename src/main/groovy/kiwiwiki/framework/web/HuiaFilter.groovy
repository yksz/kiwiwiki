package kiwiwiki.framework.web

import java.util.regex.Matcher
import java.util.regex.Pattern

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class HuiaFilter implements Filter {
    private static final CLASS_PARAMETER = 'class'
    private static def initialized = false

    void init(FilterConfig conf) {
        synchronized (HuiaFilter.class) {
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
        def (closure, matcher) = searchSpec(req)
        if (closure) {
            execute(closure, matcher, req, resp)
        } else {
            chain.doFilter(req, resp)
        }
    }

    private def searchSpec(req) {
        def uri = req.requestURI - req.contextPath
        switch (req.method) {
            case 'GET':
                return match(uri, Huia.gets)
            case 'POST':
                return match(uri, Huia.posts)
            case 'PUT':
                return match(uri, Huia.puts)
            case 'DELETE':
                return match(uri, Huia.deletes)
        }
    }

    private def match(uri, methods) {
        for (method in methods) {
            Matcher matcher = method.pattern.matcher(uri)
            if (matcher.matches())
                return [method.closure, matcher]
        }
        return [null, null]
    }

    private def execute(closure, matcher, req, resp) {
        closure.delegate = resp
        def result = closure(req, matcher)
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
