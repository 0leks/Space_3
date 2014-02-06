package main;

import java.io.Serializable;

public class Base implements Serializable{
	private int x, y;
	private int width, height;
	Player player;
	public Base(Player mine, int sx, int sy, int sw, int sh) {
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		player = mine;
	}
	public String toString() {
		return player+","+x+","+y+","+width+","+height;
	}
}
