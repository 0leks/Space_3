package main;

import java.util.ArrayList;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import data.GameData;
import data.RemoveLaser;
import data.ShipData;

public class World{
	private ArrayList<Player> players;
	public ArrayList<Ship> ships;
	public ArrayList<Ship> sortedships;
	private ArrayList<Base> bases;
	private ArrayList<Laser> lasers;
	private int radius;
	private transient GameData gamedata;
	private transient Server server;
	private transient Timer gametimer;
	public static int GAMETIMER = 50;
	public static final int SMALL = 1;
	public static final int MEDIUM = 2;
	public static final int LARGE = 3;
	public World(int size, Server s) {
		server = s;
		players = new ArrayList<Player>();
		bases = new ArrayList<Base>();
		ships = new ArrayList<Ship>();
		sortedships = new ArrayList<Ship>();
		lasers = new ArrayList<Laser>();
		setSize(size);
		gamedata = new GameData();
		gametimer= new Timer(World.GAMETIMER, new ActionListener() {
			@Override
//			public void move() {
//			if(target!=null) {
//				double dx = (double)(target.x-gs.moveY();etX())*getSpeed()/100.0;
//				double dy = (double)(target.y-getY())*getSpeed()/100.0;
//				if(dx>1)
//					dx = 1;
//				if(dx<-1)
//					dx = -1;
//				if(dy>1) 
//					dy=1;
//				if(dy<-1)
//					dy=-1;
//				System.out.println("Ship "+this+" is moving to ("+(x+speed*dx)+","+(y+speed*dy)+")");
//				x+=speed*dx;
//				y+=speed*dy;
//				if(Math.abs(target.x-x)<speed && Math.abs(target.y-y)<speed) {
//					target = null;
//				}
//			}
//		}
			public void actionPerformed(ActionEvent e) {
				for(int a=0; a<ships.size(); a++) {
					Ship s = ships.get(a);
					if(s==null) {
						ships.remove(a--);
						continue;
					}
					if(s.hasTarget()) {
						Rectangle move = s.getMoveX();
						if(!collides(s, move)) {
							s.setPos(move);
						}
						move = s.getMoveY();
						if(!collides(s, move)) {
							s.setPos(move);
						}
					}
					s.servertic();
					if(s.laserReady()) {
						Ship en = getClosestEnemy(s);
						if(en!=null && s.canShoot(en)) {
							s.shot();
							Laser l = new Laser(s.getID(), en.getID(), s.getCooldown()-4, s.getDamage());
							System.out.println("Ship "+s.getID()+" Shooting at "+en.getID()+" cd:"+en.getCooldown()+"                 "+s.cooldown);
							lasers.add(l);
							server.sendToAll(l);
						}
					}
//					ships.get(a).move();
				}
				for(int a=0; a<lasers.size(); a++) {
					Laser l = lasers.get(a);
					if(l.widen()) {
						Ship en = getShip(l.to);
						if(en!=null) {
							if(en.takeDamage(l.damage)) {
								removeShip(en);
							}
						}
						lasers.remove(a--);
					}
				}
//				gamedata = new GameData();
//				gamedata.bases = bases;
//				// TODO needs to be changed to not create a new ArrayList every time data is sent
//				gamedata.ships = new ArrayList(ships);
				sendGameData();
			}
		});
	}
	public void removeShip(Ship s) {
		System.out.println("Removing Ship "+s.getID());
		ships.remove(s);
		sortedships.remove(s);
		ShipData sd = s.getData();
		sd.dead = true;
		server.sendToAll(sd);
		for(int a=0; a<ships.size(); a++) {
			System.out.println(ships.get(a));
		}
		for(int a=lasers.size()-1; a>=0; a--) {
			Laser l = lasers.get(a);
			if(l.from==s.getID()) {
				lasers.remove(a);
				System.out.println("Removing Laser");
				RemoveLaser rl = new RemoveLaser(l);
				server.sendToAll(rl);
			}
		}
	}
	public Ship getShip(int id) {
		for(int a=0; a<ships.size(); a++) {
			if(ships.get(a).getID()==id) {
				return ships.get(a);
			}
		}
		return null;
	}
	public Ship getRandomCloseEnemy(Ship s) {
		Ship en = null;
		int dist = 99999;
		for(int a = 0; a<sortedships.size(); a++) {
			Ship b = sortedships.get(a);
			if(!b.getPlayer().equals(s.getPlayer())) {
				int d = b.getDistanceFrom(s);
				if(d<dist) {
					if(dist==99999 || Math.random()<.5) {
						dist = d;
						en = b;
					}
				}
			}
		}
		return en;
	}
	public Ship getClosestEnemy(Ship s) {
		Ship en = null;
		int dist = 99999;
		for(int a = 0; a<sortedships.size(); a++) {
			Ship b = sortedships.get(a);
			if(!b.getPlayer().equals(s.getPlayer())) {
				int d = b.getDistanceFrom(s);
				if(d<dist) {
					dist = d;
					en = b;
				}
			}
		}
		return en;
	}
	public boolean collides(Ship s, Rectangle newpos) {
		for(int a=0; a<ships.size(); a++) {
			Ship sh = ships.get(a);
			if(sh==null)
				System.out.println("SH is null!!");
			if(sh!=s && sh.collides(newpos)) {
				return true;
			}
		}
		return false;
	}
	public void playerMoveCommand(Player p, int x, int y) {
		for(int a=0; a<sortedships.size(); a++) {
			Ship s = sortedships.get(a);
			if(s.getPlayer().equals(p)) {
				System.out.println("Setting move target of Ship "+s+" to ("+x+","+y+")");
				s.setTarget(new Point(x, y));
			}
		}
	}
	public void addShip(Ship s) {
		System.out.println("Adding Ship:"+s.toString());
		ships.add(s);
		sortedships.add(s);
	}
	public void sendGameData() {
		for(int a=0; a<bases.size(); a++) {
			server.sendToAll(bases.get(a));
		}
		if(ships.size()>0) {
			for(int a=0; a<ships.size() && a<100 ; a++) {
				Ship s = ships.remove(0);
				if(s.sent) {
					server.sendToAll(s.getData());
				} else {
					server.sendToAll(s);
				}
				ships.add(s);
			}
		}
//		for(int a=0; a<lasers.size(); a++) {
//			
//		}
//		for(int a=0; a<ships.size(); a++) {
//			server.sendToAll(ships.get(a));
//		}
//		gamedata.bases = null;
//		gamedata.ships = null;
	}
	public void startGame() {
		gametimer.start();
	}
	public void initializeBases() {
		int numbases = players.size();
		double radperbase = 2*Math.PI/numbases;
		int playerindex = 0;
		for(double rad = 0; rad<2*Math.PI && playerindex<numbases; rad = rad+radperbase, playerindex++) {
			int[] center = polartorect(radius-40, rad);
			Base b = new Base(players.get(playerindex), center[0], center[1], 40, 40);
			bases.add(b);
		}
	}
	public void addPlayer(Player p) {
		players.add(p);
	}
	public void setSize(int size) {
		radius = getRadius(size);
	}
	public static int getRadius(int size) {
		if(size==SMALL) {
			return 500;
		} else if(size == MEDIUM) {
			return 1000;
		} else if(size == LARGE) {
			return 1500;
		}
		return 0;
	}
	public String mytoString() {
		String s = "World( radius="+radius+" players="+players.size()+"\n";
		for(int a=0; a<bases.size(); a++) {
			s+="Base("+bases.get(a).toString()+")\n";
		}
		for(int a=0; a<ships.size(); a++) {
			s+="Ship("+ships.get(a).toString()+")\n";
		}
		return s;
	}
	public static int[] polartorect(double radius, double angle) {
		int[] toret = new int[2];
		toret[0] = (int) (Math.cos(angle)*radius);
		toret[1] = (int) (Math.sin(angle)*radius);
		return toret;
	}
	public static double[] recttopolar(double x, double y) {
		double[] toret = new double[2];
		toret[0] = Math.sqrt(x*x+y*y);
		toret[1] = Math.atan2(y, x);
		return toret;
	}
}
