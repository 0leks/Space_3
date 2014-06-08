package data;

import java.awt.Graphics;

public class Upgrade {
	public int type;
	public static final int TIMETOSPAWN = 1;
	public static final int HEALTH = 2;
	public static final int DAMAGE = 3;
	public static final int SPEED = 4;
	public static final int SHOOTINGSPEED = 5;
	public static final int RANGE = 6;
	public Upgrade(int stype) {
		type = stype;
	}
}
