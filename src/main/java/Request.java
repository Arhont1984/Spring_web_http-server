import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    // Конструктор для инициализации Request
    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    // Метод для парсинга запроса из BufferedReader
    public static Request parse(BufferedReader reader) throws IOException {
        // Читаем первую строку (request line)
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("Empty request line");
        }

        // Парсим метод и путь
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        String method = requestParts[0];
        String path = requestParts[1];

        // Заголовки
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Читаем тело запроса, если оно есть
        StringBuilder bodyBuilder = new StringBuilder();
        if ("POST".equalsIgnoreCase(method)) {
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars);
            bodyBuilder.append(bodyChars);
        }

        return new Request(method, path, headers, bodyBuilder.toString());
    }
}
