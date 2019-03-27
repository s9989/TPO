/**
 *
 *  @author Kuryłowicz Jakub S9989
 *
 */

package zad1;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Service {

    private Currency currency;

    Service(String country, Locale inLocale) throws IllegalArgumentException
    {

        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(inLocale), iso);
        }

        String countryLocale = countries.get(country);

        if (countryLocale == null) {
            throw new IllegalArgumentException("Nieznana nazwa kraju");
        }

        Locale locale = new Locale("", countryLocale);
        this.currency = Currency.getInstance(locale);
    }

    Service(String country)
    {
        this(country, Locale.UK);
    }

    String getWeather(String city)
    {
        String api = "https://samples.openweathermap.org/data/2.5/weather?q={city}&appid=1";
        Fetcher fetcher = new Fetcher(api.replace("{city}", city));

        return fetcher.fetch();
    }

    Double getRateFor(String currency) throws IOException
    {
        Double rate = 0.0;

        String api = "https://api.exchangeratesapi.io/latest?base={currency_code}";
        Fetcher fetcher = new Fetcher(api.replace("{currency_code}", currency));

        try {
            Object jp = new JSONParser().parse(fetcher.fetch());
            JSONObject jo = (JSONObject) jp;
//            System.out.println(jo.get("base"));
            Map<String, Double> rates = (Map<String, Double>) jo.get("rates");

            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                if (entry.getKey().equals(this.currency.toString())) {
                    rate = entry.getValue();
                }
            }

        } catch (ParseException e) {
            throw new IOException("Błąd przy przetwarzaniu odpowiedzi z API");
        }

        return rate;
    }

    private Double getNRBRateTry(String url) throws IOException
    {
        Fetcher fetcher = new Fetcher(url);

        String regex = ".*?1 --currency--.*?(\\d*?,\\d*).*?".replace("--currency--", this.currency.toString());
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(fetcher.fetch());

        if (!m.matches()) {
            throw new IOException("Nie znaleziono waluty");
        }

        return Double.valueOf(m.group(1).replace(",", "."));
    }

    Double getNBPRate() throws IOException
    {
        Double rate;

        try {
            rate = this.getNRBRateTry("http://www.nbp.pl/kursy/kursya.html");
        } catch (IOException e) {

            try {
                rate = this.getNRBRateTry("http://www.nbp.pl/kursy/kursyb.html");
            } catch (IOException e2) {
                throw new IOException("Nie znaleziono waluty w NBP");
            }

        }

        return rate;
    }

    public Currency getCurrency()
    {
        return currency;
    }
}
