package interfaces;

import java.util.ArrayList;

import containerClasses.HddEntry;

public interface IHddServer {
	public void update();
	public ArrayList<HddEntry> getHddList();
	public void setHddList(ArrayList<HddEntry> hddList);

}
