package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final String EXIT_CODE = "EXIT";

    static Set<SelectionKey> selectedKeys;

    public static void main(String[] args) throws IOException
    {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {

            selector.select();
            selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    System.out.println("Registered: " + serverSocket);
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    answerWithEcho(buffer, key);
                }

//                iterator.remove();
            }

        }
    }

    private static void propagate() throws IOException
    {
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();

            sendMessage(key);

//            iterator.remove();
        }
    }

    private static void sendMessage(SelectionKey key) throws IOException
    {
        String message = "OK";

        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

        if (key.channel() instanceof ServerSocketChannel) {
            return; // no message to server
        }
        SocketChannel client = (SocketChannel) key.channel();

        System.out.println("Sending message to: " + client.getRemoteAddress().toString());
        client.write(buffer);
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException
    {
        SocketChannel client = (SocketChannel) key.channel();
        try {
            client.read(buffer);
        } catch (IOException ex) {
            System.out.println("Reading error: " + ex.getMessage());
            client.close();
        }
        if (buffer.position() == 0) {
            return;
        }
        String request = new String(buffer.array()).trim();
        System.out.println("Request: " + request);

        if (request.equals("propagate")) {
            propagate();
        }

        buffer.flip();
        client.write(buffer);
        System.out.println("Response: " + request);
        for(int i = 0; i < buffer.position(); i++) {
            buffer.put(i, (byte) 0);
        }
        buffer.clear();
//        client.close();
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException
    {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
//        client.close();
        System.out.println("Connected: " + client.getRemoteAddress().toString());
    }

}
