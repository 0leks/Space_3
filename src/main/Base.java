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
	private int money;
	public int id;
	public static int sid;
	private transient int[] upgrades;
	public Base(Player mine, int sx, int sy, int sw, int sh) {
		upgrades = new int[6];
		for(int a=0; a<upgrades.length; a++) {
			upgrades[a] = 1;
		}
		id = sid++;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		HEALTH = 10;
		DAMAGE = 1;
		SPEED = 10;
		SHOOTINGSPEED = 10;
		RANGE = 200;
		WIDTH = 20;
		TIMETOSPAWN = 15;
		timetospawn = 0;
		bounds = new Rectangle(x, y, width, height);
		player = mine;
		money = 0;
	}
	public void upgrade(Upgrade upgrade) {
		if(upgrade.getCost()<=money) {
			switch(upgrade.upgrade) {
				case TIMETOSPAWN:
					if(TIMETOSPAWN>5) {
						TIMETOSPAWN-=1;
						upgrades[0]++;
						money-=upgrade.getCost();
					}
					break;
				case HEALTH:
					HEALTH+=1;
					upgrades[1]++;
					money-=upgrade.getCost();
					break;
				case DAMAGE:
					DAMAGE+=1;
					upgrades[2]++;
					money-=upgrade.getCost();
					break;
				case SPEED:
					SPEED+=1;
					upgrades[3]++;
					money-=upgrade.getCost();
					break;
				case SHOOTINGSPEED:
					if(SHOOTINGSPEED>5) {
						SHOOTINGSPEED-=1;
						upgrades[4]++;
						money-=upgrade.getCost();
					}
					break;
				case RANGE:
					RANGE+=5;
					upgrades[5]++;
					money-=upgrade.getCost();
					break;
				default:
					break;
			}
		} else {
			System.out.println("Not enough money. Have:"+money+", need:"+upgrade.getCost());
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
		Ship s = new Ship(this.getPlayer(), 0, 0, WIDTH, WIDTH, SPEED, SHOOTINGSPEED, RANGE, DAMAGE, HEALTH);
		s.source = this;
		return s;
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
	public void addMoney(int add) {
		money += add;
	}
	public int[] getUpgrades() { return upgrades; }
	public int getMoney() { return money; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Player getPlayer() { return player; }
}
