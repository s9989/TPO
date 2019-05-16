package zad1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.rmi.PortableRemoteObject;

public class PhoneDirectory extends PortableRemoteObject implements PhoneDirectoryInterface
{
    private Map pbMap = new HashMap();

    public PhoneDirectory(String fileName) throws java.rmi.RemoteException
    {
        super();

        // Inicjalna zawartość książki telefonicznej
        // jest wczytywana z pliku o formacie
        //  imię  numer_telefonu
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(" +", 2);
                pbMap.put(info[0], info[1]);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
    }

    public String getPhoneNumber(String name) throws java.rmi.RemoteException
    {
        return (String) pbMap.get(name);
    }

    public boolean addPhoneNumber(String name, String num) throws java.rmi.RemoteException
    {
        if (pbMap.containsKey(name)) {
            return false;
        }

        pbMap.put(name, num);
        return true;
    }

    public boolean replacePhoneNumber(String name, String num) throws java.rmi.RemoteException
    {
        if (!pbMap.containsKey(name)) {
            return false;
        }

        pbMap.put(name, num);
        return true;
    }

}