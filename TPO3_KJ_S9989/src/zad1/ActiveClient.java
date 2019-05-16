package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveClient {

    static private GraphicsConfiguration gc;

    private static boolean GUI = true;

    private static final int CLIENT_PORT = 9990;
    private static final int SERVER_PORT = 9991;
    private static final int MAX_CONNECTIONS = 20;

    private static JTextField translationInput = new JTextField(20);

    public static void main(String[] args) throws Exception
    {
        if (GUI) {
            ActiveClient.initGUI();
        } else {
            ActiveClient.getMessageFromConsole();
        }
        ActiveClient.startListening();
    }

    private static void startListening() throws Exception
    {
        try (var listener = new ServerSocket(CLIENT_PORT)) {
            System.out.println("The ActiveClient (" + CLIENT_PORT + ") is running...");
            var pool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
            while (true) {
                pool.execute(new Active(listener.accept()));
            }
        }
    }

    private static void initGUI()
    {
        JFrame frame = new JFrame(gc);
        frame.setTitle("Translator");
        frame.setSize(300, 300);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel startPanel = new JPanel();

        var wordLabel = new JLabel("Polskie słowo: ");
        var wordInput = new JTextField(20);

        startPanel.add(wordLabel);
        startPanel.add(wordInput);

        JPanel centerPanel = new JPanel();

        var translationLabel = new JLabel("Tłumaczenie : ");

        centerPanel.add(translationLabel);
        centerPanel.add(ActiveClient.translationInput);

        JPanel endPanel = new JPanel();

        var button_en = new JButton("EN");
        var button_fr = new JButton("FR");

        endPanel.add(button_en);
        endPanel.add(button_fr);

        GridBagConstraints c;

        frame.setLayout(new GridBagLayout());

        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(startPanel, c);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(centerPanel, c);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2; c.gridwidth = 1; c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(endPanel, c);
        frame.pack();
        frame.setVisible(true);

        button_en.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String input = wordInput.getText();
                try {
                    ActiveClient.sendMessage(input, "EN");
                } catch (IOException ex) {

                }
            }
        });

        button_fr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = wordInput.getText();
                try {
                    ActiveClient.sendMessage(input, "FR");
                } catch (IOException ex) {

                }
            }
        });
    }

    private static boolean sendMessage(String input, String language) throws IOException
    {
        var socket = new Socket("127.0.0.1", SERVER_PORT);
        var socketInput = new PrintWriter(socket.getOutputStream(), true);
        var socketOutput = new Scanner(socket.getInputStream());

        var request = new Request(input, language);

        socketInput.println(request.getContent());
        System.out.println("Sent [" + request.getContent() + "] to main");

        if (socketOutput.hasNextLine()) {
            String response = socketOutput.nextLine();

            if (!response.equals("OK")) {
                System.out.println(response);
                return false;
            }
        }

        return true;
    }

    private static void getMessageFromConsole() throws IOException
    {
        String language = "EN";
        var in = new Scanner(System.in);

        System.out.print("Enter language: ");
        if (in.hasNextLine()) {
            language = in.nextLine();
        }


        System.out.print("Enter message: ");
        if (in.hasNextLine()) {
            String input = in.nextLine();

            if (!ActiveClient.sendMessage(input, language)) {
                ActiveClient.getMessageFromConsole();
            }

        }
    }

    private static class Active implements Runnable
    {
        private Socket socket;

        Active(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {

                this.handleResponse();

            } catch (Exception e) {
                System.out.println("Error:" + socket);
                e.printStackTrace();
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }

        private void handleResponse() throws IOException
        {
            var in = new Scanner(this.socket.getInputStream());
            var out = new PrintWriter(this.socket.getOutputStream(), true);

            if (in.hasNextLine()) {

                String input = in.nextLine();
                System.out.println("Received [" + input + "] from Dict");

                Response response = new Response(input);

                out.println(response.getContent());
                System.out.println(response.getWord() + " - " + response.getTranslation());

                if (GUI) {
                    ActiveClient.translationInput.setText(response.getTranslation());
                } else {
                    // send next message
                    ActiveClient.getMessageFromConsole();
                }
            }
        }

    }

    private static class Response
    {
        String message = "OK";
        String word, translation;

        Response(String input)
        {
            Pattern pattern = Pattern.compile("(.*?);(.*?)");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                this.message = "Wrong format";
            }

            this.word = matcher.group(1);
            this.translation = matcher.group(2);
        }

        String getContent() {
            return message;
        }

        String getWord() {
            return word;
        }

        String getTranslation() {
            return translation;
        }
    }

    private static class Request
    {
        private String word;
        private String lang;
        private String port;

        Request(String input, String language)
        {
            this.word = input;
            this.lang = language;
            this.port = String.valueOf(CLIENT_PORT);
        }

        String getContent()
        {
            return this.word + ";" + this.lang + ";" + this.port;
        }
    }

}
