package data;

import java.io.Serializable;

public class ServerData implements Serializable{
	public String players;
	public int radius;
	public String ip;
	public String getServerData() {
		String s = "IP="+ip+"\nRadius="+radius;
		return s;
	}
}
