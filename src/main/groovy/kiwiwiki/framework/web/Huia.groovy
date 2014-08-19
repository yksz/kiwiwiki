package kiwiwiki.framework.web

import java.util.regex.Pattern

class Huia {
    static List<Map> gets = [], posts = [], puts = [], deletes = []

    static def get(Pattern pattern, Closure closure) {
        register('get', pattern, closure)
    }

    static def post(Pattern pattern, Closure closure) {
        register('post', pattern, closure)
    }

    static def put(Pattern pattern, Closure closure) {
        register('put', pattern, closure)
    }

    static def delete(Pattern pattern, Closure closure) {
        register('delete', pattern, closure)
    }

    private static def register(method, pattern, closure) {
        Huia["${method}s"] << [pattern: pattern, closure: closure]
    }

    static def uri(String path) {
        "/${path}".toURI()
    }
}
