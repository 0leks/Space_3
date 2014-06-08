package main;

import java.awt.Rectangle;
import java.io.Serializable;

import data.Upgrade;

public class Base implements Serializable{
	private static final long serialVersionUID = 1L;
	private int x, y;
	private int width, height;
	private Rectangle bounds;
	private Player player;
	private int timetospawn;
	private int TIMETOSPAWN;
	private int HEALTH;
	private int DAMAGE;
	private int SPEED;
	private int SHOOTINGSPEED;
	private int RANGE;
	private int WIDTH;
	public int id;
	public static int sid;
	public Base(Player mine, int sx, int sy, int sw, int sh) {
		id = sid++;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		HEALTH = 10;
		DAMAGE = 1;
		SPEED = 10;
		SHOOTINGSPEED = 10;
		RANGE = 100;
		WIDTH = 20;
		TIMETOSPAWN = 5;
		timetospawn = 0;
		bounds = new Rectangle(x, y, width, height);
		player = mine;
	}
	public void upgrade(Upgrade upgrade) {
		int t = upgrade.type;
		if(t==Upgrade.DAMAGE) {
			DAMAGE+=1;
		} else if(t==Upgrade.HEALTH) {
			HEALTH+=1;
		} else if(t==Upgrade.RANGE) {
			RANGE+=1;
		} else if(t==Upgrade.SHOOTINGSPEED) {
			//needs rebalancing
			SHOOTINGSPEED-=1;
			if(SHOOTINGSPEED<=0) {
				SHOOTINGSPEED = 1;
			}
		} else if(t==Upgrade.SPEED) {
			SPEED+=1;
		} else if(t==Upgrade.TIMETOSPAWN) {
			//needs rebalancing
			TIMETOSPAWN-=1;
			if(TIMETOSPAWN<=0) {
				TIMETOSPAWN = 1;
			}
		}
	}
	public void resetTimer() {
		timetospawn = 0;
	}
	public void tic() {
		timetospawn++;
	}
	public boolean ready() {
		if(timetospawn>TIMETOSPAWN) {
			return true;
		}
		return false;
	}
	public Ship getShip() {
		return new Ship(this.getPlayer(), 0, 0, WIDTH, WIDTH, SPEED, SHOOTINGSPEED, RANGE, DAMAGE, HEALTH);
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
