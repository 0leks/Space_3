package data;

import java.io.Serializable;

import main.Laser;

public class RemoveLaser implements Serializable{
	public Laser l;
	public RemoveLaser(Laser lr) {
		l = lr;
	}
}
