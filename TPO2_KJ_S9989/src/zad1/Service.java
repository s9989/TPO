/**
 *
 *  @author Kuryłowicz Jakub S9989
 *
 */

package zad1;

import java.io.IOException;
import org.json.simple.JSONObject;

class Service {

    private String country;

    Service(String country)
    {
        this.country = country;
    }

    String getWeather(String city) throws IOException
    {
        String api = "https://samples.openweathermap.org/data/2.5/weather?q={city}&appid=1";
        Fetcher fetcher = new Fetcher(api.replace("{city}", city));
        JSONObject o = new JSONObject();

        return fetcher.fetch();
    }

    Double getRateFor(String currency) throws IOException
    {
        String api = "https://api.exchangeratesapi.io/latest?base={currency_code}";
        Fetcher fetcher = new Fetcher(api.replace("{currency_code}", currency));

        System.out.println(fetcher.fetch());
        return 0.0;
    }

    Double getNBPRate() throws IOException
    {
        Fetcher fetcher = new Fetcher("http://www.nbp.pl/kursy/kursya.html");

        System.out.println(fetcher.fetch());
        return 0.0;
    }

}  
