package main;

public class Explosion {
	public int x, y, radius, maxsize, d;
	public Explosion(int sx, int sy, int ms) {
		x = sx;
		y = sy;
		radius = 0;
		maxsize = ms;
		d = maxsize/15;
	}
	public boolean widen() {
		radius+=d;
		if(radius>maxsize) {
			radius = maxsize;
			d = -d;
		}
		if(radius<0) {
			return true;
		}
		return false;
	}
}
