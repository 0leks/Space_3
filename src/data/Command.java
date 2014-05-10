package data;

import java.io.Serializable;

public class Command implements Serializable{
	public int type;
	public int x, y;
	public transient static final int CREATEUNIT = 1111;
	public transient static final int MOVE = 1112;
	public Command(int stype) {
		type = stype;
	}
	public Command(int stype, int x, int y) {
		type = stype;
		this.x = x;
		this.y = y;
	}
	public String toString() {
		if(type==MOVE) {
			return "Move("+x+","+y+")";
		}
		return "Command("+type+","+x+","+y+")";
	}
}
