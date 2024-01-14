package bibliothèque;

import java.util.HashMap;
import java.util.Map;

public class JSONObject {
    private final Map<String, Object> data;

    public JSONObject() {
        this.data = new HashMap<>();
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public static JSONObject parse(String jsonString) {
        JSONObject jsonObject = new JSONObject();

        jsonString = jsonString.substring(1, jsonString.length() - 1); // Remove external braces
        String[] keyValuePairs = jsonString.split(",");
        
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":", 2); // Split into key and value (limit to 2 parts)
            String key = entry[0].trim().replace("\"", ""); // Trim and remove quotes for the key

            // Remove quotes for the value if it is a string
            String value = entry[1].trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            jsonObject.put(key, value);
        }

        return jsonObject;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }

        // Remove the trailing comma if there are entries
        if (result.length() > 1) {
            result.setLength(result.length() - 1);
        }

        result.append("}");

        return result.toString();
    }
}
