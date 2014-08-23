package data;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import main.Player;

public class DetailedShipData implements Serializable{
	public int x, y, id;
	public int width, height;
	public Player player;
	public int speed;
	public int COOLDOWN;
	public int range;
	public int damage;
	public int health;
	public int loot;
}
