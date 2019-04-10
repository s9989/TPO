package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dictionary {

    static final int DICTIONARY_EN_PORT = 9992;
    static final int DICTIONARY_FR_PORT = 9993;

    private static final int MAX_CONNECTIONS = 20;

    private static final HashMap<String, String> translations = new HashMap<>();

    private static Integer initialize(String lang)
    {
        if (lang.equals("EN")) {
            translations.put("pies", "dog");
            translations.put("kot", "cat");
            translations.put("dom", "house");
            return DICTIONARY_EN_PORT;
        }

        if (lang.equals("FR")) {
            translations.put("pies", "chien");
            translations.put("kot", "chat");
            translations.put("dom", "maison");
            return DICTIONARY_FR_PORT;
        }

        return DICTIONARY_EN_PORT;
    }

    public static void main(String[] args) throws Exception
    {
        String lang = "EN";
        if (args.length > 0) {
            lang = args[0];
        }

        Integer port = Dictionary.initialize(lang);

        try (var listener = new ServerSocket(port)) {
            System.out.println("The Dictionary (" + lang + ") server (" + port + ") is running...");
            var pool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
            while (true) {
                pool.execute(new Dict(listener.accept()));
            }
        }
    }

    private static class Dict implements Runnable
    {
        private Socket socket;

        Dict(Socket socket) {
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

                    System.out.println("Received [" + input + "] from Main");

                    Response response = new Response(input);
                    out.println(response.getContent());

                    if (response.getContent().equals("OK")) {

                        var request = new Request(response);

                        var socket = new Socket("127.0.0.1", request.getPort());
                        var socketInput = new PrintWriter(socket.getOutputStream(), true);
                        var socketOutput = new Scanner(socket.getInputStream());

                        socketInput.println(request.getContent());
                        System.out.println("Sent [" + request.getContent() + "] to Client");
                        System.out.println("Client response: " + socketOutput.nextLine());
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
        String message = "OK";
        String word;
        Integer port;

        Response(String input)
        {
            Pattern pattern = Pattern.compile("(.*?);(\\d*?)");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                this.message = "Wrong format";
            }

            this.word = matcher.group(1);
            this.port = Integer.valueOf(matcher.group(2));
        }

        String getContent() {
            return message;
        }

        String getWord() {
            return word;
        }

        Integer getPort() {
            return port;
        }
    }

    private static class Request
    {
        private Integer port;
        private String word;
        private String translation = "----";

        Request(Response response)
        {
            this.port = response.getPort();
            this.word = response.getWord();

            if (translations.containsKey(this.word)) {
                this.translation = translations.get(this.word);
            }
        }

        String getContent()
        {
            return this.word + ";" + this.translation;
        }

        Integer getPort() {
            return port;
        }

    }

}
