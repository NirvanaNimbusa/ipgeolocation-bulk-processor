package io.ipgeolocation.bulkLookup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IPGeolocationLookup {

    public static HttpResponse<JsonNode> bulkLookup(String apiKey, String[] ipAddresses, String fields) {
        if (isNullOrEmpty(apiKey)) {
            throw new NullPointerException("Pre-condition violated: API key must not be null or empty.");
        }

        checkNotNull(ipAddresses, "Pre-condition violated: IP addresses must not be null.");

        if (isNullOrEmpty(fields)) {
            fields = "*";
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("apiKey", apiKey);
        parameters.put("fields", fields);

        return Unirest.post("https://api.ipgeolocation.io/ipgeo-bulk")
                .body(new JSONObject().put("ips", ipAddresses))
                .queryString(parameters)
                .contentType("application/json")
                .accept("application/json")
                .asJson();
    }
}
