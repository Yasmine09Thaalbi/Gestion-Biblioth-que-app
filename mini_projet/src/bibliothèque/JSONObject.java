package bibliothèque;

import java.util.HashMap;
import java.util.Map;

public class JSONObject {
    private Map<String, Object> map;

    public JSONObject() {
        map = new HashMap<>();
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public String getString(String key) {
        Object value = get(key);
        return (value instanceof String) ? (String) value : null;
    }

    public int getInt(String key) {
        Object value = get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : 0;
    }
    
    public long getLong(String key) {
        Object value = get(key);
        return (value instanceof Number) ? ((Number) value).longValue() : 0;
    }

    public boolean getBoolean(String key) {
        Object value = get(key);
        return (value instanceof Boolean) && (Boolean) value;
    }

    // Other methods can be added based on your requirements

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                result.append(", ");
            }
            result.append("\"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof String) {
                result.append("\"").append(entry.getValue()).append("\"");
            } else {
                result.append(entry.getValue());
            }
            first = false;
        }
        result.append("}");
        return result.toString();
    }
    
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }
    
	 public static JSONObject parse(String jsonString) {
	    JSONObject jsonObject = new JSONObject();
	    try {
	        // Assuming the input string is in a simple key-value pair format
	        // Example: {"key1": "value1", "key2": 123, "key3": true}
	        jsonString = jsonString.trim().substring(1, jsonString.length() - 1);
	        String[] pairs = jsonString.split(",\\s*");
	
	        for (String pair : pairs) {
	            String[] keyValue = pair.split(":");
	            String key = keyValue[0].trim().replaceAll("\"", "");
	            String valueString = keyValue[1].trim();
	
	            if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
	                // String value
	                String value = valueString.substring(1, valueString.length() - 1);
	                jsonObject.put(key, value);
	            } else if (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false")) {
	                // Boolean value
	                boolean value = Boolean.parseBoolean(valueString);
	                jsonObject.put(key, value);
	            } else if (valueString.matches("-?\\d+\\.\\d+")) {
	                // Double value
	                double doubleValue = Double.parseDouble(valueString);
	                jsonObject.put(key, doubleValue);
	            } else if (valueString.matches("-?\\d+")) {
	                // Long value
	                long longValue = Long.parseLong(valueString);
	                jsonObject.put(key, longValue);
	            } else {
	                // Handle other cases as needed
	                System.out.println("Unhandled value: " + valueString);
	            }
	        }
	
	    } catch (Exception e) {
	        System.out.println("Error parsing JSON string: " + e.getMessage());
	    }
	    return jsonObject;
	}

}
