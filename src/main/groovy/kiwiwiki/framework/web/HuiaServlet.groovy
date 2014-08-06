package kiwiwiki.framework.web

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HuiaServlet extends HttpServlet {
    private static final CLASS_PARAMETER = 'class'
    private static def initialized = false
    private static def get = [:], post = [:], put = [:], delete = [:]

    void init() {
        synchronized (HuiaServlet.class) {
            if (!initialized) {
                for (route in getRoutes()) {
                    HuiaServlet."${route.method}" << [(route.path): route.closure]
                }
                initialized = true
            }
        }
    }

    private def getRoutes() {
        runStarter()
        Huia.routes
    }

    private def runStarter() {
        def className = getInitParameter(CLASS_PARAMETER)
        if (className != null) {
            def starter = Class.forName(className).newInstance()
            starter.start()
        }
    }

    void doGet(HttpServletRequest req, HttpServletResponse resp) {
        execute(req, resp, get[req.pathInfo])
    }

    void doPost(HttpServletRequest req, HttpServletResponse resp) {
        execute(req, resp, post[req.pathInfo])
    }

    void doPut(HttpServletRequest req, HttpServletResponse resp) {
        execute(req, resp, put[req.pathInfo])
    }

    void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        execute(req, resp, delete[req.pathInfo])
    }

    private def execute(req, resp, closure) {
        if (!closure) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
            return
        }
        closure.delegate = resp
        def result = closure(req)
        switch (result) {
            case String:
            case GString:
                resp.writer.write(result.toString())
                break
        }
    }
}
