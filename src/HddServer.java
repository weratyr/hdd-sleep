import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 *  Avalable Commands via hashmanp Arraylist etc
 * 	- client & server know these commands
 * 	- client sends a command list within the seralized objekt,
 *    so the server will generic and just execute that liste
 */
/* TODO
 *  - Create Conainter for hdd devices from Text File
 */

public class HddServer {
	private final ServerSocket server;
	public ArrayList<HddEntry> hddList;
	private ArrayList<ArrayList<String>> recvHdd;
	private final ServerCommand<String, String[]> commands;
	private final ArrayList<Connect> clientList;
	private final ArrayList<String> clientCheckboxNames;

	public HddServer(int port) throws IOException {
		server = new ServerSocket(port);
		clientList = new ArrayList<Connect>();
		hddList = new ArrayList<HddEntry>();
		hddList.add(new HddEntry(1, "ata-SAMSUNG_HD154UI_S1Y6J1KS701423", "de57a813-26d2-43d1-af3e-3267bd0237be",
				"/mnt/1500gb", "Home/Apps"));
		hddList.add(new HddEntry(2, "ata-SAMSUNG_HD203WI_60071C48AA1W90", "b576c1ab-dd29-4241-ad6b-481be81efb05",
				"/mnt/2000gb", "Filme2"));
		hddList.add(new HddEntry(3, "ata-SAMSUNG_HD154UI_S1Y6J1KS701422", "7ca46770-072c-4948-8b6b-420e2ea256fe",
				"/mnt/backup1500gb", "Backup"));

		clientCheckboxNames = new ArrayList<String>();
		clientCheckboxNames.add("Home/Apps");
		clientCheckboxNames.add("Filme2");
		clientCheckboxNames.add("Backup");
		clientCheckboxNames.add("test");

		commands = new ServerCommand<String, String[]>();
		commands.put("start", new String[] { "mount", "devParti", "mountpoint" });
		commands.put("stop", new String[] { "umount", "devParti", "hdparm -Y", "devParti" });
		commands.put("statusMountpoint", new String[] { "/bin/bash -c", "df -h|grep -c " });
		commands.put("statusHdparm", new String[] { "hdparm -C ", "hddID", "grep is:",
				"|awk -F\" \" '{print $4}'" });

	}

