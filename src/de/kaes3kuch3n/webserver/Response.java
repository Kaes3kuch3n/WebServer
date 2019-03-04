package de.kaes3kuch3n.webserver;

import java.io.*;
import java.util.Date;

public class Response {

    private PrintWriter writer;
    private BufferedOutputStream dataOutputStream;

    private int statusCode;
    private String status;

    Response(PrintWriter writer, BufferedOutputStream dataOutputStream) {
        this.writer = writer;
        this.dataOutputStream = dataOutputStream;

        statusCode = 200;
        status = "OK";
    }

    public void setStatus(int statusCode, String status) {
        this.statusCode = statusCode;
        this.status = status;
    }

    public void send(File file) {
        int fileLength = (int) file.length();
        String contentType = getContentType(file.getName());

        writeHeader(contentType, fileLength);
        try {
            sendFileData(file);
        } catch (IOException e) {
            System.err.println("Failed sending response: ");
            e.printStackTrace();
        }
    }

    public void send(String text) {
        writeHeader("text/plain", text.getBytes().length);
        writer.println(text);
        writer.flush();
    }

    /**
     * Returns the MIME type of the requested file based on its file ending.
     * @param requestedFile The name of the requested file
     * @return Content MIME type of the requested file; default is plain text
     */
    private String getContentType(String requestedFile) {
        String[] splitFilePath = requestedFile.split("\\.");
        String fileType = splitFilePath[splitFilePath.length - 1];

        switch (fileType) {
            case "css":
                return "text/css";
            case "js":
                return "text/javascript";
            case "html":
            case "htm":
                return "text/html";
            default:
                return "text/plain";
        }
    }

    /**
     * Writes a HTTP header with the given arguments to the PrintWriter provided.
     * @param contentType The MIME type of the content
     * @param contentLength The length of the content
     */
    private void writeHeader(String contentType, int contentLength) {
        writer.println("HTTP/1.1 " + statusCode + " " + status);
        writer.println("Server: Java WebServer by Kaes3kuch3n v1.0");
        writer.println("Date: " + new Date());
        writer.println("Content-type: " + contentType);
        writer.println("Content-length: " + contentLength);
        writer.println();
        writer.flush();
    }

    /**
     * Sends a file to a buffered output stream.
     * @param file The file to send
     * @throws IOException If file doesn't exist or if there are problems with the stream
     */
    private void sendFileData(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        int nextByte;

        while ((nextByte = inputStream.read()) != -1) {
            dataOutputStream.write(nextByte);
        }
        dataOutputStream.flush();
    }
}
