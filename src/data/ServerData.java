package data;

import java.io.Serializable;

public class ServerData implements Serializable{
	public String players;
	public int radius;
	public int startingmoney;
	public String ip;
	public boolean gamestarted;
	public String getServerData() {
		String s = "IP="+ip+"\nRadius="+radius+"\nStarting $="+startingmoney+"\nGamestarted="+gamestarted;
		return s;
	}
	//asdf
}
