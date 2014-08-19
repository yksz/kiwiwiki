package kiwiwiki

import static kiwiwiki.framework.web.Huia.*

class WebApplication {
    void start() {
        get(~/\//) { req, m ->
            uri 'index.html'
        }

        get(~/\/hello\/(?<name>.+)/) { req, m ->
            def name = m.group('name')
            "Hello ${name}!"
        }
    }
}
