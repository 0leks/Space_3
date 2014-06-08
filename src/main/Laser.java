package main;

import java.io.Serializable;

public class Laser implements Serializable{
	public int from;
	public int to;
	public int width;
	public int ttl;
	public int damage;
	public transient Base source;
	public Laser(int f, int t, int tt, int sdamage) {
		from = f;
		to = t;
		ttl = tt;
		damage = sdamage;
		width = 1;
	}
	public boolean widen() {
		width++;
		if(width>ttl) {
			return true;
		}
		return false;
	}
}
