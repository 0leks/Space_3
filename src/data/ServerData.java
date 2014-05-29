package data;

import java.io.Serializable;

public class ServerData implements Serializable{
	public String players;
	public int radius;
	public String ip;
	public boolean gamestarted;
	public String getServerData() {
		String s = "IP="+ip+"\nRadius="+radius+"\nGamestarted="+gamestarted;
		return s;
	}
	//asdf
}
