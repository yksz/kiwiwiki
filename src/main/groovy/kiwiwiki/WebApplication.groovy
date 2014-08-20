package kiwiwiki

import static kiwiwiki.framework.web.Huia.*

class WebApplication {
    void start() {
        get('/') {
            uri 'index.html'
        }

        get('/:name') { req ->
            def name = req.getAttribute(':name')
            "Hello ${name}!"
        }

        get(~/\/hello\/(?<name>.+)/) { req, m ->
            def name = m.group('name')
            "Hello ${name}!"
        }
    }
}
