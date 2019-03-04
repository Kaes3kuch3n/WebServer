package de.kaes3kuch3n.webserver;

import java.util.Map;

public class Request {
    private int id;
    private Map<String, String> parameters;

    Request(int id, Map<String, String> parameters) {
        this.id = id;
        this.parameters = parameters;
    }

    public int getId() {
        return id;
    }

    public String getParam(String paramName) {
        return parameters.get(paramName);
    }
}
