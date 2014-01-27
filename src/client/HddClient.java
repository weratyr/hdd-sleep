package client;

import interfaces.ServerInterface;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import containerClasses.HddEntry;

public class HddClient extends JFrame {

	private final JPanel infoPanel;
	private final JLabel status;
	private ArrayList<HddEntry> hddList;
	private final ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
	private ServerInterface server;
	// private final String rmiServerURL = "//127.0.0.1/Server";
	private final String rmiServerURL = "//10.10.1.58/Server";
	private Thread thread;

	public HddClient() {


		boolean sync = true;
		while (sync) {
			try {
				server = (ServerInterface) Naming.lookup(rmiServerURL);
				hddList = server.getHddList();
				sync = false;
			} catch (Exception ex) {
				int n = JOptionPane.showConfirmDialog(null, "Server not available!! Try reconnect?",
						"Error", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
			}
		}

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((dim.width) / 2 - this.getSize().width / 2, (dim.height) / 2
				- this.getSize().height / 2);
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		Button start = new Button("start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							for (int i = 0; i < checkBoxList.size(); i++) {
								if (checkBoxList.get(i).isSelected()) {
									hddList.get(i).setState(1);
								}
							}
							server.setHddList(hddList);
							server.update(true);

						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, "Server not available!! Restart this tool!!",
									"Error", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
							System.out.println("Server not available");
						}
					}
				});
				thread.start();
			}
		});
		cp.add(start, BorderLayout.EAST);

		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		status = new JLabel("<html><b>Status</b></html>");
		infoPanel.add(status);

		for (int i = 0; i < hddList.size(); i++) {
			checkBoxList.add(new JCheckBox(hddList.get(i).getName()));
			if (hddList.get(i).getState() == 1) {
				checkBoxList.get(i).setSelected(true);
			}
			infoPanel.add(checkBoxList.get(i));
		}

		cp.add(infoPanel, BorderLayout.CENTER);
		setVisible(true);
		setTitle("Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

	}

	public static void main(String[] args) {
		// HddClient hddClient = new HddClient();
		new HddClient();
	}

}
