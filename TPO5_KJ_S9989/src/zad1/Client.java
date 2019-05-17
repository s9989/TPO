package zad1;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class Client
{
    static private GraphicsConfiguration gc;

    public static void  main( String args[] )
    {
        Context ic;
        Object objref;
        PhoneDirectoryInterface phoneDirectory;

        try {
            ic = new InitialContext();

            objref = ic.lookup("PhoneBookService");
            System.out.println("Client: Obtained a ref. to PhoneBookServer server.");

            // STEP 2: Narrow the object reference to the concrete type and
            // invoke the method.
            phoneDirectory = (PhoneDirectoryInterface) PortableRemoteObject.narrow(
                    objref, PhoneDirectoryInterface.class);

            renderGui(phoneDirectory);

            /*
            String number;
            phoneDirectory.addPhoneNumber("jake", "789");

            number = phoneDirectory.getPhoneNumber("anna");
            System.out.println(number);

            phoneDirectory.replacePhoneNumber("anna", "000");

            number = phoneDirectory.getPhoneNumber("anna");
            System.out.println(number);
            */

        } catch( Exception e ) {
            System.err.println( "Exception " + e + "Caught" );
            e.printStackTrace( );
            return;
        }
    }

    private static void renderGui(PhoneDirectoryInterface phoneDirectory)
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

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JPanel top = new JPanel();

        top.add(new JLabel("PhoneBook"));

        JButton read = new JButton("read");
        read.setBackground(Color.GREEN);
        top.add(read);

        JButton save = new JButton("save");
        save.setBackground(Color.RED);
        top.add(save);

        panel.add(top);

        JTextField ta_read = new JTextField();
        panel.add(ta_read);

        JTextField ta_write = new JTextField();
        panel.add(ta_write);

        top.putClientProperty("input", ta_read);
        top.putClientProperty("output", ta_write);

        listPane.add(panel);

        read.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton current = (JButton)e.getSource();
                JPanel parent = (JPanel) current.getParent();
                JTextField inputField = (JTextField) parent.getClientProperty("input");
                JTextField outputField = (JTextField) parent.getClientProperty("output");
                try {
                    String value = phoneDirectory.getPhoneNumber(inputField.getText());
                    outputField.setText(value);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Connection error");
                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton current = (JButton)e.getSource();
                JPanel parent = (JPanel) current.getParent();
                JTextField inputField = (JTextField) parent.getClientProperty("input");
                JTextField outputField = (JTextField) parent.getClientProperty("output");
                try {
                    String value = phoneDirectory.getPhoneNumber(inputField.getText());
                    if (null == value) {
                        phoneDirectory.addPhoneNumber(inputField.getText(), outputField.getText());
                        JOptionPane.showMessageDialog(null, "Added");
                    } else {
                        phoneDirectory.replacePhoneNumber(inputField.getText(), outputField.getText());
                        JOptionPane.showMessageDialog(null, "Replaced");
                    }

                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Connection error");
                }
            }
        });

        frame.add(listPane);

        frame.pack();
        frame.setVisible(true);
    }
}
