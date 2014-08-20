package kiwiwiki.framework.web

import java.util.regex.Pattern

class Huia {
    static Map gets = [:], posts = [:], puts = [:], deletes = [:]

    static def get(String path, Closure closure) {
        register('get', path, closure)
    }

    static def get(Pattern pattern, Closure closure) {
        register('get', pattern, closure)
    }

    static def post(String path, Closure closure) {
        register('post', path, closure)
    }

    static def post(Pattern pattern, Closure closure) {
        register('post', pattern, closure)
    }

    static def put(String path, Closure closure) {
        register('put', path, closure)
    }

    static def put(Pattern pattern, Closure closure) {
        register('put', pattern, closure)
    }

    static def delete(String path, Closure closure) {
        register('delete', path, closure)
    }

    static def delete(Pattern pattern, Closure closure) {
        register('delete', pattern, closure)
    }

    private static def register(method, route, closure) {
        Huia["${method}s"] << [(route): closure]
    }

    static def uri(String path) {
        "/${path}".toURI()
    }
}
