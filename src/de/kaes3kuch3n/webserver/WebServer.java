package de.kaes3kuch3n.webserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class WebServer {

    private WebServer(int port) {
        registerRoutes();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("WebServer started. Listening on port " + port + "...");

            while (true) {
                Connection connection = new Connection(serverSocket.accept());
                System.out.println("Connected to " + connection.getConnectionDetails());

                Thread thread = new Thread(connection);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerRoutes() {
        Routes.register("/", Method.GET, (request, response) -> showIndex(response));
        Routes.register404((request, response) -> error404(response));
    }

    private void showIndex(Response response) {
        response.send(new File("resources/public/index.html"));
    }

    private void error404(Response response) {
        response.send(new File("resources/public/404.html"));
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        new WebServer(port);
    }
}
