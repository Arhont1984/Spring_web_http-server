public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        // Example of adding handlers
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String responseBody = "GET Handler for /messages";
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + responseBody.length() + "\r\n" +
                    "Connection: close\r\n\r\n").getBytes());
            responseStream.write(responseBody.getBytes());
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            String responseBody = "POST Handler for /messages";
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + responseBody.length() + "\r\n" +
                    "Connection: close\r\n\r\n").getBytes());
            responseStream.write(responseBody.getBytes());
        });

        server.start(); // Start listening on port 9999

    }
}
