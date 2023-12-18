package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote{
	void createQueue(String userName) throws RemoteException;
	void saveMessage(String userName, String message) throws RemoteException;
	List<String> getUserMessages(String userName) throws RemoteException;
}
