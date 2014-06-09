package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import data.*;

public class Client implements Runnable{
	Socket socket;
	ObjectInputStream hostin;
	ObjectOutputStream hostout;
	Thread mythread;
	String ip;
	int port;
	Player thisplayer;
	String message;
	String message2;
	boolean nameconfirmed;
	boolean colorconfirmed;
	boolean SERVERERROR;
	ServerData currentserverdata;
	ConnectFrame connectframe;
	GameFrame gameframe;
	public boolean startclient;
	private Button press;
	private ArrayList<Button> buttons;
	private ArrayList<Base> bases;
	private ArrayList<Ship> ships;
	private ArrayList<Laser> lasers;
	private ArrayList<Explosion> explosions;
	Point lookingat;
	Point mouse;
	boolean zoomtobase;
	public static final int CAMERASPEED = 20;
	public int FREQ = 21;
	private int cameradx, camerady;
	public ArrayList<Rectangle> target;
	public Timer tim;
	private int money;
	public Client() {
		buttons = new ArrayList<Button>();
		target = new ArrayList<Rectangle>();
		bases = new ArrayList<Base>();
		ships = new ArrayList<Ship>();
		lasers = new ArrayList<Laser>();
		explosions = new ArrayList<Explosion>();
		thisplayer = new Player();
		message = "asdf";
		message2 = "";
	    this.mythread = new Thread(this);
	    connectframe = new ConnectFrame();
	    gameframe = new GameFrame();
	    lookingat = new Point(0,0);
	    mouse = new Point(0,0);
	    zoomtobase = true;
	    initializeButtons();
	}
	public void initializeButtons() {
		buttons.clear();
		int startx = 10;
		int dx = 0;
		int starty = 40;
		int dy = 50;
		int width = 100;
		int height = 40;
		starty-=dy;
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.DAMAGE)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.blue);
				super.paint(g);
			}
		});
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.HEALTH)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.red);
				super.paint(g);
			}
		});
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.RANGE)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.GRAY);
				super.paint(g);
			}
		});
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.SHOOTINGSPEED)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.YELLOW);
				super.paint(g);
			}
		});
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.SPEED)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.ORANGE);
				super.paint(g);
			}
		});
		buttons.add(new Button(new Rectangle(startx+=dx, starty+=dy, width, height), new Upgrade(Upgrade.TIMETOSPAWN)) {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.CYAN);
				super.paint(g);
			}
		});
	}
	public void disconnect() {
		System.out.println("Disconnecting");
		send(new Disconnect());
		try {
			hostin.close();
			hostout.close();
			changeFrame();
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Client c = new Client();
		c.mythread.start();
	}
	public Ship getShip(int id) {
		for(int a=0; a<ships.size(); a++) {
			if(ships.get(a).getID()==id) {
				return ships.get(a);
			}
		}
		return null;
	}
	public String trimString(String s) {
		char[] chars = s.toCharArray();
		String toret = "";
		for(int a=0; a<chars.length; a++) {
			if(chars[a]>=48 && chars[a]<=57) {
				toret += ""+chars[a];
			}
		}
		return toret;
	}
	@Override
	public void run() {
		while(true) {
			if(startclient) {
				startclient = false;
				InetAddress hostIP = null;
				try {
					connectframe.addText("Converting ip adress:"+ip+"\n");
					hostIP = InetAddress.getByName(ip);
					connectframe.addText("  -Done converting, result is:"+hostIP+"\n");
					if (hostIP == null) {
						connectframe.addText("ip invalid, returning\n");
						return;
					}
					connectframe.addText("Creating socket on "+ip+":"+port+"\n");
					socket = new Socket(hostIP, port);
					connectframe.addText("Creating ObjectOutputStream\n");
					hostout = new ObjectOutputStream(socket.getOutputStream());
					connectframe.addText("Flushing ObjectOutputStream\n");
					hostout.flush();
					connectframe.addText("Creating ObjectInputStream\n");
					hostin = new ObjectInputStream(socket.getInputStream());
					System.out.println("Sending :"+thisplayer);
					send(thisplayer);
					connectframe.start.setText("Update");
					connectframe.setTitle(connectframe.getTitle()+" connected to ("+ip+":"+port+")");
					read();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					connectframe.addText(e.getMessage()+" ("+ip+":"+port+")");
				} catch (IOException e) {
					e.printStackTrace();
					connectframe.addText(e.getMessage()+" ("+ip+":"+port+")");
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(Object o) {
		try {
			hostout.writeUnshared(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void changeFrame() {
		connectframe.setVisible(false);
		gameframe.setVisible(true);
		gameframe.gametimer.start();
	}
	public void read() {
		while(true) {
			try {
				Object read = hostin.readUnshared();
				if(read instanceof PlayerConfirm) {
					String str = ((PlayerConfirm)read).msg;
					message2 = str;
				}
				if(read instanceof ServerData) {
					if(currentserverdata!=null) {
						ServerData serverdata = (ServerData)read;
						if(currentserverdata.gamestarted) {
							
						} else {
							if(serverdata.gamestarted) {
								changeFrame();
							}
						}
					}
					currentserverdata = (ServerData)read;
					connectframe.serverplayers.setText(currentserverdata.players);
					connectframe.serverdata.setText(currentserverdata.getServerData());
				}
				if(read instanceof RemoveLaser) {
//					RemoveLaser rl = (RemoveLaser)read;
//					for(int a=lasers.size()-1; a>=0; a--) {
//						if(lasers.get(a).from==rl.l.from) {
//							lasers.remove(a);
//							System.out.println("Removing Laser! because ship died");
//						}
//					}
					// comented this out temporarily to avoid null pointer exceptions.
				}
				if(read instanceof Base) {
					Base s = (Base)read;
					boolean added = false;
					for(int a=0; a<bases.size(); a++) {
						if(bases.get(a).id==s.id) {
							bases.get(a).become(s);
							if(s.getPlayer().equals(thisplayer)) {
								money = s.getMoney();
							}
//							bases.remove(a);
//							bases.add(s);
							added = true;
							break;
						}
					}
					if(!added) {
						bases.add(s);
					}
				}
				if(read instanceof Laser) {
					Laser l = (Laser)read;
					lasers.add(l);
				}
				if(read instanceof ShipData) {
					ShipData s = (ShipData)read;
					for(int a=0; a<ships.size(); a++) {
						Ship ship = ships.get(a);
						if(ship.getID()==s.id) {
							if(s.dead) {
								ships.remove(a);
								System.out.println("Ship "+s.id+" was removed.");
								Explosion ex = new Explosion(ship.getBounds().x, ship.getBounds().y, ship.getWidth()*3/2);
								explosions.add(ex);
								break;
							} else {
								ships.get(a).become(s);
								break;
							}
						}
					}
				}
				if(read instanceof Ship) {
					Ship s = (Ship)read;
					boolean added = false;
					for(int a=0; a<ships.size(); a++) {
						if(ships.get(a).getID()==s.getID()) {
							ships.get(a).become(s);
//							ships.remove(a);
//							ships.add(s);
							added = true;
							break;
						}
					}
					if(!added) {
						ships.add(s);
					}
				}
			} catch (ClassNotFoundException e) {
				connectframe.addText(e.getMessage()+" ("+ip+":"+port+")\n");
				e.printStackTrace();
			} catch (IOException e) {
				connectframe.addText(e.getMessage()+" ("+ip+":"+port+")\n");
				SERVERERROR = true;
//				e.printStackTrace();
			}
		}
	}
	public class GameFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		public JPanel draw;
		public Timer gametimer;
		public GameFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					disconnect();
					System.exit(0);
				}
			});
			this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			this.setUndecorated(true);
			draw = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override
				public void paintComponent(Graphics g) {
					// TODO Paint Component Method
					super.paintComponent(g);
					g.setColor(Color.black);
					g.fillRect(0, 0, getWidth(), getHeight());
					if(SERVERERROR) {
						g.setFont(new Font("Courier", Font.BOLD, 120));
						int half = getHeight()/2;
						int delta = 5;
						int total = 51;
						int net = delta*total;
						boolean right = true;
						int tic = 0 ;
						int x = 350;
						for(int a=0; a<=total; a+=1) {
							int m = delta*a;
							g.setColor(new Color(m, m, m));
							if(right) {
								x+=delta;
							} else {
								x-=delta;
							}
							if(tic++>7) {
								if(Math.random()<.95) {
									right = !right;
									tic = 0;
								}
							}
							g.drawString("SERVER ERROR", x, half-net+m);
						}
						g.setColor(thisplayer.color);
						g.drawString("SERVER ERROR", x, half);
					}
					for(int a=0; a<bases.size(); a++) {
						Base b = bases.get(a);
						g.setColor(b.getPlayer().color);
						Rectangle r = b.getBounds();
						g.fillRect(r.x-lookingat.x, r.y-lookingat.y, r.width, r.height);
//						g.fillRect(b.getX()-lookingat.x, b.getY()-lookingat.y, b.getWidth(), b.getHeight());
//						g.fillRect(b.getX()-lookingat.x-b.getWidth()/2, b.getY()-lookingat.y-b.getHeight()/2, b.getWidth(), b.getHeight());
					}
					for(int a=0; a<explosions.size(); a++) {
						Explosion e = explosions.get(a);
						g.setColor(Color.orange);
//						g.fillOval(e.x-lookingat.x, e.y-lookingat.y, e.radius, e.radius);
						g.fillOval(e.x-e.radius/2-lookingat.x, e.y-e.radius/2-lookingat.y, e.radius, e.radius);
					}
					for(int a=0; a<ships.size(); a++) {
						Ship b = ships.get(a);
						g.setColor(b.getPlayer().color);
						Rectangle r = b.getBounds();
						g.fillRect(r.x-lookingat.x, r.y-lookingat.y, r.width, r.height);
//						g.fillRect(b.getX()-lookingat.x, b.getY()-lookingat.y, b.getWidth(), b.getHeight());
//						g.fillRect(b.getX()-lookingat.x-b.getWidth()/2, b.getY()-lookingat.y-b.getHeight()/2, b.getWidth(), b.getHeight());
					}
					
					for(int a=0; a<lasers.size(); a++) {
						Laser l = lasers.get(a);
						Ship from = getShip(l.from);
						Ship to = getShip(l.to);
						if(from==null || to==null) { 
							System.out.println("Removing Laser because either target or source is null");
							lasers.remove(a--);
						} else {
//							g.setColor(from.getPlayer().color);
//							for(int b=-l.width/2; b<l.width/2; b++) {
//								int xsh = 0;
//								int ysh = 0;
//								if(Math.random()<.5) {
//									xsh = b;
//									ysh = 0;
//								} else {
//									xsh = 0;
//									ysh = b;
//								}
							double ratio = (l.width*1.0)/l.ttl;
//							System.out.println(ratio);
							if(ratio>.9) {
								g.setColor(new Color((int)(from.getPlayer().color.getRed()*ratio), (int)(from.getPlayer().color.getGreen()*ratio), (int)(from.getPlayer().color.getBlue()*ratio)));
	//							g.setColor(from.getPlayer().color);
								g.drawLine(from.getBounds().x-lookingat.x, from.getBounds().y-lookingat.y, to.getBounds().x-lookingat.x, to.getBounds().y-lookingat.y);
							}
//							}
						}
					}
					for(int a=0; a<buttons.size(); a++) {
						Button b = buttons.get(a);
						b.paint(g);
					}
					g.setFont(new Font("Courier", Font.BOLD, 30));
					g.setColor(Color.white);
					g.drawString((lookingat.x+mouse.x)+","+(lookingat.y+mouse.y), 10, 30);
					g.setFont(new Font("Courier", Font.BOLD, 50));
					g.drawString(money+"", 10, getHeight()-10);
				}
			};
			draw.setBackground(Color.black);
			this.add(draw, BorderLayout.CENTER);
			this.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent arg0) {
					Point currentmouse = mouse.getLocation();
					mouseMoved(arg0);
					if(press!=null) {
						press.bounds.x+=mouse.x-currentmouse.x;
						press.bounds.y+=mouse.y-currentmouse.y;
					}
				}
				@Override
				public void mouseMoved(MouseEvent e) {
					mouse = e.getPoint();
					if(e.getX()<20) {
						cameradx = -1;
					} else if(e.getX()>getWidth()-20) {
						cameradx = 1;
					} else {
						cameradx = 0;
					}
					if(e.getY()<20) {
						camerady = -1;
					} else if(e.getY()>getHeight()-20) {
						camerady = 1;
					} else {
						camerady = 0;
					}
				}
			});
			this.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					int k = e.getKeyCode();
					if(k==KeyEvent.VK_UP) {
						camerady = -1;
					}
					if(k==KeyEvent.VK_DOWN) {
						camerady = 1;
					}
					if(k==KeyEvent.VK_LEFT) {
						cameradx = -1;
					}
					if(k==KeyEvent.VK_RIGHT) {
						cameradx = 1;
					}
					if(k==KeyEvent.VK_R) {
						initializeButtons();
					}
					if(k==KeyEvent.VK_ESCAPE) {
						disconnect();
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					int k = e.getKeyCode();
					if(k==KeyEvent.VK_UP) {
						camerady = 0;
					}
					if(k==KeyEvent.VK_DOWN) {
						camerady = 0;
					}
					if(k==KeyEvent.VK_LEFT) {
						cameradx = 0;
					}
					if(k==KeyEvent.VK_RIGHT) {
						cameradx = 0;
					}
				}
				@Override
				public void keyTyped(KeyEvent arg0) {
				}
			});
			this.addMouseListener(new MouseListener() {
				@Override
				public void mousePressed(MouseEvent e) {
					int x = e.getX()+lookingat.x;
					int y = e.getY()+lookingat.y;
					mouse = e.getPoint();
					if(e.getButton()==MouseEvent.BUTTON1) {
						for(int a=0; a<buttons.size(); a++) {
							Button b = buttons.get(a);
							if(b.bounds.contains(mouse)) {
								press = b;
							}
						}
//						int cd = (int)(Math.random()*100+50);
//						Ship s = new Ship(thisplayer, x, y, 20, 20, 10, cd, 1250, 10, 30);
//						send(s);
//						System.out.println("Sending Ship:"+s.toString());
					} else if(e.getButton()==MouseEvent.BUTTON2) {
//						for(int a=x-200; a<=x+200; a+=FREQ) {
//							for(int b=y-200; b<=y+200; b+=FREQ) {
//								int cd = (int)(Math.random()*100+50);
//								Ship s = new Ship(thisplayer, a, b, 20, 20, 10, cd, 2150, 10, 30);
//								send(s);
//								System.out.println("Sending Ship:"+s.toString());
//							}
//						}
					} else if(e.getButton()==MouseEvent.BUTTON3) {
						Command c = new Command(Command.MOVE, x, y);
						send(c);
						System.out.println("Sending Command:"+c.toString());
					}
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {
					press = null;
				}
				@Override
				public void mouseClicked(MouseEvent arg0) {}
				@Override
				public void mouseEntered(MouseEvent arg0) {}
				@Override
				public void mouseExited(MouseEvent arg0) {}
			});
			gametimer = new Timer(33, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					lookingat.x+=CAMERASPEED*cameradx;
					lookingat.y+=CAMERASPEED*camerady;
					for(int a=lasers.size()-1; a>=0; a--) {
						Laser l = lasers.get(a);
						if(l.widen()) {
							lasers.remove(a--);
						}
					}
					for(int a = explosions.size()-1; a>=0; a--) {
						Explosion e = explosions.get(a);
						if(e.widen()) {
							explosions.remove(a);
						}
					}
					repaint();
				}
			});
		}
	}
	public class ConnectFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JPanel panel;
		JTextField ipaddress;
		JTextField portbox;
		JButton start;
		JTextField usernamebox;
		JTextField redbox, greenbox, bluebox;
		Timer tim;
		public JTextArea serverplayers;
		public JTextArea serverdata;
		public ConnectFrame() {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setSize(500, 500);
			this.setTitle("Client");
			panel = new JPanel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(Color.black);
					g.drawString("IP Address", 25, 25);
					g.drawString("Port", 285, 25);
					g.drawString("Username", 25, 75);
					g.setColor(thisplayer.color);
					g.fillRect(385, 80, 29, 29);
					g.setColor(Color.black);
					g.drawString("Color", 285, 75);
					g.drawString(thisplayer.toString(), 20, 130);
					g.drawString(message, 20, 145);
					g.drawString(message2, 20, 160);
				}
			};
			panel.setLayout(null);
			serverplayers = new JTextArea();
			serverplayers.setSize(200, 200);
			serverplayers.setLocation(20, 170);
			serverplayers.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.add(serverplayers);
			serverdata = new JTextArea();
			serverdata.setSize(200, 200);
			serverdata.setLocation(230, 170);
			serverdata.setBorder(BorderFactory.createLineBorder(Color.black));
			panel.add(serverdata);
			ipaddress = new JTextField("localhost");
			ipaddress.setSize(250, 30);
			ipaddress.setLocation(20, 30);
			panel.add(ipaddress);
			portbox = new JTextField("34567");
			portbox.setSize(90, 30);
			portbox.setLocation(280, 30);
			panel.add(portbox);
			usernamebox = new JTextField("Noob");
			usernamebox.setSize(250, 30);
			usernamebox.setLocation(20, 80);
			panel.add(usernamebox);
