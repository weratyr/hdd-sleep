import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HddServer {
	private final ServerSocket server;
	public ArrayList<HddEntry> hddList = new ArrayList<HddEntry>();

	public HddServer(int port) throws IOException {
		server = new ServerSocket(port);
	}

	public InputStream executeCmd(String[] command) {
		try {
			System.out.println("cmd [0]: " + command[0] + command[1]);
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

	public void run() {
		String[] hdds;
		while (true) {
			try {
				Socket client = server.accept();
				Connect connectionThread = new Connect(client);

				System.out.println("next round");
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

		@Override
		public void run() {
			try {
				System.out.println("Read Objekt");
				hddList = (ArrayList<HddEntry>) ois.readObject();
				System.out.println(hddList.size());

				for (int i = 0; i < hddList.size(); i++) {
					if (hddList.get(i).getFlag() == 1) {
						executeCmd(new String[] { hddList.get(i).getUuidPartition(),
								hddList.get(i).getMountPoint() });
					} else {
						executeCmd(new String[] { hddList.get(i).getUuidDevice(),
								hddList.get(i).getMountPoint() });
					}
				}

				// close streams and connections
				ois.close();
				oos.close();
				client.close();
			} catch (Exception e) {
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
