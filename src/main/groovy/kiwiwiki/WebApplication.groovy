package kiwiwiki

import static kiwiwiki.framework.web.Huia.*

class WebApplication {
    void start() {
        get('/hello') {
            'Hello World!'
        }
    }
}
