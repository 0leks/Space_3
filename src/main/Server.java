package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.ServerData;
import data.Upgrade;

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
	World world;
	public int selectedworldsize;
	public int startingmoney;
	public Server() {
		selectedworldsize = 1;
		startingmoney = 10;
		thread = new Thread(this);
		creationframe = new CreationFrame();
		connections = new ArrayList<Connection>();
		servertimer = new Timer(World.GAMETIMER, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				serverdata.players = worldframe.players.getText();
//				serverdata.radius = World.getRadius(selectedworldsize);
				serverdata = getServerData();
				for(int a=0; a<connections.size(); a++) {
					Connection c = connections.get(a);
					c.send(serverdata);
				}
			}
		});
		serverdata = new ServerData();
	}
//	public void flush() {
//		for(int a=0; a<connections.size(); a++) {
//			Connection c = connections.get(a);
//			try {
//				c.out.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	public ServerData getServerData() {
		serverdata.players = worldframe.players.getText();
		serverdata.radius = World.getRadius(selectedworldsize);
		serverdata.startingmoney = startingmoney;
		return serverdata;
	}
	public void playerMoveCommand(Player p, int x, int y) {
		world.playerMoveCommand(p, x, y);
	}
	public void playerUpgradeCommand(Player player, Upgrade u) {
		world.playerUpgradeCommand(player, u);
	}
	public void sendToAll(Object o) {
		for(int a=0; a<connections.size(); a++) {
			connections.get(a).send(o);
		}
	}

	public void sendToPlayer(Player player, int[] upgrades) {
		for(int a=0; a<connections.size(); a++) {
			if(connections.get(a).player.equals(player)) {
				connections.get(a).send(upgrades);
			}
		}
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
		try {
			c.in.close();
			c.out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		world = new World(getWorldSize(), this);
		for(int a=0; a<connections.size(); a++) {
			world.addPlayer(connections.get(a).player);
		}
		world.initializeBases();
		System.out.println(world.toString());
	}
	public void startGame() {
		serverdata.gamestarted = true;
		world.startGame();
	}
	public int getWorldSize() {
		return this.selectedworldsize;
	}
	public class WorldFrame extends JFrame {
		JTextArea players;
		JPanel panel;
		JButton create;
		JButton start;
		Timer tim;
		String title;
		JRadioButton small;
		JRadioButton medium;
		JRadioButton large;
		JSlider startinggold;
		ButtonGroup size;
		public WorldFrame() {
			this.setSize(500, 500);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setIconImage(Toolkit.getDefaultToolkit().createImage("assets/spaceicon.png"));
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.out.println("Closing Server");
					System.exit(0);
				}
			});
			title = "Server at ";
			try {
				title += InetAddress.getLocalHost().getHostAddress()+":";
				serverdata.ip = InetAddress.getLocalHost().getHostAddress()+":"+portNumber;
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
					g.drawString("Starting $: "+startingmoney, 55, 300);
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
			
			startinggold = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
			startinggold.setSize(275, 50);
			startinggold.setLocation(175, y+15);
//			startinggold.setMajorTickSpacing(20);
			startinggold.setMinorTickSpacing(10);
			startinggold.setPaintTicks(true);
			startinggold.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
				    startingmoney = (int)source.getValue();
				}
			});
			this.add(startinggold);
			
			
			start = new JButton("START GAME");
			start.setSize(190, 40);
			start.setLocation(50, 400);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					startGame();
					start.setVisible(false);
					worldframe.remove(start);
				}
			});
			
			create = new JButton("CREATE WORLD");
			create.setSize(190, 40);
			create.setLocation(50, 400);
			create.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					createWorld();
					create.setVisible(false);
					start.setVisible(true);
					small.setEnabled(false);
					medium.setEnabled(false);
					large.setEnabled(false);
					startinggold.setEnabled(false);
				}
			});
			this.add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
			panel.add(players);
			panel.add(create);
			panel.add(start);
			tim = new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = "Players:\n";
					for(int a=0; a<connections.size(); a++) {
						Connection c = connections.get(a);
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
				if(!serverdata.gamestarted) {
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
			this.setIconImage(Toolkit.getDefaultToolkit().createImage("assets/spaceicon.png"));
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
