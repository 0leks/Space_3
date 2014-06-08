package main;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import data.Command;
import data.Disconnect;
import data.PlayerConfirm;

public class Connection implements Runnable{
	ObjectInputStream in;
	ObjectOutputStream out;
	Thread thread;
	Player player;
	Server server;
	int lifepoints;
	public Connection(Server w, ObjectInputStream i, ObjectOutputStream o) {
		server = w;
		lifepoints = 10;
		in = i;
		out = o;
		player  = new Player();
		thread = new Thread(this);
	}
	public void start() {
		thread.start();
	}
	public void send(Object o) {
		try {
			out.writeUnshared(o);//writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
			error();
		}
	}
	@Override
	public void run() {
		while(true) {
			try {
				Object ob = in.readUnshared();
				System.out.println("Read:"+ob);
				
				if(ob instanceof Ship) {
					server.world.addShip(((Ship)ob).create());
				}
				if(ob instanceof Disconnect) {
					System.out.println("Player "+player+" disconnected");
					server.detach(this);
					return;
				}
				if(ob instanceof Player) {
					Player com = (Player)ob;
					//new connection
					PlayerConfirm good = new PlayerConfirm();
					if(server.isNameGood(com.name, player.name)) {
						player.name = com.name;
					} else {
						good.msg+="Name "+com.name+" is taken. ";
					}
					if(server.isColorGood(com.color, player.color)) {
						player.color = com.color;
					} else {
						good.msg+="Color ("+com.color.getRed()+","+com.color.getGreen()+","+com.color.getBlue()+") is taken. ";
					}
					good.msg+="ID on Server:"+this.player.toString();
					System.out.println("messagetosend:"+good.msg);
					send(good);
				}
				if(ob instanceof Command) {
					Command com = (Command)ob;
					if(com.type==Command.MOVE) {
						System.out.println("Received Command:"+com);
						server.playerMoveCommand(this.player, com.x, com.y);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				error();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				error();
				break;
			}
		}
	}
	public void error() {
		System.out.println(lifepoints+"tries before detach");
		if(lifepoints--<=0) {
			detach();
		}
		
	}
	public void detach() {
		System.out.println("Detaching player "+player);
		try {
			this.out.close();
			this.in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.detach(this);
	}
}
