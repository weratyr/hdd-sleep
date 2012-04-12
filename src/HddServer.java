import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map.Entry;

public class HddServer {
	private final ServerSocket server;
	private String clientMSG;
	private final Hashtable<String, Integer> mountPoints;

	public HddServer(int port, Hashtable<String, Integer> mountPoints) throws IOException {
		server = new ServerSocket(port);
		this.mountPoints = mountPoints;
	}

	public ServerSocket getServer() {
		return server;
	}

	public Socket waitingForClients(ServerSocket serverSocket)
			throws IOException {
		Socket socket = serverSocket.accept();
		return socket;
	}

	public void writeMessage(Socket socket, String message) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		printWriter.println(message);
		printWriter.flush();
	}

	public void readMessage(Socket socket) {
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String message = "";
			message = bufferedReader.readLine();
				System.out.println(message);
				clientMSG = message;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InputStream executeCmd(String[] command) {
		try {
			// System.out.println("cmd [0]: " + command[0] + command[1]);
			Process child = Runtime.getRuntime().exec(command);
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					child.getErrorStream()));
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
			String[] cmd = { "/bin/bash", "-c",
					"df -h|grep -c " + mountPoint };
			BufferedReader br = new BufferedReader(new InputStreamReader(executeCmd(cmd)));
			while ((line = br.readLine()) != null) {
				if (line.equals("1"))
					return true;
				else
					return false;
			}
		} catch (IOException e) { e.printStackTrace(); }
		return false;
	}

	public void run() {
		String[] hdds;
		while (true) {
			try {
				Socket client = waitingForClients(getServer());
				readMessage(client);
				hdds = clientMSG.split(" ");
				char[] action = hdds[0].toCharArray();
				switch (action[0]) {
				case '1':
					// check current status
					for (Entry<String, Integer> entry : mountPoints.entrySet()) {
						System.out.println(entry.getKey());
						if (getStatus(entry.getKey())) {
							mountPoints.put(entry.getKey(), 1);
						} else {
							mountPoints.put(entry.getKey(), 0);
						}
						clientMSG += entry.getKey()+"="+entry.getValue()+" ";
					}
					writeMessage(client, clientMSG);
					break;
				case '2':
					for (int i = 1; i < hdds.length; i++) {
						String[] cmd = { "/usr/local/bin/hddsleep.sh", "start", hdds[i] };
						executeCmd(cmd);
					}
					break;
				case '3':
					for (int i = 1; i < hdds.length; i++) {
						String[] cmd = { "/usr/local/bin/hddsleep.sh", "stop",
								hdds[i] };
						executeCmd(cmd);
					}
					break;
				}
				client.close();
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

	public static void main(String[] arg) throws Exception {

		int port = 8001;

		Hashtable<String, Integer> mountPoints = new Hashtable<String, Integer>();
		for(int i=0; i< arg.length; i++) {
			mountPoints.put(arg[i], 0);
			System.out.println(arg[i]);
		}
		if(mountPoints.isEmpty()) {
			System.out.println("No mountpoints avaiable!!!");
			System.exit(0);
		}

		System.out.println("Sever is running, and waiting for client's!");
		HddServer hddServer = new HddServer(port,mountPoints);
		hddServer.run();

	}

}
