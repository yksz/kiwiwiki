package kiwiwiki.framework.web

class Huia {
    static List<Map> routes = []

    static def get(String path, Closure closure) {
        register('get', path, closure)
    }

    static def post(String path, Closure closure) {
        register('post', path, closure)
    }

    static def put(String path, Closure closure) {
        register('put', path, closure)
    }

    static def delete(String path, Closure closure) {
        register('delete', path, closure)
    }

    private static def register(method, path, closure) {
        routes << [method: method, path: path, closure: closure]
    }
}
