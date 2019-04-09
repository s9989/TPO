package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dictionary {

    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(9992)) {
            System.out.println("The Dictionary server (9992) is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new Dict(listener.accept()));
            }
        }
    }

    private static class Dict implements
            Runnable
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
                    Response response = new Response(in.nextLine());
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
        String word, port;

        public Response(String input)
        {
            Pattern pattern = Pattern.compile("(.*?);(\\d*?)");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                this.message = "Wrong format";
            }

            this.word = matcher.group(1);
            this.port = matcher.group(2);
        }

        public String getMessage() {
            return message;
        }

        public String getWord() {
            return word;
        }

        public String getPort() {
            return port;
        }
    }

}
