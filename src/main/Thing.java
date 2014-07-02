package main;

import java.io.Serializable;

public abstract class Thing implements Serializable{
	protected int id;
	public static int sid;
	public Thing() {
		id = sid++;
	}
	/**
	 * needs to be extended to reduce health appropriately
	 * @return
	 */
	public abstract boolean takeDamage(int damage);
	public int getID() { return id; }
	public abstract int getDistanceFrom(Ship other);
	public abstract int getDistanceFrom(Base other);
	public abstract int getLoot();
}
