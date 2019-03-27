package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

class Fetcher
{
    private String url;

    Fetcher(String url)
    {
        this.url = url;
    }

    String fetch() throws IOException
    {
        URL url = new URL(this.url);

        StringBuilder result = new StringBuilder();

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


        return result.toString();
    }
}
