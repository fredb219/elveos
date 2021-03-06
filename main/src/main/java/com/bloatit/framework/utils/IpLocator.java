package com.bloatit.framework.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import com.bloatit.common.ConfigurationManager;
import com.bloatit.framework.exceptions.highlevel.BadProgrammerException;
import com.bloatit.framework.exceptions.highlevel.ExternalErrorException;

public class IpLocator {

    private static IpLocator instance = new IpLocator();

    public static Country localize(final String ip) {
        return instance.localizeIp(ip);
    }

    public static class Country {
        public final String name;
        public final String code;

        public Country(final String name, final String code) {
            super();
            this.name = name;
            this.code = code;
        }
    }

    /**
     * The size is calculated with a first run ... So when the db change, you
     * have to change the size !
     */
    private static final int size = 161954;

    private final long[] ips = new long[size];
    private final int[] ctr = new int[size];
    private final String[] cnames = new String[256];
    private final String[] ccodes = new String[256];

    private IpLocator() {
        try {
            parseCountryFile();
            parseIpCountryFile();
        } catch (final FileNotFoundException e) {
            throw new ExternalErrorException(e);
        } catch (final IOException e) {
            throw new ExternalErrorException(e);
        }
    }

    private Country localizeIp(final String addr) {
        // first convert ip to long
        final String[] addrArray = addr.split("\\.");
        long ip = 0;
        if (addrArray.length != 4) {
            throw new BadProgrammerException("The ip address is malformed");
        }
        // Last one is ignore (always 0 in the db)
        for (int i = 0; i < 3; i++) {
            final int power = 3 - i;
            ip += ((Long.parseLong(addrArray[i]) % 256 * Math.pow(256, power)));
        }
        final int ipIndex = findIPIndex(ip, 0, size);
        return new Country(cnames[ctr[ipIndex]], ccodes[ctr[ipIndex]]);
    }

    private void parseCountryFile() throws FileNotFoundException, IOException {
        final File countriesFile = new File(ConfigurationManager.SHARE_DIR + "/locales/countries.csv");
        final BufferedReader countriesBufRdr = new BufferedReader(new FileReader(countriesFile));
        String line = null;
        while ((line = countriesBufRdr.readLine()) != null) {
            final StringTokenizer st = new StringTokenizer(line, ",");
            int id = Integer.parseInt(st.nextToken());
            if (id == -1) {
                id = 255;
            }
            cnames[id] = st.nextToken();
            for (int i = 2; i < st.countTokens() - 1; i++) {
                cnames[id] += "," + st.nextToken();
            }
            ccodes[id] = st.nextToken();
        }
        countriesBufRdr.close();
    }

    // public static void main(String[] args) throws Exception {
    // // generateDBFile("/home/thomas/ipdatabase/csv/sorted.csv",
    // // "/home/thomas/db.csv");
    // }

    /**
     * To create a new db file. Remember the first file must be ordered with
     * "sort -n $file".
     */
    @SuppressWarnings("unused")
    private static void generateDBFile(final String inputfile, final String dbfile) throws FileNotFoundException, IOException {
        final BufferedReader bufRdr = new BufferedReader(new FileReader(new File(inputfile)));
        final BufferedWriter w = new BufferedWriter(new FileWriter(new File(dbfile)));
        int lastCountry = -1;
        String line;
        while ((line = bufRdr.readLine()) != null) {
            final StringTokenizer st = new StringTokenizer(line, ",");
            final long ip = Long.parseLong(st.nextToken());
            final int countryId = Integer.parseInt(st.nextToken());
            if (countryId != lastCountry) {
                w.write(ip + "," + countryId + "\n");
            }
            lastCountry = countryId;
        }
        w.close();
        bufRdr.close();
    }

    private void parseIpCountryFile() throws FileNotFoundException, IOException {
        String line;
        final File file = new File(ConfigurationManager.SHARE_DIR + "/locales/ip_country.csv");
        final BufferedReader bufRdr = new BufferedReader(new FileReader(file));
        int lastCountry = -1;
        int i = 0;
        while ((line = bufRdr.readLine()) != null) {
            final StringTokenizer st = new StringTokenizer(line, ",");
            final long ip = Long.parseLong(st.nextToken());
            final int countryId = Integer.parseInt(st.nextToken());

            if (countryId != lastCountry) {
                ips[i] = ip;
                ctr[i] = countryId;
                i++;
            }
            lastCountry = countryId;
        }
        bufRdr.close();
    }

    private int findIPIndex(final long ip, final int bornMin, final int bornMax) {
        if (bornMin + 1 == bornMax) {
            return bornMin;
        }
        final int center = (bornMin + bornMax) / 2;
        if (ip > ips[center]) {
            return findIPIndex(ip, center, bornMax);
        } else if (ip < ips[center]) {
            return findIPIndex(ip, bornMin, center);
        } else {
            return center;
        }
    }

    public static void initialize() {
        // Make sure the static part are loaded
    }
}
