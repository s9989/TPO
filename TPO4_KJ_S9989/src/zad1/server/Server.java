package zad1.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

class Server
{
    private ServerSocketChannel ssc;
    private Selector selector;

    private HashSet<String> topics = new HashSet<>();
    private HashSet<Feed> feeds = new HashSet<>();

    private String getTopics()
    {
        return String.join(";", topics);
    }

    Server(String host, int port)
    {
        topics.add("news");
        topics.add("weather");
        topics.add("sport");

        try {
            // Utworzenie kanału dla gniazda serwera
            ssc = ServerSocketChannel.open();

            // Tryb nieblokujący
            ssc.configureBlocking(false);

            // Ustalenie adresu (host+port) gniazda kanału
            ssc.socket().bind(new InetSocketAddress(host, port));

            // Utworzenie selektora
            selector = Selector.open();

            // Zarejestrowanie kanału do obsługi przez selektor
            ssc.register(selector, SelectionKey.OP_ACCEPT);

        } catch(Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
        System.out.println("Started listening");

        handleConnections();
    }

    private void handleConnections()
    {
        while(true) {
            try {
                // czeka na zajście  zdarzenia związanego z kanałami
                // zarejestrowanymi do obslugi przez selektor
                selector.select();

                // Coś się wydarzyło na kanałach
                // Zbiór kluczy opisuje zdarzenia
                Set keys = selector.selectedKeys();

                Iterator iter = keys.iterator();
                while (iter.hasNext()) {   // dla każdego klucza

                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();

                    if (key.isAcceptable()) { // jakiś klient chce się połączyć

                        // Uzyskanie kanału do komunikacji z klientem
                        SocketChannel cc = ssc.accept();

                        // Komunikacja z klientem - nieblokujące we/wy
                        cc.configureBlocking(false);

                        // rejestrujemy kanał komunikacji z klientem
                        cc.register(selector, SelectionKey.OP_READ);
                        System.out.println("Regitered new channel");
                        continue;
                    }

                    if (key.isReadable()) {  // któryś z kanałów gotowy do czytania
                        try {
                            handleRequest(key);
                        } catch (SelectionKeyException ex) {
                            SelectionKey damagedKey = ex.getKey();
                            damagedKey.channel().close();
                            continue;
                        }
                    }

                    if (key.isWritable()) {  // któryś z kanałów gotowy do pisania
                        try {
                            propagate(key);
                        } catch (SelectionKeyException ex) {
                            SelectionKey damagedKey = ex.getKey();
                            damagedKey.channel().close();
                            continue;
                        }
                    }
                }
            } catch(Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private void writeToChannel(SocketChannel channel, String message) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.write(buffer);
    }

    private String readFromChannel(SocketChannel channel) throws IOException
    {
        var buffer = ByteBuffer.allocate(256);
        channel.read(buffer);
        return new String(buffer.array()).trim();
    }

    private void handleRequest(SelectionKey key) throws SelectionKeyException
    {
        SocketChannel channel = (SocketChannel) key.channel();

        try {
            String line = readFromChannel(channel);

            System.out.println("Request: " + line);

            String commandDataSplitter = "°";

            if (!line.contains(commandDataSplitter)) {
                return; // nierozpoznawalny komunikat
            }

            String command = line.split(commandDataSplitter)[0];
            String data = line.split(commandDataSplitter)[1];

            if (command.equals("introduce") && data.equals("reader")) {
                writeToChannel(channel, getTopics());

                Feed feed = new Feed(topics);
                feeds.add(feed);

                key.attach(feed);
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }

            if (command.equals("info")) {
                writeToChannel(channel, "°" + getTopics());
            }

            if (command.equals("add")) {
                topics.add(data);
            }

            if (command.equals("remove")) {
                topics.remove(data);
            }

            if (command.equals("subscribe")) {

                if (null != key.attachment()) {
                    Feed feed = (Feed) key.attachment();
                    feed.subscribe(data);
                }

            }

            if (command.equals("unsubscribe")) {

                if (null != key.attachment()) {
                    Feed feed = (Feed) key.attachment();
                    feed.unsubscribe(data);
                }

            }

            if (command.equals("propagate")) {

                String topicMessageSplitter = "¦";
                String topic = data.split(topicMessageSplitter)[0];
                String message = data.split(topicMessageSplitter)[1];

                for (Feed feed : feeds)
                {
                    feed.putMessage(topic, message);
                }
            }

        } catch (IOException ex) {
            throw new SelectionKeyException(ex.getMessage(), ex, key);
        }
    }

    private void propagate(SelectionKey key) throws SelectionKeyException
    {
        SocketChannel channel = (SocketChannel) key.channel();

        if (null == key.attachment()) {
            return; // tylko rozpoznawalne kanały
        }

        Feed feed = (Feed) key.attachment();

        for (String message : feed.getMessages())
        {
            try {
                writeToChannel(channel, message);
//                System.out.println("Sent: " + message);
            } catch (IOException ex) {
                throw new SelectionKeyException(ex.getMessage(), ex, key);
            }
        }
    }
}
