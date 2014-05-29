package main;

import java.awt.Rectangle;
import java.io.Serializable;

public class Base implements Serializable{
	private int x, y;
	private int width, height;
	private Rectangle bounds;
	private Player player;
	public int id;
	public static int sid;
	public Base(Player mine, int sx, int sy, int sw, int sh) {
		id = sid++;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		bounds = new Rectangle(x, y, width, height);
		player = mine;
	}
	public String toString() {
		return player+","+x+","+y+","+width+","+height;
	}
	public void become(Base other) {
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
		this.player = other.player;
		this.id = other.id;
	}
	public boolean collides(Rectangle asdf) {
		if(asdf.intersects(this.getBounds())) {
			return true;
		}
		return false;
	}
	public Rectangle getBounds() {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		return bounds;
	}
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Player getPlayer() { return player; }
}
