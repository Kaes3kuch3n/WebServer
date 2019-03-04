package de.kaes3kuch3n.webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Routes {

    private static Map<String, Route> routes = new HashMap<>();
    private static BiConsumer<Request, Response> error404 = (request, response) -> {
        response.setStatus(404, "File Not Found");
        response.send("404 - File Not Found");
    };

    /**
     * Register a new route
     * @param path The path to register the route at
     * @param handler The handler that handles the route
     */
    public static void register(String path, Method method, BiConsumer<Request, Response> handler) {
        routes.put(path, new Route(method, handler));
    }

    public static void register404(BiConsumer<Request, Response> handler) {
        error404 = handler;
    }

    /**
     * Returns the handler for the route that fits the path.
     * @param path The path that was accessed
     * @param method The method that was used
     * @return A fitting route, if one exists; else returns null
     */
    static BiConsumer<Request, Response> get(String path, Method method) {
        Route route;
        if ((route = routes.get(path)) != null && route.method == method) {
            return route.handler;
        }

        return null;
    }

    static BiConsumer<Request, Response> error404() {
        return error404;
    }

    private static class Route {
        private Method method;
        private BiConsumer<Request, Response> handler;

        private Route(Method method, BiConsumer<Request, Response> handler) {
            this.method = method;
            this.handler = handler;
        }
    }
}
