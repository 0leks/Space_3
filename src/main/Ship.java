package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import data.ShipData;

public class Ship implements Serializable {
	private static final long serialVersionUID = 1L;
	private int x, y;
	private int width, height;
	private Player player;
	private int speed;
	private Point target;
	private int id;
	public static int sid;
	private Rectangle bounds;
	private ShipData data;
	public boolean sent;
	public int cooldown;
	private final int COOLDOWN;
	private int range;
	private int damage;
	private int health;
	public Ship(Player mine, int sx, int sy, int sw, int sh, int sspeed, int scooldown, int srange, int sdamage, int shealth) {
		health = shealth;
		range = srange;
		COOLDOWN = scooldown;
		cooldown = 0;
		id = sid++;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		bounds = new Rectangle(x, y, width, height);
		data = new ShipData();
		sent = false;
		player = mine;
		speed = sspeed;
		damage = sdamage;
	}
	public Ship create() {
		return new Ship(player, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getSpeed(), this.getCooldown(), this.getRange(), this.getDamage(), this.getHealth());
	}
	public void become(Ship other) {
		this.id = other.id;
		this.x = other.getX();
		this.y = other.getY();
		this.width = other.getWidth();
		this.height = other.getHeight();
		this.bounds = other.getBounds();
		this.player = other.getPlayer();
		this.speed = other.getSpeed();
	}
	public void become(ShipData other) {
		this.id = other.id;
		this.x = other.x;
		this.y = other.y;
	}
	public String toString() {
		return "ID:"+this.id+","+player+","+x+","+y+","+width+","+height;
	}
	public void setTarget(Point newtarget) {
		target = newtarget;
	}
	public boolean takeDamage(int dam) {
		System.out.println("Ship "+id+" took "+dam+" damage "+"                 "+cooldown);
		health-=dam;
		width = health;
		height = health;
		if(width<0) {
			width = 1;
		}
		if(height<0) {
			height = 1;
		}
		if(health<0) {
			return true;
		}
		return false;
	}
	public boolean canShoot(Ship other) {
		if(this.getDistanceFrom(other)<this.getRange()) {
			return true;
		}
		return false;
	}
	public void shot() {
		cooldown = COOLDOWN;
	}
	public void servertic() {
		cooldown--;
//		move();
	}
	public ShipData getData() {
		data.x = x;
		data.y = y;
		data.id = id;
		return data;
	}
	public Rectangle getBounds() {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		return bounds;
	}
	public Ship duplicate() {
		return new Ship(player, x, y, width, height, speed, COOLDOWN, range, damage, health);
	}
	public boolean hasTarget() {
		return target!=null;
	}
	public Rectangle getMoveX() {
		int ddx = target.x-getX();
		if(ddx==0) {
			return new Rectangle(x, y, width, height);
		} else if(Math.abs(ddx)<getSpeed()) {
			return new Rectangle(x+ddx, y, width, height);
		} else {
			int dx = Math.abs(ddx)/(ddx)*getSpeed();
			return new Rectangle(x+dx, y, width, height);
		}
	}
	public Rectangle getMoveY() {
		int ddy = target.y-getY();
		if(ddy==0) {
			return new Rectangle(x, y, width, height);
		} else if(Math.abs(ddy)<getSpeed()) {
			return new Rectangle(x, y+ddy, width, height);
		} else {
			int dy = Math.abs(ddy)/(ddy)*getSpeed();
			return new Rectangle(x, y+dy, width, height);
		}
	}
	public void setPos(Rectangle r) {
		this.x = r.x;
		this.y = r.y;
	}
	public boolean collides(Rectangle other) {
		return this.getBounds().intersects(other);
	}
	public boolean collides(Ship other) {
		return this.getBounds().intersects(other.getBounds());
	}
	public int getDistanceFrom(Ship other) {
		int dist = Math.abs(other.getY()-this.getY()+other.getX()-this.getX());
		return dist;
	}
	public boolean laserReady() {
		if(cooldown<=0) {
			return true;
		}
		return false;
	}
	public int getHealth() { return health; }
	public int getID() { return id; }
	public int getDamage() { return damage; }
	public int getRange() { return range; }
	public int getCooldown() { return COOLDOWN; }
	public int getSpeed() { return speed; }
	private int getX() { return x; }
	private int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Player getPlayer() { return player; }
}
