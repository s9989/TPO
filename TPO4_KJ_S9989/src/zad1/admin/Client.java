package zad1.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    static private GraphicsConfiguration gc;

    private static SocketChannel channel;
    private static Client instance;

    private JPanel mainPanel;

    public static Client start() {
        if (instance == null)
            instance = new Client();

        return instance;
    }

    public static void stop() throws IOException {
        channel.close();
    }

    private void writeToChannel(SocketChannel channel, String message) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.write(buffer);
    }

    private JPanel generateSection(String topic)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel top = new JPanel();

        top.add(new JLabel(topic));

        JButton send = new JButton("send");
        send.setBackground(Color.GREEN);
        top.add(send);

        JButton delete = new JButton("delete");
        delete.setBackground(Color.RED);
        top.add(delete);

        panel.add(top);
        top.putClientProperty("topic", topic);

        TextArea ta = new TextArea();
        ta.setColumns(50);
        ta.setRows(2);
        panel.add(ta);

        top.putClientProperty("textArea", ta);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton current = (JButton)e.getSource();
                JPanel parent = (JPanel) current.getParent();

                String topic = (String) parent.getClientProperty("topic");
                TextArea ta = (TextArea) parent.getClientProperty("textArea");

                String message = String.join("¦", topic, ta.getText());

                try {
                    writeToChannel(channel, String.join("°", "propagate", message));
                } catch (IOException ex) {
                    // bez obsługi
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton current = (JButton)e.getSource();
                JPanel parent = (JPanel) current.getParent();
                JPanel block = (JPanel) parent.getParent();
                JPanel frame = (JPanel) block.getParent();
                String topic = (String) parent.getClientProperty("topic");

                try {
                    writeToChannel(channel, String.join("°", "remove", topic));
                } catch (IOException ex) {
                    // bez obsługi
                }

                frame.remove(block);
                frame.validate();
                frame.revalidate();
            }
        });

        return panel;
    }

    private void renderGui(String[] topics)
    {
        JFrame frame = new JFrame(gc);
        frame.setTitle("Admin");
        frame.setSize(400, 800);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));



        JPanel menu = new JPanel();
        JTextField name = new JTextField();
        name.setColumns(25);
        menu.add(name);

        JButton add = new JButton("add");
        add.setBackground(Color.YELLOW);
        menu.add(add);

        menu.putClientProperty("text", name);

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JButton current = (JButton) e.getSource();
                JPanel menu = (JPanel) current.getParent();
                JPanel frame = (JPanel) menu.getParent();

                JTextField textField = (JTextField) menu.getClientProperty("text");

                String topic = textField.getText();

                mainPanel.add(generateSection(topic));

                try {
                    writeToChannel(channel, String.join("°", "add", topic));
                } catch (IOException ex) {
                    // bez obsługi
                }

                frame.validate();
                frame.revalidate();
            }
        });

        mainPanel.add(menu);


        for (String topic : topics) {
            JPanel panel = generateSection(topic);
            mainPanel.add(panel);
        }

        frame.add(mainPanel);

        frame.setVisible(true);
    }

    private Client() {
        try {
            channel = SocketChannel.open(new InetSocketAddress("localhost", 5456));

            var b1 = ByteBuffer.wrap(("introduce°reader").getBytes());
            channel.write(b1);

            var b = ByteBuffer.allocate(256);
            channel.read(b);
            String response = new String(b.array()).trim();
            System.out.println(response);

            renderGui(response.split(";"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Client.start();
    }

}
