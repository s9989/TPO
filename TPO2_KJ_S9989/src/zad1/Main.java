/**
 * @author Kuryłowicz Jakub S9989
 */

package zad1;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        try {
            Service s = new Service("Poland");
            String weatherJson = s.getWeather("Warsaw");
            Double rate1 = s.getRateFor("USD");
            Double rate2 = s.getNBPRate();

            System.out.println(weatherJson);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
