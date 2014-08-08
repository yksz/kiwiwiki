package kiwiwiki

import static kiwiwiki.framework.web.Huia.*

class WebApplication {
    void start() {
        get('/') {
            uri 'index.html'
        }

        get('/hello') { req ->
            'Hello World!'
        }
    }
}
