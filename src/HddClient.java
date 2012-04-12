import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HddClient extends JFrame {

	private final JPanel infoPanel;
	private final JCheckBox homeStatus;
	private final JCheckBox backup1500gbStatus;
	private final JCheckBox filme1tbStatus;
	private final JLabel status;
	private Socket socket;
	private String m;

	public HddClient() {
		//setPreferredSize(new Dimension(310, 180));
		setSize(new Dimension(310,180));
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		Button start = new Button("start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				String msgToServer = selectedHdds("2");
				System.out.println("write to server " + msgToServer);
				writeMessage(socket, msgToServer);
				closeConnection();
			}
		});
		cp.add(start, BorderLayout.EAST);

		Button stop = new Button("stop");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				String msgToServer = selectedHdds("3");
				System.out.println("write to server " + msgToServer);
				writeMessage(socket, msgToServer);
				closeConnection();
			}
		});


		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		status = new JLabel("<html><b>Status</b></html>");
		infoPanel.add(status);
		homeStatus = new JCheckBox("Home/Apps");
		infoPanel.add(homeStatus);
		filme1tbStatus = new JCheckBox("Filme 2tb: ");
		//filme1tbStatus.setEnabled(false);
		infoPanel.add(filme1tbStatus);
		backup1500gbStatus = new JCheckBox("Backup HDD: ");
		//backup1500gbStatus.setEnabled(false);
		infoPanel.add(backup1500gbStatus);
		Button status = new Button("check status");
		status.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectToServer();
				writeMessage(socket, "1");
				readMessage(socket);
				closeConnection();
			}
		});
		infoPanel.add(status);
		cp.add(infoPanel, BorderLayout.CENTER);
		cp.add(stop, BorderLayout.WEST);

		setVisible(true);
		setTitle("Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//pack();

	}

	public String selectedHdds(String action) {
		String msgToServer = action + " ";
		if (homeStatus.isSelected()) {
			msgToServer += "1500gb ";
		}
		if (backup1500gbStatus.isSelected()) {
			msgToServer += "backup1500gb ";
		}
		if (filme1tbStatus.isSelected()) {
			msgToServer += "2000gb ";
		}
		if (msgToServer.equals(action + " ")) {
			msgToServer = action + " 1500gb 2000gb";
		}
		return msgToServer;
	}

	public void connectToServer() {
		//String ip = "127.0.0.1"; // localhost
		String ip = "10.10.1.58";
		// InetAddress ip;
		try {
			// ip = InetAddress.getByName("weratyr.ath.cx");
			int port = 8001;
			socket = new Socket(ip, port); // verbindet sich mit Server
			System.out.println("conneced");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void writeMessage(Socket socket, String message) {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()),true);
			printWriter.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readMessage(Socket socket) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String message = bufferedReader.readLine(); // blockiert
			System.out.println(message);
			setStatus(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStatus(String msg) {
		String[] msgKa = msg.split(" ");
		String[] msgkaSi = msgKa[0].split("=");
		//  1/mnt/1500gb=1 /mnt/2000gb=1 /mnt/backup1500gb=0
		if (msgkaSi[1].equals("1")) {
			homeStatus.setSelected(true);
		} else
			homeStatus.setSelected(false);
		msgkaSi = msgKa[1].split("=");
		if (msgkaSi[1].equals("1")) {
			filme1tbStatus.setSelected(true);
		} else
			filme1tbStatus.setSelected(false);
		msgkaSi = msgKa[2].split("=");
		if (msgkaSi[1].equals("1")) {
			backup1500gbStatus.setSelected(true);
		} else
			backup1500gbStatus.setSelected(false);
	}

	public static void main(String[] args) {
		HddClient hddClient = new HddClient();
	}

}
