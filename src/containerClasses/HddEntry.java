package containerClasses;

import java.io.Serializable;

public class HddEntry implements Serializable {

	private String name;
	private String mountPoint;
	private String hddName;
	private int state;

	public HddEntry() {

	}

	public HddEntry(String name, String mp, int state, String hddName) {
		this.name = name;
		this.mountPoint = mp;
		this.state = state;
		this.hddName = hddName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getHddName() {
		return hddName;
	}

	public void setHddName(String hddName) {
		this.hddName = hddName;
	}



}
