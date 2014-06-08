package main;

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
	}
}
