package io.ipgeolocation.bulkLookup;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Bootstrap {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NullPointerException("Pre-condition violated: please provide your API key thru command-line argument to this program.");
        } else if (isNullOrEmpty(args[0])) {
            throw new NullPointerException("Pre-condition violated: your API key must not be null or empty");
        }

        GeolocateDomain geolocateDomain = new GeolocateDomain(args[0]);
        geolocateDomain.processDomainsFile("./domains.txt");
    }
}
