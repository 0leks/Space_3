package main;

import java.util.ArrayList;

public class World {
	private ArrayList<Player> players;
	private int radius;
	public static final int SMALL = 1;
	public static final int MEDIUM = 2;
	public static final int LARGE = 3;
	public World(int size) {
		players = new ArrayList<Player>();
		setSize(size);
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
	public String toString() {
		String s = "World( radius="+radius+" players="+players.size();
		return s;
	}
}
