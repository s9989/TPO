package zad1;

public interface PhoneDirectoryInterface extends java.rmi.Remote
{
    public String getPhoneNumber(String name) throws java.rmi.RemoteException;

    public boolean addPhoneNumber(String name, String num) throws java.rmi.RemoteException;

    public boolean replacePhoneNumber(String name, String num) throws java.rmi.RemoteException;
}
