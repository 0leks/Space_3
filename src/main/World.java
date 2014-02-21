package main;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import data.GameData;

public class World{
	private ArrayList<Player> players;
	public ArrayList<Ship> ships;
	private ArrayList<Base> bases;
	private int radius;
	private transient GameData gamedata;
	private transient Server server;
	private transient Timer gametimer;
	public static final int SMALL = 1;
	public static final int MEDIUM = 2;
	public static final int LARGE = 3;
	public World(int size, Server s) {
		server = s;
		players = new ArrayList<Player>();
		bases = new ArrayList<Base>();
		ships = new ArrayList<Ship>();
		setSize(size);
		gamedata = new GameData();
		gametimer= new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamedata.bases = bases;
				gamedata.ships = new ArrayList(ships);
				sendGameData();
			}
		});
	}
	public void addShip(Ship s) {
		System.out.println("Adding Ship:"+s.toString());
		ships.add(s);
	}
	public void sendGameData() {
		server.sendToAll(gamedata);
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
