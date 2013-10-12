package server;

import interfaces.IHddServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import rmiServer.RmiServer;
import containerClasses.HddEntry;

public class HddServer implements IHddServer {
	private ArrayList<HddEntry> hddList;

	public HddServer() {

	}

	@Override
	public void update() {
		for (HddEntry hdd : hddList) {
			if (hdd.getState() == 1) {
				executeCmd(new String[] { "/usr/local/bin/hddsleep.sh", "start", hdd.getHddName() });
			}
			System.out.println("Hdd Name:" + hdd.getHddName() + " Hdd state:" + hdd.getState());
		}
	}

	@Override
	public ArrayList<HddEntry> getHddList() {
		updateStatus();
		return hddList;
	}

	@Override
	public void setHddList(ArrayList<HddEntry> hddList) {
		this.hddList = hddList;
	}


	public void updateStatus() {
		for(HddEntry hdd : hddList) {
			if(getStatus(hdd.getMountPoint())) {
				hdd.setState(1);
			} else {
				hdd.setState(0);
			}
		}
	}

	public InputStream executeCmd(String[] command) {
		try {
			// System.out.println("cmd [0]: " + command[0] + command[1]);
			Process child = Runtime.getRuntime().exec(command);
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(child.getErrorStream()));
			while ((line = br.readLine()) != null) {
				System.out.println("cmd: " + line);
			}
			InputStream is = child.getInputStream();
			return is;
		} catch (Exception e) {
			e.getCause();
			System.out.println("war nix:: " + e.getMessage());
			return null;
		}
	}

	public Boolean getStatus(String mountPoint) {
		String line;
		try {
			String[] cmd = { "/bin/bash", "-c", "df -h|grep -c " + mountPoint };
			BufferedReader br = new BufferedReader(new InputStreamReader(executeCmd(cmd)));
			while ((line = br.readLine()) != null) {
				if (line.equals("1"))
					return true;
				else
					return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Inital Server instance
	 *
	 * @param mountpoint,name,hddName mountpoint,name,hddName
	 * @throws
	 */
	public static void main(String[] arg) throws Exception {

		ArrayList<HddEntry> hddList = new ArrayList<HddEntry>();
		for (int i = 0; i < arg.length; i++) {
			hddList
					.add(new HddEntry(arg[i].split(",")[1], arg[i].split(",")[0], 0, arg[i].split(",")[2]));
			System.out.println(arg[i].split(",")[2]);
		}
		if (hddList.isEmpty()) {
			System.out.println("No hdd entry avaiable!!!");
			System.out.println("list eg: mountpoint,name;mountpoint,name ");
			System.exit(0);
		}

		// Create registry for an port
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		}

		// Naming RMI Server and make it accessable
		try {
			Naming.rebind("Server", new RmiServer(hddList));
		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
		} catch (RemoteException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
