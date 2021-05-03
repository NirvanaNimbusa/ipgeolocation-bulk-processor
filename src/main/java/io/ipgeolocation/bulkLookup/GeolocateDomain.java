package io.ipgeolocation.bulkLookup;

import io.ipgeolocation.api.Geolocation;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;

public class GeolocateDomain {
    private final String apiKey;

    public GeolocateDomain(String apiKey) {
        this.apiKey = apiKey;
    }

    public void processDomainsFile(String domainsFile) {
        if (isNullOrEmpty(domainsFile)) {
            throw new NullPointerException("Pre-condition violated: file path must not be null or empty.");
        }

        Path domainsFilePath = Paths.get(domainsFile);
        Path geolocatedDomainsFilePath = Paths.get("./domains-geolocated.csv");
        CellProcessor notNullCellProcessor = new NotNull();
        CellProcessor[] ipGeolocatedIPAddressesFileCellProcessors = new CellProcessor[] { notNullCellProcessor, notNullCellProcessor, notNullCellProcessor };

        try (InputStream inputStream = new FileInputStream(domainsFilePath.toFile());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = new FileOutputStream(geolocatedDomainsFilePath.toFile());
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CsvListWriter csvListWriter = new CsvListWriter(outputStreamWriter, CsvPreference.STANDARD_PREFERENCE)
        ) {
            System.out.println("Geolocating domains from file ..");

            String domain;
            int index = 0;
            String[] ipAddresses = new String[50];

            while ((domain = bufferedReader.readLine()) != null) {
                if (index < 50) {
                    ipAddresses[index] = domain;
                    index += 1;
                } else {
                    ipGeolocateAndWriteIPAddresses(ipAddresses, csvListWriter, ipGeolocatedIPAddressesFileCellProcessors);
                    index = 0;
                }
            }

            if (index > 0) {
                ipGeolocateAndWriteIPAddresses(ipAddresses, csvListWriter, ipGeolocatedIPAddressesFileCellProcessors);
            }

            System.out.println("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ipGeolocateAndWriteIPAddresses(String[] ipAddresses, CsvListWriter csvListWriter, CellProcessor[] ipGeolocatedIPAddressesFileCellProcessors) throws IOException {
        List<Geolocation> geolocationList = IPGeolocationLookup.bulkLookup(apiKey, ipAddresses, "country_code2,country_name");

        for (Geolocation geolocation: geolocationList) {
            if (!isNull(geolocation.getDomain())) {
                csvListWriter.write(Arrays.asList(geolocation.getDomain(), geolocation.getCountryCode2(), geolocation.getCountryName()), ipGeolocatedIPAddressesFileCellProcessors);
            } else {
                csvListWriter.write(Collections.singletonList(geolocation.getMessage()));
            }
        }
    }
}
