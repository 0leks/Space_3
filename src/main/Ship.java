package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import data.DetailedShipData;
import data.ShipData;

public class Ship extends Thing {
	private static final long serialVersionUID = 1L;
	private int x, y;
	private int width, height;
	private Player player;
	private int speed;
	private transient Point target;
	private transient Rectangle bounds;
	private transient ShipData data;
	private transient DetailedShipData detaileddata;
	public transient boolean sent;
	public transient int cooldown;
	private final int COOLDOWN;
	private int range;
	private int damage;
	private int health;
	private int loot;
	public transient Base source;
	public Ship(Player mine, int sx, int sy, int sw, int sh, int sspeed, int scooldown, int srange, int sdamage, int shealth, int sloot) {
		health = shealth;
		range = srange;
		COOLDOWN = scooldown;
		cooldown = 0;
		loot = sloot;
		x = sx;
		y = sy;
		width = sw;
		height = sh;
		bounds = new Rectangle(x, y, width, height);
		data = new ShipData();
		detaileddata = new DetailedShipData();
		sent = false;
		player = mine;
		speed = sspeed;
		damage = sdamage;
	}
	public Ship create() {
		return new Ship(player, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getSpeed(), this.getCooldown(), this.getRange(), this.getDamage(), this.getHealth(), this.getLoot());
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
		this.health = other.getHealth();
	}
	public void become(ShipData other) {
		this.id = other.id;
		this.x = other.x;
		this.y = other.y;
		this.health = other.health;
	}
	public String toString() {
		return "ID:"+this.id+","+player+","+x+","+y+","+width+","+height;
	}
	public void setTarget(Point newtarget) {
		target = newtarget;
	}
	@Override
	public boolean takeDamage(int damage) {
//		System.out.println("Ship "+id+" took "+dam+" damage "+"                 "+cooldown);
		health-=damage;
//		width = health;
//		height = health;
//		if(width<0) {
//			width = 1;
//		}
//		if(height<0) {
//			height = 1;
//		}
		if(health<=0) {
			return true;
		}
		return false;
	}
	public boolean canShoot(Thing en) {
		if(en.getDistanceFrom(this)<this.getRange()) {
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
	public DetailedShipData getDetailedData() {
		detaileddata.COOLDOWN = COOLDOWN;
		detaileddata.damage = damage;
		detaileddata.health = health;
		detaileddata.height = height;
		detaileddata.loot = loot;
		detaileddata.id = id;
		detaileddata.player = player;
		detaileddata.range = range;
		detaileddata.speed = speed;
		detaileddata.width = width;
		detaileddata.x = x;
		detaileddata.y = y;
		return detaileddata;
	}
	public ShipData getData() {
		data.x = x;
		data.y = y;
		data.id = id;
		data.health = health;
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
		return new Ship(player, x, y, width, height, speed, COOLDOWN, range, damage, health, loot);
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
	@Override
	public int getDistanceFrom(Ship other) {
		int dist = Math.abs(other.getY()-this.getY())+Math.abs(other.getX()-this.getX());
		return dist;
	}
	@Override
	public int getDistanceFrom(Base other) {
		int dist = Math.abs(other.getY()-this.getY())+Math.abs(other.getX()-this.getX());
		return dist;
	}
	public boolean laserReady() {
		if(cooldown<=0) {
			return true;
		}
		return false;
	}
	@Override
	public int getLoot() { return loot; }
	public int getHealth() { return health; }
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
