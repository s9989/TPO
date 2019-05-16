//HelloInterface.java
import java.rmi.Remote;

public interface HelloInterface extends java.rmi.Remote
{
    void sayHello( String from ) throws java.rmi.RemoteException;
}