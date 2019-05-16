package zad1;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

public class Client
{
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

            String number;
            phoneDirectory.addPhoneNumber("jake", "789");

            number = phoneDirectory.getPhoneNumber("anna");
            System.out.println(number);

            phoneDirectory.replacePhoneNumber("anna", "000");

            number = phoneDirectory.getPhoneNumber("anna");
            System.out.println(number);

        } catch( Exception e ) {
            System.err.println( "Exception " + e + "Caught" );
            e.printStackTrace( );
            return;
        }
    }
}
