package main;

import java.awt.Rectangle;
import java.io.Serializable;

import data.Upgrade;

public class Base extends Thing {
	private static final long serialVersionUID = 1L;
	private int x, y;
	private int width, height;
	private Rectangle bounds;
	private Player player;
	private int timetospawn;
	private int health;
	private boolean dead;
	
	private int TIMETOSPAWN;
	private int HEALTH;
	private int DAMAGE;
	private int SPEED;
	private int SHOOTINGSPEED;
	private int RANGE;
	private int WIDTH;
	private int money;
	private transient int[] upgrades;
	public Base(Player mine, int sx, int sy, int sw, int sh) {
		upgrades = new int[6];
		for(int a=0; a<upgrades.length; a++) {
			upgrades[a] = 1;
		}
		dead = false;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		health = 10000;
		HEALTH = 10;
		DAMAGE = 1;
		SPEED = 10;
		SHOOTINGSPEED = 20;
		RANGE = 200;
		WIDTH = 20;
		TIMETOSPAWN = 30;
		timetospawn = 0;
		bounds = new Rectangle(x, y, width, height);
		player = mine;
		money = 0;
	}
	@Override
	public boolean takeDamage(int damage) {
		health-=damage;
		if(health<=0) {
			dead = true;
			health = 0;
			return true;
		}
		return false;
	}
	public void upgrade(Upgrade upgrade) {
		if(upgrade.getCost()<=money) {
			switch(upgrade.upgrade) {
				case TIMETOSPAWN:
					if(TIMETOSPAWN>15) {
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
					if(SHOOTINGSPEED>10) {
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
	@Override
	public int getDistanceFrom(Ship other) {
		return other.getDistanceFrom(this);
	}
	@Override
	public int getDistanceFrom(Base other) {
		int dist = Math.abs(other.getY()-this.getY())+Math.abs(other.getX()-this.getX());
		return dist;
	}
	public void resetTimer() {
		timetospawn = 0;
	}
	public void tic() {
		timetospawn++;
	}
	public boolean ready() {
		if(timetospawn>TIMETOSPAWN && !dead) {
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
		this.bounds = other.bounds;
		this.DAMAGE = other.DAMAGE;
		this.dead = other.dead;
		this.health = other.health;
		this.HEALTH = other.HEALTH;
		this.height = other.height;
		this.id = other.id;
		this.money = other.money;
		this.player = other.player;
		this.RANGE = other.RANGE;
		this.SHOOTINGSPEED = other.SHOOTINGSPEED;
		this.SPEED = other.SPEED;
		this.timetospawn = other.timetospawn;
		this.TIMETOSPAWN = other.TIMETOSPAWN;
		this.upgrades = other.upgrades;
		this.width = other.width;
		this.WIDTH = other.WIDTH;
		this.x = other.x;
		this.y = other.y;
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
	@Override
	public int getLoot() {
		int ret = money;
		money = 0;
		return 20+ret;
	}
	public boolean getDead() { return dead; }
	public int getCurrentHealth() { return health; }
	public int getSpawnTime() { return TIMETOSPAWN; }
	public int[] getUpgrades() { return upgrades; }
	public int getMoney() { return money; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Player getPlayer() { return player; }
}
