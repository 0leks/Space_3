package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import data.ServerData;

public class Server implements Runnable {
	public ServerSocket server;
	Socket sock;
	Thread thread;
	CreationFrame creationframe;
	WorldFrame worldframe;
	int portNumber = 34567;
	ArrayList<Connection> connections;
	Timer servertimer;
	ServerData serverdata;
	int currentworldWidth;
	World world;
	public int selectedworldsize;
	public Server() {
		selectedworldsize = 1;
		thread = new Thread(this);
		creationframe = new CreationFrame();
		connections = new ArrayList<Connection>();
		servertimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serverdata.players = worldframe.players.getText();
				serverdata.width = currentworldWidth;
				for(Connection c : connections) {
					c.send(serverdata);
				}
			}
		});
		serverdata = new ServerData();
	}
	public static void main(String[] args) {
		Server s = new Server();
	}
	public boolean isNameGood(String name, String used) {
		for(int a=0; a<connections.size(); a++) {
			if(connections.get(a).player!=null) {
				if(!connections.get(a).player.name.equals(used)) {
					if(name.equals(connections.get(a).player.name)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	public boolean isColorGood(Color color, Color used) {
		for(int a=0; a<connections.size(); a++) {
			Color cur = connections.get(a).player.color;
			if(!cur.equals(used)) {
				int dif = Math.abs(cur.getRed()-color.getRed());
				dif += Math.abs(cur.getGreen()-color.getGreen());
				dif += Math.abs(cur.getBlue()-color.getBlue());
				if(dif<50) {
					return false;
				}
			}
		}
		return true;
	}
	public void start() {
		thread.start();
	}
	public void detach(Connection c) {
		connections.remove(c);
		System.out.println(c.player+" was detached");
	}
	@Override
	public void run() {
		worldframe = new WorldFrame();
		try {
			System.out.println("Creating Server on port:"+portNumber);
			server = new ServerSocket(portNumber);
			servertimer.start();
			while(true) {
				System.out.println("waiting for connection");
				sock = server.accept();
				System.out.println("Creating outputstream");
				ObjectOutputStream hostout = new ObjectOutputStream(sock.getOutputStream());
				hostout.flush();
				System.out.println("making reader");
				ObjectInputStream hostin = new ObjectInputStream(sock.getInputStream());
				Connection con = new Connection(this, hostin, hostout);
				connections.add(con);
				con.start();
			}
		} catch (IOException e) {
			System.out.println("Error Creating Server");
			e.printStackTrace();
		}
	}
	public void createWorld() {
		world = new World(getWorldSize());
		for(int a=0; a<connections.size(); a++) {
			world.addPlayer(connections.get(a).player);
		}
		System.out.println(world.toString());
	}
	public int getWorldSize() {
		return this.selectedworldsize;
	}
	public class WorldFrame extends JFrame {
		JTextArea players;
		JPanel panel;
		JButton create;
		Timer tim;
		String title;
		JRadioButton small;
		JRadioButton medium;
		JRadioButton large;
		ButtonGroup size;
		public WorldFrame() {
			this.setSize(500, 500);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			title = "Server at ";
			try {
				title += InetAddress.getLocalHost().getHostAddress()+":";
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			title += portNumber;
			this.setTitle(title);
			players = new JTextArea("Players:\ntest\ntest");
			players.setFocusable(false);
			players.setFont(new Font("Nyala", Font.PLAIN, 20));
			players.setSize(400, 200);
			players.setLocation(50, 50);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.setFont(new Font("Arial", Font.PLAIN, 25));
					g.drawString(title, 20, 35);
					g.setFont(new Font("Arial", Font.PLAIN, 18));
					g.drawString("World Size:", 55, 273);
				}
			};
			int x = 150;
			int y = 257;
			SizeListener sl = new SizeListener();
			small = new JRadioButton("Small");
			small.setSelected(true);
			small.setSize(70, 20);
			small.setLocation(x, y);
			small.addActionListener(sl);
			medium = new JRadioButton("Medium");
			medium.setSelected(true);
			medium.setSize(70, 20);
			medium.setLocation(x+70, y);
			medium.addActionListener(sl);
			large = new JRadioButton("Large");
			large.setSelected(true);
			large.setSize(70, 20);
			large.setLocation(x+140, y);
			large.addActionListener(sl);
			size = new ButtonGroup();
			size.add(small);
			size.add(medium);
			size.add(large);
			this.add(small);
			this.add(medium);
			this.add(large);
			
			create = new JButton("START GAME");
			create.setSize(190, 40);
			create.setLocation(50, 400);
			create.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					createWorld();
				}
			});
			this.add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
			panel.add(players);
//			panel.add(width);
			panel.add(create);
			tim = new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = "Players:\n";
					for(Connection c : connections) {
						if(c.player!=null) {
							text+=c.player+"\n";
						} else {
							text+="null\n";
						}
					}
					players.setText(text);
					repaint();
				}
				
			});
			tim.start();
			this.setVisible(true);
		}
		public class SizeListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==small) {
					selectedworldsize = World.SMALL;
				} else if(e.getSource()==medium) {
					selectedworldsize = World.MEDIUM;
				} else if(e.getSource()==large) {
					selectedworldsize = World.LARGE;
				}
			}
		}
	}
	public class CreationFrame extends JFrame {
		public JPanel panel;
		JTextField port;
		String text = "";
		public CreationFrame() {
			port = new JTextField("34567");
			port.setSize(200, 40);
			port.setLocation(50, 50);
			port.setToolTipText("port");
			JButton start = new JButton("CREATE");
			start.setSize(100, 40);
			start.setLocation(50, 250);
			start.setFocusable(false);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean good = true;
					int num = 0;
					try {
						num = Integer.parseInt(port.getText());
					} catch (Exception ex) {
						good = false;
						text = " -- example: 34567";
						repaint();
					}
					if(good) {
						portNumber = num;
						setVisible(false);
						start();
					}
				}
			});
			this.setSize(500, 500);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.drawString("port"+text, port.getX(), port.getY()-5);
				}
			};
			panel.setLayout(null);
			panel.add(port);
			panel.add(start);
			panel.requestFocus();
			this.add(panel, BorderLayout.CENTER);
			this.setTitle("Server");
			this.setVisible(true);
		}
	}
}
