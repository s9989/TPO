package zad1.server;

public class Main
{
    private static final String host = "127.0.0.1";
    private static final int port = 5456;

    public static void main(String[] args)
    {
        new Server(host, port);
    }
}
