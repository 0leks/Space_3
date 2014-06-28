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
		switch(upgrade) {
			case TIMETOSPAWN:
				return level;
			case HEALTH:
				return 1+level/5;
			case DAMAGE:
				return level;
			case SPEED:
				return level;
			case SHOOTINGSPEED:
				return level;
			case RANGE:
				return 1+level/2;
			default:
				return 0;
		}
	}
}
