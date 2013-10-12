package rmiServer;

import interfaces.IHddServer;
import interfaces.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import server.HddServer;
import containerClasses.HddEntry;

public class RmiServer extends UnicastRemoteObject implements ServerInterface {

	public boolean state;
	private final IHddServer hddServer;

	public RmiServer(ArrayList<HddEntry> hddList) throws RemoteException {
		super();
		System.out.println("Sever is running, and waiting for client's!");
		this.hddServer = new HddServer();
		this.hddServer.setHddList(hddList);
	}

	@Override
	public ArrayList<HddEntry> getHddList() throws RemoteException {
		return hddServer.getHddList();
	}

	@Override
	public void setHddList(ArrayList<HddEntry> hddList) throws RemoteException {
		hddServer.setHddList(hddList);
	}

	@Override
	public void update(Boolean state) throws RemoteException {
		hddServer.update();
	}
}
