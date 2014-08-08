package kiwiwiki.framework.web

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class HuiaFilter implements Filter {
    private static final CLASS_PARAMETER = 'class'
    private static def initialized = false
    private static def get = [:], post = [:], put = [:], delete = [:]

    void init(FilterConfig conf) {
        synchronized (HuiaFilter.class) {
            if (!initialized) {
                for (route in getRoutes(conf)) {
                    HuiaFilter."${route.method}" << [(route.path): route.closure]
                }
                initialized = true
            }
        }
    }

    private def getRoutes(conf) {
        runStarter(conf)
        Huia.routes
    }

    private def runStarter(conf) {
        def className = conf.getInitParameter(CLASS_PARAMETER)
        if (className != null) {
            def starter = Class.forName(className).newInstance()
            starter.start()
        }
    }

    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        def closure = getClosure(req)
        if (closure) {
            execute(closure, req, resp)
        } else {
            chain.doFilter(req, resp)
        }
    }

    private def getClosure(req) {
        def path = req.requestURI - req.contextPath
        switch (req.method) {
            case 'GET':
                return get[path]
            case 'POST':
                return post[path]
            case 'PUT':
                return put[path]
            case 'DELETE':
                return delte[path]
        }
    }

    private def execute(closure, req, resp) {
        closure.delegate = resp
        def result = closure(req)
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
