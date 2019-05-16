package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Listener {

    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static Listener instance;

    public static Listener start() {
        if (instance == null)
            instance = new Listener();

        return instance;
    }

    public static void stop() throws IOException {
        client.close();
        buffer = null;
    }

    private Listener() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 5454));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() throws IOException {

        String response = null;
        while (true) {
            client.read(buffer);
            if (buffer.position() > 0) {
                response = new String(buffer.array()).trim();
                System.out.println("Server message: " + response);
                buffer.clear();
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        Listener listener = Listener.start();
        listener.listen();
    }

}
