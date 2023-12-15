package main;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Controller implements User {
	public List<String> contactsList = new ArrayList<>();
	public Consumer<String> messageConsumer;
	public boolean isUserOn = false;
	public String userName = "";
	
	private Registry registry;
	private Remote remoteObject;
	
	private Server server;
	

	public Controller() {
		try {
			registry = LocateRegistry.getRegistry();
			remoteObject = UnicastRemoteObject.exportObject(this, 0);
			server = (Server) registry.lookup("Server");
			
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Erro initializeRPC");
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(String message) throws RemoteException {
		messageConsumer.accept(message + "\n");
	}

	public void changeStatusUser() {
		if (isUserOn) {
			try {
				registry.unbind(userName);
				isUserOn = false;

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				registry.rebind(userName, remoteObject);
				isUserOn = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	public void sendMessageToUser(String user, String message) throws RemoteException {
		try {
			User client = (User) registry.lookup(user);
			client.sendMessage("mensagem de " + userName + ": " + message);

		} catch (Exception e) {
			sendMessageToServer(user, ("mensagem de " + userName + ": " + message));

		}

	}

	public void sendMessageToServer(String user, String message) {
		try {
			server.saveMessage(user, message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void createUserQueueAtServer() {
		try {
			server.createQueue(userName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public List<String> getUserMessagesAtServer() {
		try {
			return server.getUserMessages(userName);
		} catch (RemoteException e) {
			List<String> messagesError = new ArrayList<>();
			return messagesError;
		}
	}

}
