import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    //Логика обработки запроса из лекции.
    private static void requestProcessing(Socket clientSocket) {

        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        while (true) {

            try (
                    final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    final BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())
            ) {
                // read only request line for simplicity
                // must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                System.out.printf("RequestLine: " + requestLine + "\n");
                final var parts = requestLine.split(" ");


                Request request = new Request(requestLine);

                // Логика обработки хендлеров
                System.out.println("Request Path: " + request.getPath());
                System.out.println("Query Parameters: " + request.getParameters());
                int number = 1;
                System.out.println("Parameter with number " + number + " : " + request.getOneParameter(number));

                if (parts.length != 3) {
                    // just close socket
                    continue;
                }

                final var path = parts[1];
                if (!validPaths.contains(path)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    continue;
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                    continue;
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // Создаем пул потоков с фиксированным размером
    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(64);
        try (final ServerSocket serverSocket = new ServerSocket(9999)) {
            while (true) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    executorService.submit(() -> requestProcessing(clientSocket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            executorService.shutdown();
        }
    }
}