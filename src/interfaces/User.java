package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface User extends Remote {
	void sendMessage(String message) throws RemoteException;
}
