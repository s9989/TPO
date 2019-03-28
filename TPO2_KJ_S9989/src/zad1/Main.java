/**
 * @author Kuryłowicz Jakub S9989
 */

package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            String city = "London";
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


            JFrame frame = new JFrame(gc);
            frame.setTitle("Klient usług sieciowych");
            frame.setSize(960, 800);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel textLabel1 = new JLabel("Country: ");
            JTextField textField1 = new JTextField(10);
            textField1.setText(country);
            JLabel textLabel2 = new JLabel("City: ");
            JTextField textField2 = new JTextField(10);
            textField2.setText(city);
            JLabel textLabel3 = new JLabel("Currency: ");
            JTextField textField3 = new JTextField(6);
            textField3.setText(currency);
            JButton submitButton = new JButton("OK");

            JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
            startPanel.setPreferredSize(new Dimension(960, 50));

            startPanel.add(textLabel1);
            startPanel.add(textField1);
            startPanel.add(textLabel2);
            startPanel.add(textField2);
            startPanel.add(textLabel3);
            startPanel.add(textField3);
            startPanel.add(submitButton);

            JLabel jLabelStart = new JLabel("<html><center>" + currency + " to " + s.getCurrency() + "<br>" + s.getRateFor(currency) + "</center></html>", SwingConstants.CENTER);
            jLabelStart.setPreferredSize(new Dimension(150, 100));

            JLabel jLabelCenter = new JLabel("<html><body style=\"width:200px;word-wrap:break-word;\">Pogoda<br>" + s.getWeatherMain(city) + "</body></html>", SwingConstants.CENTER);
            jLabelCenter.setPreferredSize(new Dimension(660, 100));
            jLabelCenter.setOpaque(true);
            jLabelCenter.setBackground(new Color(66, 133, 244 ));
            jLabelCenter.setForeground(Color.WHITE);

            JLabel jLabelEnd = new JLabel("<html><center>" + s.getCurrency() + " to PLN<br>" + s.getNBPRate() + "</center></html>", SwingConstants.CENTER);
            jLabelEnd.setPreferredSize(new Dimension(150, 100));

            frame.setLayout(new GridBagLayout());

            JPanel jEndPanel = new JPanel();
            final JFXPanel fxPanel = new JFXPanel();
            fxPanel.setPreferredSize(new Dimension(960, 650));

            jEndPanel.add(fxPanel);
            jEndPanel.setPreferredSize(new Dimension(960, 650));

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0; c.gridy = 0; c.gridwidth = 3; c.fill = GridBagConstraints.HORIZONTAL;
            frame.add(startPanel, c);
            c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
            frame.add(jLabelStart, c);
            c.gridx = 1; c.gridy = 1; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
            frame.add(jLabelCenter, c);
            c.gridx = 2; c.gridy = 1; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
            frame.add(jLabelEnd, c);
            c.gridx = 0; c.gridy = 2; c.gridwidth = 3; c.fill = GridBagConstraints.HORIZONTAL;
            frame.add(jEndPanel, c);

            frame.pack();
            frame.setVisible(true);


            Platform.runLater(() -> {
                StackPane root = new StackPane();
                Scene scene = new Scene(root);
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                webEngine.load("https://en.wikipedia.org/wiki/" + city);
                root.getChildren().add(webView);
                fxPanel.setScene(scene);
            });

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    s.setCountry(textField1.getText());

                    try {
                        String text1 = "<html><center>" + textField3.getText() + " to " + s.getCurrency() + "<br>" + s.getRateFor(textField3.getText()) + "</center></html>";
                        jLabelStart.setText(text1);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }

                    try {
                        String text2 = "<html><center>" + s.getCurrency() + " to PLN<br>" + s.getNBPRate() + "</center></html>";
                        jLabelEnd.setText(text2);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }

                    try {
                        String text3 = "<html><body style=\"width:200px;word-wrap:break-word;\">Pogoda<br>" + s.getWeatherMain(textField2.getText()) + "</body></html>";
                        jLabelCenter.setText(text3);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                    Platform.runLater(() -> {
                        JFXPanel jfx = (JFXPanel) jEndPanel.getComponent(0);
                        StackPane spane = (StackPane) jfx.getScene().getRoot();
                        WebView wview = (WebView) spane.getChildren().get(0);
                        wview.getEngine().load("https://en.wikipedia.org/wiki/" + textField2.getText());
                    });
                }
            });



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
