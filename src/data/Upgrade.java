package data;

import java.awt.Graphics;
import java.io.Serializable;

import main.UpgradeType;

public class Upgrade implements Serializable{
	public UpgradeType upgrade;
	public int level;
//	public int type;
//	public static final int TIMETOSPAWN = 1;
//	public static final int HEALTH = 2;
//	public static final int DAMAGE = 3;
//	public static final int SPEED = 4;
//	public static final int SHOOTINGSPEED = 5;
//	public static final int RANGE = 6;
	public Upgrade(UpgradeType type, int slevel) {
		upgrade = type;
		level = slevel;
	}
	public int getCost() {
		int toreturn = 0;
		switch(upgrade) {
			case TIMETOSPAWN:
				toreturn = level*level/4;
			case HEALTH:
				toreturn = level/5;
			case DAMAGE:
				toreturn = level;
			case SPEED:
				toreturn = level;
			case SHOOTINGSPEED:
				toreturn = level*level/2;
			case RANGE:
				toreturn = level/2;
			default:
				toreturn = 0;
		}
		if(toreturn<=0) {
			toreturn = 1;
		}
		return toreturn;
	}
}
