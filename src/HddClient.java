import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/* TODO
 * - create Device - List
 *  - Text file ->  UUID  mountpoint  checkboxname
 *  								UUID  mountpoint  checkboxname
 *  - Create Conainter for hdd devices from Text File
 *
 *
 *
 */

@SuppressWarnings("serial")
public class HddClient extends JFrame {

	private final JPanel infoPanel = new JPanel();
	private Socket socket;
	private ArrayList<HddEntry> hddList;
	private ArrayList<JCheckBox> checkboxList;

	public HddClient() {
	}

	public void loadLater() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Toolkit kit = getToolkit();
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] gs = ge.getScreenDevices();
				Insets in = kit.getScreenInsets(gs[0].getDefaultConfiguration());
				Dimension d = kit.getScreenSize();
				int max_width = (d.width - in.left - in.right);
				int max_height = (d.height - in.top - in.bottom);
				setLocation((max_width - getWidth()) / 2, (max_height - getHeight()) / 2);

				Container cp = getContentPane();
				cp.setLayout(new BorderLayout());

				Button start = new Button("start");
				start.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connectToServer();
						writeObjektToSocket(1);
						closeConnection();
					}
				});

				Button stop = new Button("stop");
				stop.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connectToServer();
						writeObjektToSocket(0);
						closeConnection();
					}
				});
				Button statusButton = new Button("check status");
				statusButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connectToServer();
						writeObjektToSocket(3);
						closeConnection();
					}
				});

				infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
				JLabel status = new JLabel("<html><b>Status</b></html>");
				infoPanel.add(status);

				// generate dynamic checkboxes
				hddList = new ArrayList<HddEntry>();
				hddList.add(new HddEntry(1, "dksfa3942ksf-3234-5-", "de57a813-26d2-43d1-af3e-3267bd0237be",
						"/mnt/1500gb", "Home/Apps"));
				hddList.add(new HddEntry(2, "dksfa3942ksf-3234-5-", "b576c1ab-dd29-4241-ad6b-481be81efb05",
						"/mnt/2000gb", "Filme 2TB"));
				hddList.add(new HddEntry(3, "dksfa3942ksf-3234-5-", "7ca46770-072c-4948-8b6b-420e2ea256fe",
						"/mnt/backup1500gb", "Backup HDD"));

				checkboxList = new ArrayList<JCheckBox>();
				JCheckBox tempCheckbox;
				for (int i = 0; i < hddList.size(); i++) {
					tempCheckbox = new JCheckBox(hddList.get(i).getName());
					infoPanel.add(tempCheckbox);
					checkboxList.add(tempCheckbox);
					// j.addItemListener(this);
				}
				// infoPanel.add(statusButton);
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
				buttonPanel.add(start);
				buttonPanel.add(stop);
				buttonPanel.add(statusButton);

				cp.add(buttonPanel, BorderLayout.WEST);
				cp.add(infoPanel, BorderLayout.CENTER);

				setVisible(true);
				setTitle("Monitor");
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				pack();
			}
		});

	}

	public void writeObjektToSocket(int action) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(selectedHdds(action));
			oos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public ArrayList<HddEntry> selectedHdds(int action) {
		ArrayList<HddEntry> selectedHddList = new ArrayList<HddEntry>();
		for (int i = 0; i < checkboxList.size(); i++) {
			if (checkboxList.get(i).isSelected()) {
				hddList.get(i).setFlag(action);
				selectedHddList.add(hddList.get(i));
			}
		}
		System.out.println(selectedHddList.size());
		return selectedHddList;
	}

	public void connectToServer() {
		String ip = "127.0.0.1"; // localhost
		// String ip = "10.10.1.58";
		// InetAddress ip;
		try { // ip = InetAddress.getByName("weratyr.ath.cx");
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

	public void setStatus(String msg) {
		String[] msgKa = msg.split(" ");
		String[] msgkaSi = msgKa[0].split("=");
		// 1/mnt/1500gb=1 /mnt/2000gb=1 /mnt/backup1500gb=0
		// if (msgkaSi[1].equals("1")) {
		// homeStatus.setSelected(true);
		// } else
		// homeStatus.setSelected(false);
		// msgkaSi = msgKa[1].split("=");
		// if (msgkaSi[1].equals("1")) {
		// filme1tbStatus.setSelected(true);
		// } else
		// filme1tbStatus.setSelected(false);
		// msgkaSi = msgKa[2].split("=");
		// if (msgkaSi[1].equals("1")) {
		// backup1500gbStatus.setSelected(true);
		// } else
		// backup1500gbStatus.setSelected(false);
	}

	public static void main(String[] args) {
		HddClient hddClient = new HddClient();
		hddClient.loadLater();
	}

}
