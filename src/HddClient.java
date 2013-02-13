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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class HddClient extends JFrame {

	private final JPanel infoPanel = new JPanel();
	private Socket socket;
	private ArrayList<JCheckBox> checkboxList;
	private final String[] cmdList = { "start", "stop", "status" };
	private ArrayList<String> checkboxNames;
	private String cmd;
	int max_width, max_height;
	Popup popup;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public HddClient() {

	}
/**
 * Load JFrame with delay
 */
	public void loadLater() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				Toolkit kit = getToolkit();
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] gs = ge.getScreenDevices();
				Insets in = kit.getScreenInsets(gs[0].getDefaultConfiguration());
				Dimension d = kit.getScreenSize();
				max_width = (d.width - in.left - in.right);
				max_height = (d.height - in.top - in.bottom);
				setLocation((max_width - getWidth()) / 2, (max_height - getHeight()) / 2);

				// connect to server
				connectToServer();
				// read config
				readObjektFromSocket();

				addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent event) {
						System.out.println("exit application");
						try {
							ArrayList<String> element = new ArrayList<String>();
							element.add("close");
							ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
							temp.add(element);
							oos.writeObject(temp);
							oos.flush();
							ois.close();
							oos.close();
							socket.close();
							dispose();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

				Container cp = getContentPane();
				cp.setLayout(new BorderLayout());

				Button start = new Button("start");
				start.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// connectToServer();
						cmd = cmdList[0];
						writeObjektToSocket();
						closeConnection();
					}
				});

				Button stop = new Button("stop");
				stop.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// connectToServer();
						cmd = cmdList[1];
						writeObjektToSocket();
						closeConnection();
					}
				});
				Button statusButton = new Button("check status");
				statusButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// connectToServer();
						cmd = cmdList[2];
						writeObjektToSocket();
						closeConnection();
					}
				});

				infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
				JLabel status = new JLabel("<html><b>Status</b></html>");
				infoPanel.add(status);

				checkboxList = new ArrayList<JCheckBox>();
				JCheckBox tempCheckbox;
				for (int i = 0; i < checkboxNames.size(); i++) {
					tempCheckbox = new JCheckBox(checkboxNames.get(i));
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
				// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				pack();
			}
		});

	}

	public void writeObjektToSocket() {
		try {
			System.out.println("Client: write to Socket");
			oos.writeObject(selectedHdds(cmd));
			oos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void readObjektFromSocket() {
		try {
			System.out.println("Client: read from Socket");
			checkboxNames = (ArrayList<String>) ois.readObject();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public ArrayList<ArrayList<String>> selectedHdds(String action) {
		ArrayList<ArrayList<String>> selectedHddList = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < checkboxList.size(); i++) {
			if (checkboxList.get(i).isSelected()) {
				ArrayList<String> cmd = new ArrayList<String>();
				cmd.add(checkboxNames.get(i));
				cmd.add(action);
				selectedHddList.add(cmd);
			}
		}
		return selectedHddList;
	}

	public void connectToServer() {
		String ip = "127.0.0.1"; // localhost
		// String ip = "10.10.1.58";
		try { // ip = InetAddress.getByName("weratyr.ath.cx");
			int port = 8001;
			socket = new Socket(ip, port); // verbindet sich mit Server
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("conneced");
		} catch (Exception e) {
			JPanel popupContainer = new JPanel();
			popupContainer.add(new JLabel("Could not connect!!!"));
			JButton reconn = new JButton("reconnect");
			reconn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					popup.hide();
					connectToServer();
					loadLater();
				}
			});
			popupContainer.add(reconn);

			PopupFactory factory = PopupFactory.getSharedInstance();
			popup = factory.getPopup(this, popupContainer, ((max_width - getWidth()) / 2) + 50,
					((max_height - getHeight()) / 2) + 55);
			popup.show();
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		// try {
		// socket.close();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
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
