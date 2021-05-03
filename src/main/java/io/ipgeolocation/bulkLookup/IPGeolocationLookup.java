package io.ipgeolocation.bulkLookup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.GeolocationParams;
import io.ipgeolocation.api.IPGeolocationAPI;

import java.util.List;

public class IPGeolocationLookup {

    public static List<Geolocation> bulkLookup(String apiKey, String[] ipAddresses, String fields) {
        if (isNullOrEmpty(apiKey)) {
            throw new NullPointerException("Pre-condition violated: API key must not be null or empty.");
        }

        checkNotNull(ipAddresses, "Pre-condition violated: IP addresses must not be null.");

        if (isNullOrEmpty(fields)) {
            fields = "*";
        }

        IPGeolocationAPI ipGeolocationAPI = new IPGeolocationAPI(apiKey);
        GeolocationParams geolocationParams = new GeolocationParams();
        geolocationParams.setFields(fields);
        geolocationParams.setIPAddresses(ipAddresses);

        return ipGeolocationAPI.getBulkGeolocation(geolocationParams);
    }
}