//			redbox = new JTextField(""+(int)(Math.random()*25)*10);
//			greenbox = new JTextField(""+(int)(Math.random()*25)*10);
//			bluebox = new JTextField(""+(int)(Math.random()*25)*10);
			redbox = new JTextField(""+50);
			greenbox = new JTextField(""+100);
			bluebox = new JTextField(""+150);
			redbox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(redbox.getText());
					if(!trim.equals(redbox.getText())) {
						redbox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			greenbox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(greenbox.getText());
					if(!trim.equals(greenbox.getText())) {
						greenbox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			bluebox.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					String trim = trimString(bluebox.getText());
					if(!trim.equals(bluebox.getText())) {
						bluebox.setText(trim);
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					keyPressed(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					
				}
			});
			redbox.setSize(30, 30);
			greenbox.setSize(30, 30);
			bluebox.setSize(30, 30);
			redbox.setLocation(280, 80);
			greenbox.setLocation(315, 80);
			bluebox.setLocation(350, 80);
			panel.add(redbox);
			panel.add(greenbox);
			panel.add(bluebox);
			tim = new Timer(200, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
			tim.start();
			start = new JButton("CONNECT");
			start.setSize(90, 30);
			start.setLocation(380, 30);
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("hostin is:"+hostin);
					if(hostin==null) {
						ip = ipaddress.getText();
						try {
							int po = Integer.parseInt(portbox.getText());
							port = po;
							startclient = true;
						} catch(Exception e) {
//							portbox.setText("34567");
							e.printStackTrace();
						}
					} else {
						send(thisplayer);
					}
				}
			});
			panel.add(start);
			this.add(panel);
			this.setVisible(true);
		}
		public void update() {
			String name = usernamebox.getText();
			message = "";
			if(name.toCharArray().length>16) {
				message = "16 Character max for username";
			} else {
				thisplayer.name = name;
			}
			try {
				int red = Integer.parseInt(redbox.getText());
				int green = Integer.parseInt(greenbox.getText());
				int blue = Integer.parseInt(bluebox.getText());
				thisplayer.color = new Color(red, green, blue);
			} catch(Exception e) {
				message = "Range for colors is [0,255]";
				System.out.println("Color box is broken!");
			}
			repaint();
		}
		public void addText(String s) {
			serverplayers.setText(serverplayers.getText()+s);
		}
		public void setText(String s) {
			serverplayers.setText(s);
		}
	}
}
