package kiwi.wiki

import static kiwi.framework.Kiwi.*

class KiwiWiki {
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
