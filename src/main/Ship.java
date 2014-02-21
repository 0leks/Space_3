package main;

import java.io.Serializable;

public class Ship implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x, y;
	private int width, height;
	private Player player;
	public Ship(Player mine, int sx, int sy, int sw, int sh) {
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		player = mine;
	}
	public String toString() {
		return player+","+x+","+y+","+width+","+height;
	}
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Player getPlayer() { return player; }
}
