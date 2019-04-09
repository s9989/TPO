package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.InputMismatchException;

class Fetcher
{
    private String url;

    Fetcher(String url)
    {
        this.url = url;
    }

    String fetch() throws InputMismatchException
    {
        StringBuilder result = new StringBuilder();

        try {

            // First set the default cookie manager.
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));


            HttpURLConnection.setFollowRedirects(false);

            URL url = new URL(this.url);

            // Open URL stream as an input stream and print contents to command line
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String inputLine;

                // Read the "gpl.txt" text file from its URL representation
                while((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }

        } catch (IOException e) {
            throw new InputMismatchException(e.getMessage());
        }

        return result.toString();
    }
}
