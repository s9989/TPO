package zad1.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class Listener {

    static private GraphicsConfiguration gc;

    private static SocketChannel channel;
    private static ByteBuffer buffer;
    private static Listener instance;

    private HashMap<String, TextArea> textAreas = new HashMap<>();

    private static Listener start() {
        if (instance == null)
            instance = new Listener();

        return instance;
    }

    public static void stop() throws IOException {
        channel.close();
        buffer = null;
    }

    private void writeToChannel(SocketChannel channel, String message) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.write(buffer);
    }

    private void renderGui(String[] topics)
    {
        JFrame frame = new JFrame(gc);
        frame.setTitle("Client");
        frame.setSize(400, 300);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        for (String topic : topics) {

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            JPanel top = new JPanel();

            top.add(new JLabel(topic));

            JButton subscribe = new JButton("subscribe");
            subscribe.setBackground(Color.LIGHT_GRAY);
            top.add(subscribe);

            JButton unsubscribe = new JButton("unsubscribe");
            unsubscribe.setBackground(Color.RED);
            top.add(unsubscribe);

            panel.add(top);
            top.putClientProperty("sub", subscribe);
            top.putClientProperty("unsub", unsubscribe);
            top.putClientProperty("topic", topic);

            TextArea ta = new TextArea();
            ta.setColumns(50);
            ta.setRows(2);
            panel.add(ta);

            textAreas.put(topic, ta);

            listPane.add(panel);

            unsubscribe.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton current = (JButton)e.getSource();
                    current.setBackground(Color.RED);

                    JPanel parent = (JPanel) current.getParent();
                    JButton opposite = (JButton) parent.getClientProperty("sub");
                    opposite.setBackground(Color.LIGHT_GRAY);
                    String topic = (String) parent.getClientProperty("topic");

                    try {
                        writeToChannel(channel, String.join("°", "unsubscribe", topic));
                    } catch (IOException ex) {
                        // bez obsługi
                    }
                }
            });

            subscribe.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton current = (JButton)e.getSource();
                    current.setBackground(Color.GREEN);

                    JPanel parent = (JPanel) current.getParent();
                    JButton opposite = (JButton) parent.getClientProperty("unsub");
                    opposite.setBackground(Color.LIGHT_GRAY);

                    try {
                        writeToChannel(channel, String.join("°", "subscribe", topic));
                    } catch (IOException ex) {
                        // bez obsługi
                    }
                }
            });
        }

        frame.add(listPane);

        frame.pack();
        frame.setVisible(true);
    }

    private Listener() {

        try {
            channel = SocketChannel.open(new InetSocketAddress("localhost", 5456));

            var b1 = ByteBuffer.wrap(("introduce°reader").getBytes());
            channel.write(b1);

            var b = ByteBuffer.allocate(256);
            channel.read(b);
            String response = new String(b.array()).trim();
            System.out.println(response);

            renderGui(response.split(";"));

//            var b2 = ByteBuffer.wrap((new String("subscribe°sport")).getBytes());
//            channel.write(b2);

            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(String data)
    {
        String topicMessageSplitter = "¦";

        if (!data.contains(topicMessageSplitter)) {
            return;
        }

        String topic = data.split(topicMessageSplitter)[0];
        String message = data.split(topicMessageSplitter)[1];

        TextArea ta = textAreas.get(topic);
        ta.append(message);
        ta.append("\n");
    }

    private void listen() throws IOException {

        String response;
        while (true) {
            channel.read(buffer);
            if (buffer.position() > 0) {
                response = new String(buffer.array()).trim();

                System.out.println("Server message: " + response);
                handleResponse(response);

                for(int i = 0; i < buffer.position(); i++) {
                    buffer.put(i, (byte) 0);
                }
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
