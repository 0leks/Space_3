package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import data.Upgrade;

public class UpgradeButton extends Button{
	private Upgrade upgrade;
	public boolean expanded;
	public int originalheight;
	public Color color;
	public String name;
	public UpgradeButton(Rectangle sbounds, Upgrade supgrade, Color color, String name) {
		super(sbounds);
		this.color = color;
		this.name = name;
		upgrade = supgrade;
		originalheight = sbounds.height;
		mouseOver(true);
	}
	public void mouseOver(boolean over) {
		expanded = over;
		if(expanded) {
			bounds.height = 80;
		} else {
			bounds.height = originalheight;
		}
	}
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(World.getOposite(g.getColor()));
		g.setFont(new Font("Nyala", Font.PLAIN, 20));
		g.drawString(name+": "+upgrade.level, bounds.x, bounds.y+bounds.height/2+g.getFont().getSize()/2-2);
	}
	public void paintExpanded(Graphics g, String delta) {
		g.setColor(color);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(World.getOposite(color));
		g.setFont(new Font("Nyala", Font.BOLD, 20));
		int y = bounds.y+g.getFont().getSize()/2-2;
		int dy = 20;
		g.drawString(name+":"+upgrade.level, bounds.x, y+=dy);
		g.setFont(new Font("Nyala", Font.PLAIN, 20));
		g.drawString("Cost: "+upgrade.getCost(), bounds.x, y+=dy);
		g.drawString(delta, bounds.x, y+=dy);
	}
	public void click() {
		
	}
	public Upgrade getUpgrade() {
		return upgrade;
	}
	public void setLevel(int s) {
		upgrade.level = s;
	}
}
