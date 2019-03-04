package de.kaes3kuch3n.webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

public class Connection implements Runnable {

    private Socket socket;

    /**
     * Opens a new connection using the socket provided.
     * @param socket The socket the client is connected on.
     */
    Connection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Reads the header of the request and handles it based on the information it gets.
     */
    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedOutputStream dataOutput = new BufferedOutputStream(socket.getOutputStream())
        ) {
            // Read and tokenize request
            String request = reader.readLine();
            System.out.println(request);
            StringTokenizer tokenizer = new StringTokenizer(request);
            String methodString = tokenizer.nextToken();
            String requestedPath = tokenizer.nextToken();

            //Check if method is valid
            Method method;

            if ((method = Method.fromString(methodString)) != null) {
                //Add body to params if method is POST or PUT
                Map<String, String> params = new HashMap<>();
                if (method == Method.POST || method == Method.PUT)
                    params = getRequestParams(reader);

                Request req = new Request(getId(requestedPath), params);
                Response res = new Response(writer, dataOutput);

                //Get handler from routes and run it
                BiConsumer<Request, Response> handler = Routes.get(requestedPath, method);
                if (handler != null) {
                    handler.accept(req, res);
                } else {
                    Routes.error404().accept(req, res);
                }
            } else {
                writer.println("Method invalid: " + methodString);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts the id of the selected item from the requested path and checks if it is valid.
     * @param requestedPath The path that was sent with the request
     * @return The id which is contained in the path if it is valid, otherwise null
     */
    private int getId(String requestedPath) {
        if (requestedPath.endsWith("/")) {
            requestedPath = requestedPath.substring(0, requestedPath.length() - 1);
        }

        String[] splitPath = requestedPath.split("/");
        String id = splitPath[splitPath.length - 1];

        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Extracts the parameters from the request body and puts them into a Map.
     * @param reader The reader which is reading the request
     * @return The Map containing the parameters from the request
     * @throws IOException If there are any problems with the reader
     */
    private Map<String, String> getRequestParams(BufferedReader reader) throws IOException {
        int contentLength = 0;

        String next;
        while (!(next = reader.readLine()).equals("")) {
            if (next.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(next.split(": ")[1]);
            }
        }

        char[] body = new char[contentLength];
        int charsRead = reader.read(body);

        if (charsRead != contentLength) {
            System.err.println("Error while reading HTTP message body");
        }

        String[] params = new String(body).split(";");

        Map<String, String> paramMap = new HashMap<>();
        for (String param : params) {
            String[] split = param.split("=");
            paramMap.put(split[0], split[1]);
        }

        return paramMap;
    }

    /**
     * Gets the IP address of the client connected to the server via this socket
     * @return The IPv4 address of the client
     */
    String getConnectionDetails() {
        return socket.getInetAddress().toString();
    }
}
