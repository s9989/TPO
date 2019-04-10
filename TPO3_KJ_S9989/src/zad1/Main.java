package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int SERVER_PORT = 9991;
    private static final int MAX_CONNECTIONS = 20;

    private static final HashMap<String, Integer> dictionaries = new HashMap<>();

    public static void main(String[] args) throws Exception {

        dictionaries.put("EN", Dictionary.DICTIONARY_EN_PORT);
        dictionaries.put("FR", Dictionary.DICTIONARY_FR_PORT);

        try (var listener = new ServerSocket(SERVER_PORT)) {
            System.out.println("The Main (" + SERVER_PORT + ") server is running...");
            var pool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
            while (true) {
                pool.execute(new Capitalizer(listener.accept()));
            }
        }
    }

    private static class Capitalizer implements
            Runnable
    {
        private Socket socket;

        Capitalizer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
                    String input = in.nextLine();

                    System.out.println("Received [" + input + "] from client");

                    Response response = new Response(input);
                    out.println(response.getContent());

                    if (response.getContent().equals("OK")) {

                        Request request = new Request(response);

                        Socket socket = new Socket("127.0.0.1", request.getSocketPort());

                        var socketInput = new PrintWriter(socket.getOutputStream(), true);
                        var socketOutput = new Scanner(socket.getInputStream());

                        socketInput.println(request.getContent());
                        System.out.println("Sent [" + request.getContent() + "] to Dict");
                        System.out.println("Dict response: " + socketOutput.nextLine());
                    }

                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
                e.printStackTrace();
            } finally {
                try { socket.close(); } catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }

    }

    private static class Response
    {
        private String message = "OK";
        private String word, lang;
        private Integer port;

        Response(String input)
        {
            Pattern pattern = Pattern.compile("(.*?);(.*?);(\\d*?)");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                this.message = "Wrong format";
            }

            String language = matcher.group(2);

            if (!dictionaries.containsKey(language)) {
                this.message = "Unknown language '" + language + "'";
            }

            this.word = matcher.group(1);
            this.lang = language;
            this.port = Integer.valueOf(matcher.group(3));
        }

        String getContent() {
            return message;
        }

        String getWord() {
            return word;
        }

        String getLang() {
            return lang;
        }

        Integer getPort() {
            return port;
        }
    }

    private static class Request
    {
        private String word;
        private Integer socketPort;
        private Integer port;

        Request(Response response)
        {
            this.word = response.getWord();
            this.port = response.getPort();
            this.socketPort = dictionaries.get(response.getLang());
        }

        String getContent()
        {
            return this.word + ";" + this.port;
        }

        Integer getSocketPort() {
            return socketPort;
        }
    }

}
