/**
 * @author Kuryłowicz Jakub S9989
 */

package zad1;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main {

    static private GraphicsConfiguration gc;

    public static void main(String[] args)
    {

        try {
            String country = "United Kingdom";
            String city = "Londyn";
            String currency = "USD";

            Service s = new Service(country);
            String weatherJson = s.getWeather(city);
            Double rate1 = s.getRateFor(currency);
            Double rate2 = s.getNBPRate();

            System.out.println();
            System.out.print("JSON pogody: ");
            System.out.println(weatherJson);

            System.out.print("Przelicznik waluty " + currency + " na " + s.getCurrency() + ": ");
            System.out.println(rate1);

            System.out.print("Przelicznik waluty " + s.getCurrency() + " na PLN: ");
            System.out.println(rate2);


            final JFXPanel fxPanel = new JFXPanel();

            JFrame frame = new JFrame(gc);
            frame.setTitle("Klient usług sieciowych");
            frame.setSize(960, 800);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JButton button = new JButton("Button 1 (PAGE_START)");
            frame.add(button, BorderLayout.PAGE_START);

            button = new JButton("Button 1 (PAGE_START)");
            frame.add(button, BorderLayout.LINE_START);

            button = new JButton("Button 1 (PAGE_START)");
            frame.add(button, BorderLayout.CENTER);

            button = new JButton("Button 1 (PAGE_START)");
            frame.add(button, BorderLayout.LINE_END);

            Platform.runLater(() -> {
                StackPane root = new StackPane();
                Scene scene = new Scene(root);
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                webEngine.load("https://pl.wikipedia.org/wiki/" + city);
                root.getChildren().add(webView);
                fxPanel.setScene(scene);
            });

            frame.add(fxPanel, BorderLayout.PAGE_END);

            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
