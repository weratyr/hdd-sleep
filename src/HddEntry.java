import java.io.Serializable;

public class HddEntry implements Serializable {
	int id;
	String uuidPartition;
	String uuidDevice;
	String mountPoint;
	String name;
	int flag;

	public HddEntry(int id, String uuidDevice, String uuidPartition, String mountPoint, String name) {
		this.id = id;
		this.uuidPartition = uuidPartition;
		this.uuidDevice = uuidDevice;
		this.mountPoint = mountPoint;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuidPartition() {
		return uuidPartition;
	}

	public void setUuidPartition(String uuidPartition) {
		this.uuidPartition = uuidPartition;
	}

	public String getUuidDevice() {
		return uuidDevice;
	}

	public void setUuidDevice(String uuidDevice) {
		this.uuidDevice = uuidDevice;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}



}
