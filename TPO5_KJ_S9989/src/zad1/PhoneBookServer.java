package zad1;

import javax.naming.Context;
import javax.naming.InitialContext;

public class PhoneBookServer
{
    private static final String filename = "dict.txt";

    public static void main(String[] args)
    {
        try {
            PhoneDirectory phoneDirectory = new PhoneDirectory(filename);

            Context initialNamingContext = new InitialContext();
            initialNamingContext.rebind("PhoneBookService", phoneDirectory );

            System.out.println("PhoneBookServer: ready.");

        } catch (Exception e) {
            System.out.println("Trouble: " + e);
            e.printStackTrace();
        }
    }
}