	public boolean isAvailable(String ip) {
		try {
			String[] ipSplit = ip.split("\\.");
			byte[] ipByte = new byte[] { (byte) Integer.parseInt(ipSplit[0]),
					(byte) Integer.parseInt(ipSplit[1]), (byte) Integer.parseInt(ipSplit[2]),
					(byte) Integer.parseInt(ipSplit[3]) };
			InetAddress address = InetAddress.getByAddress(ip, ipByte);
			if (address.isReachable(500)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String[] prepareForExecute(ArrayList<ArrayList<String>> hddList) {

		for(int i = 0; i < hddList.size(); i++) {
			if(hddList.get(i).size()==2) {
				String action = hddList.get(i).get(1);
				String[] cmd = commands.get(action);
				for(int j = 0; j < cmd.length; j++) {
					cmd[j].replace("devParti", "iiiiiiiiiii");
					System.out.println(cmd[j]);
				}

			} else {
				System.out.println("hddList uncomplete !!!");
			}

		}





		String[] cmd = null;
//		String action = commandList.get(1);
//		for (int j = 0; j < hddList.size(); j++) {
//			if (commands.containsKey(action)) {
//				cmd = commands.get(commandList.get(1));
//				if (hddList.get(j).getName().equals(commandList.get(0))) { // Checkbox-Name
//					for (int i = 0; i < cmd.length; i++) {
//						if (action.equals("start")) {
//							if (i == 1) {
//								cmd[i] = hddList.get(j).getUuidPartition();
//							}
//							if (i == 2) {
//								cmd[i] = hddList.get(j).getMountPoint();
//							}
//						} else if (action.equals("stop")) {
//							if (i == 1) {
//								cmd[i] = hddList.get(j).getUuidPartition();
//							}
//							if (i == 3) {
//								cmd[i] = hddList.get(j).getUuidDevice();
//							}
//						} else if (action.equals("status")) {
//
//						} else {
//							System.out.println("no action matched!!!");
//						}
//
//					}
//				}
//			}
//		}
//		System.out.println(cmd.length);
//		for (int x = 0; x < cmd.length; x++) {
//			System.err.print(cmd[x] + " ");
//		}
		return cmd;
	}

	public InputStream executeCmd(String[] cmdList) {
		try {

			Runtime child = Runtime.getRuntime();
			Process p1 = child.exec(cmdList);
			if (p1.waitFor() == 1) {
				System.out.println("executing successfull");
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
				while ((line = br.readLine()) != null) {
					System.out.println("cmd: " + line);
				}
			}
			InputStream is = p1.getInputStream();
			return is;
		} catch (Exception e) {
			System.out.println("war nix:: " + e.getMessage());
			e.printStackTrace();
			// e.getCause();
			return null;
		}
	}

	// public Boolean getStatus(String mountPoint) {
	// String line;
	// try {
	// String[] cmd = { "/bin/bash", "-c", "df -h|grep -c " + mountPoint };
	// BufferedReader br = new BufferedReader(new
	// InputStreamReader(executeCmd(cmd)));
	// while ((line = br.readLine()) != null) {
	// if (line.equals("1"))
	// return true;
	// else
	// return false;
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return false;
	// }

	public void run() {

		while (true) {
			try {
				Socket client = server.accept();
				System.out.println(client.getLocalPort());
				Connect connectionThread = new Connect(client);
				// clientList.add(e)
				// for(int i=0; i<clientList.size(); i++) {
				// clientList.get(i).start();
				// }
				System.out.println("next round");
				Thread.sleep(2000);
			} catch (Exception e) {
				try {
					Thread.sleep(5000);
					System.out.println("exception ");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	class Connect extends Thread {
		private Socket client = null;
		private ObjectInputStream ois = null;
		private ObjectOutputStream oos = null;
		private boolean aliveClient;

		public Connect() {
		}

		public Connect(Socket clientSocket) {
			client = clientSocket;
			try {
				ois = new ObjectInputStream(client.getInputStream());
				oos = new ObjectOutputStream(client.getOutputStream());
			} catch (Exception e1) {
				try {
					client.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return;
			}
			this.start();
		}

		public boolean isAliveClient() {
			return aliveClient;
		}

		public void setAliveClient(boolean alive) {
			this.aliveClient = alive;
		}

		@Override
		public void run() {

			clientList.add(this);
			aliveClient = true;
			try {
				oos.writeObject(clientCheckboxNames);
				oos.flush();
				System.out.println("write Config to client");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			while (aliveClient) {
				try {
					System.out.println("Read Objekt loop kdkdkdk");
					recvHdd = (ArrayList<ArrayList<String>>) ois.readObject();

					if (recvHdd.size() > 0) {
						// catch exeption when client is e ...
						if (recvHdd.get(0).get(0).equals("close")) {
							System.out.println("Client close connection !!!");
							closeClientConnection();
							System.out.println("Delete client from clientlist !!!");
						} else {
								prepareForExecute(recvHdd);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					closeClientConnection();
					if (client.isClosed()) {
						System.out.println("client is dead");
					}
				}
			}
			closeClientConnection();
		}

		public void closeClientConnection() {
			try {
				ois.close();
				oos.close();
				client.close();
				aliveClient = false;
				clientList.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	public static void main(String[] arg) throws Exception {

		// default port
		int port = 8001;
		for (int i = 0; i < arg.length; i++) {
			port = Integer.parseInt(arg[i]);
			// System.out.println(arg[i]);
		}
		System.out.println("Sever is running on " + port + ", and waiting for client's!");
		HddServer hddServer = new HddServer(port);
		hddServer.run();
	}

}
