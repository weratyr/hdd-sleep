package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import containerClasses.HddEntry;

public interface ServerInterface extends Remote {
	public ArrayList<HddEntry> getHddList() throws RemoteException;
	public void setHddList(ArrayList<HddEntry> hddList) throws RemoteException;
	public void update(Boolean state) throws RemoteException;

}
