import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String path;
    private Map<String, String> parameters;

    public Request(String requestLine) {
        String[] parts = requestLine.split(" ");
        String[] pathAndQuery = parts[1].split("\\?", 2);
        this.path = pathAndQuery[0];

        this.parameters = new HashMap();
        if (pathAndQuery.length > 1) {
            setParameters(pathAndQuery[1]);
        }
    }

    public String getPath() {
        return path;
    }

    public Map getParameters() {
        return parameters;
    }

    private void setParameters(String queryString) {
        List<NameValuePair> params = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            parameters.put(param.getName(), param.getValue());
        }
    }
}