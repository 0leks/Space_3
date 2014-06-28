package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import data.Upgrade;

public class Button {
	public Rectangle bounds;
	private Upgrade upgrade;
	public Button(Rectangle sbounds, Upgrade supgrade) {
		bounds = sbounds;
		upgrade = supgrade;
	}
	public void paint(Graphics g) {
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(Color.white);
		g.setFont(new Font("Nyala", Font.PLAIN, 20));
		g.drawString(upgrade.level+"", bounds.x, bounds.y+bounds.height);
	}
	public void paint(Graphics g, String name) {
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(World.getOposite(g.getColor()));
		g.setFont(new Font("Nyala", Font.PLAIN, 20));
		g.drawString(name+":"+upgrade.level, bounds.x, bounds.y+bounds.height/2+g.getFont().getSize()/2);
	}
	public Upgrade getUpgrade() {
		return upgrade;
	}
	public void setLevel(int s) {
		upgrade.level = s;
	}
}
