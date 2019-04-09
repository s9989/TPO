package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(9991)) {
            System.out.println("The Main (9991) server is running...");
            var pool = Executors.newFixedThreadPool(20);
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

                    Response response = new Response(in.nextLine());

                    if (response.getMessage().equals("OK")) {
                        var socket = new Socket("127.0.0.1", 9992);
                        var socketInput = new PrintWriter(socket.getOutputStream(), true);
                        var socketOutput = new Scanner(socket.getInputStream());
                        socketInput.println(response.getWord() + ";" + response.getPort());
                        System.out.println("Odpowiedz dict: " + socketOutput.nextLine());
                    }

                    out.println(response.getMessage());
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
        String word, lang, port;

        Response(String input)
        {
            Pattern pattern = Pattern.compile("(.*?);(.*?);(\\d*?)");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                this.message = "Wrong format";
            }

            this.word = matcher.group(1);
            this.lang = matcher.group(2);
            this.port = matcher.group(3);
        }

        String getMessage() {
            return message;
        }

        String getWord() {
            return word;
        }

        String getLang() {
            return lang;
        }

        String getPort() {
            return port;
        }
    }

}
