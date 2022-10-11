package Building;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import Database.Database;
import Database.Employee;
import Database.Room;

/**
 * Display floor layout and any necessary action buttons
 * @author Joshua Cohen
 *
 */
public class FloorLayoutGui {
	//Size of overlayed grid - used to show locations of employees/rooms
	final int x = 60, y = 60;

	//Third and fourth floor layouts as png
	Image thirdfloor, fourthfloor;
	//JLabel wrappers
	JLabel third, fourth, lablogo, loading;
	//Frames
	JFrame frame, load;
	//Database instance
	Database db = new Database();
	//Button mappings
	JButton[][] fourthbuttons, thirdbuttons;
	//Used for search queries
	JTextField txt;

	public FloorLayoutGui() throws IOException {
		//add loading screen
		load = new JFrame();
		load.setLayout(new FlowLayout());
		load.setSize(200,130);		
		load.setLocationRelativeTo(null);
		load.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		load.setVisible(true);

		/*************** LOADING MESSAGE ON START *********************/
		new Thread(()->{
			loading = new JLabel("Loading");			
			JLabel loadlogo = new JLabel();
			Image logo = new ImageIcon(this.getClass().getResource("/Building/FM_logo.png")).getImage().getScaledInstance(175, 50, Image.SCALE_SMOOTH);
			loadlogo.setIcon(new ImageIcon(logo));
			
			load.add(loadlogo);
			load.add(loading);

			while(load.isVisible()) {
				switch(loading.getText()) {
				case "Loading":
					loading.setText("Loading.");
					break;
				case "Loading.":
					loading.setText("Loading..");
					break;
				case "Loading..":
					loading.setText("Loading...");
					break;
				case "Loading...":
					loading.setText("Loading");
					break;
				default:
					break;
				}

				load.invalidate();
				load.validate();
				load.repaint();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}).start();

		/******************** CREATE NEW FRAME ***************/
		frame = new JFrame("FM Database");
		frame.setLayout(new BorderLayout());
		frame.setSize(1000, 500);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		//Quit Chrome on frame close - avoid duplicate instances of Chrome Driver
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				db.quitChrome();
			}
		});

		/***************** FLOOR LAYOUT IMAGES ********************/
		thirdfloor = new ImageIcon(this.getClass().getResource("/Building/FM_Third_Floor.png")).getImage().getScaledInstance(1500, 1000, Image.SCALE_SMOOTH);
		fourthfloor = new ImageIcon(this.getClass().getResource("/Building/FM_Fourth_Floor.png")).getImage().getScaledInstance(1500, 1000, Image.SCALE_SMOOTH);

		/******************* BUTTONS ************************/
		//used to show location of employees

		//initialize empty buttons
		thirdbuttons = new JButton[x][y];
		fourthbuttons = new JButton[x][y];

		//Wrap into JLabel icons and then overlayable containers
		third = new JLabel();
		third.setLayout(new GridLayout(thirdbuttons.length,thirdbuttons[0].length));
		third.setIcon(new ImageIcon(thirdfloor));

		for (int i = 0; i < thirdbuttons.length; i++) {
			for (int j = 0; j < thirdbuttons[0].length; j++) {
				JButton b = new JButton();
				b.setOpaque(false);
				b.setContentAreaFilled(false);
				b.setBorderPainted(false);
				third.add(b);
				thirdbuttons[i][j] = b;
			}
		}

		fourth = new JLabel();
		fourth.setLayout(new GridLayout(fourthbuttons.length,fourthbuttons[0].length));
		fourth.setIcon(new ImageIcon(fourthfloor));

		for (int i = 0; i < fourthbuttons.length; i++) {
			for (int j = 0; j < fourthbuttons[0].length; j++) {
				JButton b = new JButton();
				b.setOpaque(false);
				b.setContentAreaFilled(false);
				b.setBorderPainted(false);
				fourth.add(b);
				fourthbuttons[i][j] = b;
			}
		}

		//Add images to panel
		JPanel pnl = new JPanel(new GridLayout(2,1));
		pnl.add(fourth);
		pnl.add(third);

		//add panel to scroll bar and scroll bar to frame
		JScrollPane scroll = new JScrollPane(pnl);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getHorizontalScrollBar().setUnitIncrement(16);
		frame.add(scroll);

		/******************************* CONTROL PANEL WITH FUNCTIONAL BUTTONS *****************************/
		//add control buttons at top
		JPanel controls = new JPanel(new FlowLayout());
		//add logo to top left
		Image logo = new ImageIcon(this.getClass().getResource("/Building/FM_logo.png")).getImage();
		Image newlogo = logo.getScaledInstance(200, 55, Image.SCALE_SMOOTH);
		lablogo = new JLabel();
		lablogo.setIcon(new ImageIcon(newlogo));
		controls.add(lablogo);

		/************************* SEARCH FUNCTION *****************************/
		txt = new JTextField();
		txt.setColumns(20);
		txt.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					removeActionListeners();
					hideButtons();
					showResults();
				}
			}

			@Override public void keyReleased(KeyEvent arg0) {}
			@Override public void keyTyped(KeyEvent arg0) {}

		});

		JButton search = new JButton("Search");
		search.addActionListener((e)-> {
			removeActionListeners();
			hideButtons();
			showResults();
		});
		controls.add(txt);
		controls.add(search);

		/**************************** UMD DIRECTORY *******************************/
		JButton dir = new JButton("Directory");
		dir.addActionListener((e)->{
			hideButtons();
			removeActionListeners();
			String results = db.directorySearch(txt.getText());

			JScrollPane scrollPane = new JScrollPane(new TextArea(results));
			scrollPane.getVerticalScrollBar().setUnitIncrement(5);
			scrollPane.setPreferredSize(new Dimension(500,500));
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			load.setVisible(false);
			JOptionPane.showMessageDialog(frame, scrollPane);
		});
		controls.add(dir);
		db.startChrome();

		/************** ZOOM FUNCTIONS *********************/
		JButton in = new JButton("+");
		in.addActionListener((e)-> {
			Image newimg = thirdfloor.getScaledInstance(third.getIcon().getIconWidth()+225, third.getIcon().getIconHeight()+150, Image.SCALE_SMOOTH);
			third.setIcon(new ImageIcon(newimg));
			newimg = fourthfloor.getScaledInstance(fourth.getIcon().getIconWidth()+225, fourth.getIcon().getIconHeight()+150, Image.SCALE_SMOOTH);
			fourth.setIcon(new ImageIcon(newimg));
		});
		JButton out = new JButton("-");
		out.addActionListener((e)->{
			if (third.getIcon().getIconWidth() - 225 >= 1500) {
				Image newimg = thirdfloor.getScaledInstance(third.getIcon().getIconWidth()-225, third.getIcon().getIconHeight()-150, Image.SCALE_SMOOTH);
				third.setIcon(new ImageIcon(newimg));
				newimg = fourthfloor.getScaledInstance(fourth.getIcon().getIconWidth()-225, fourth.getIcon().getIconHeight()-150, Image.SCALE_SMOOTH);
				fourth.setIcon(new ImageIcon(newimg));
			}
		});
		controls.add(new JLabel("       "));
		controls.add(out);
		controls.add(in);
		controls.add(new JLabel("       "));

		/****************** HELP BUTON ****************************/
		JButton help = new JButton("Help");
		help.addActionListener((e)->{
			String message = "Press Enter/Click Search to query internal database\n"
					+ "Click Directory to search UMD Directory\n\n"
					+ "Possible Search Queries:\n\n"
					+ "Employees:\n"+
					"Employees - Search for all employees\n"+
					"Or by: Name, Position, Department, Room Number, Cell #, Extension\n\n"+
					"Department Queries:\n	FBO\n		Acc&Fin - Accounting\n		GIS - Geo Info Systems\n		TechServ - Technology Service\n"
					+ "	P&C\n		PM - Project Managers\n		FP - Facilities Planning\n		CB - Capital Budgeting\n		Ops - Operations\n		DS - Design Services\n"
					+ "		TechSupport - Technology Support\n"
					+ "	HR - Payroll - E&E - AVP - B&LM - O&M\n\n"+
					"Rooms:\n"+
					"Rooms - Search for all rooms\n"+
					"Or by: Room Number, Tag\n\n"+
					"Tag Queries:\n	Huddle - Conference - Coffee - Bathroom - Janitor - Materials_Library - Mail\n"+
					"	Phone - Cafe - Electrical - Storage - Stairs - Elevator - Lactation - MISC\n\n"
					+ "Feedback: https://forms.gle/ejjWA716JwJmN34t5\n\n"
					+ "Credit: Joshua Cohen";

			JScrollPane scrollPane = new JScrollPane(new TextArea(message));
			scrollPane.setPreferredSize(new Dimension(750,300));
			JOptionPane.showMessageDialog(frame, scrollPane);
		});
		controls.add(help);

		/******************************************************************************/
		//add controls
		frame.add(controls, BorderLayout.NORTH);

		//show frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		load.setVisible(false);
		frame.setVisible(true);
	}

	/* grid buttons action listener */
	private class showInfo implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int r = 0, c = 0, floor = 0;

			for (int x = 0; x < thirdbuttons.length; x++) {
				for (int y = 0; y < thirdbuttons[0].length; y++) {
					if (thirdbuttons[x][y].equals(e.getSource())) {
						r = x;
						c = y;
						floor = 3;
					} else if (fourthbuttons[x][y].equals(e.getSource()) ) {
						r = x;
						c = y;
						floor = 4;
					}
				}
			}

			Collection<Object> info = db.search(c+"-"+r+"-"+floor);
			String result = db.compileResults(info);

			JOptionPane.showMessageDialog(frame, result);
		}

	}

	/* Hides the button on the screen */
	private void hideButtons() {
		for (int i = 0; i < thirdbuttons.length; i++) {
			for (int j = 0; j < thirdbuttons.length; j++) {
				JButton t = thirdbuttons[i][j];
				JButton f = fourthbuttons[i][j];

				t.setOpaque(false);
				t.setContentAreaFilled(false);
				t.setBorderPainted(false);
				f.setOpaque(false);
				f.setContentAreaFilled(false);
				f.setBorderPainted(false);
			}
		}
	}

	/* Add buttons to screen */
	private void addButtons(Collection<Object> results) {

		//add to layout
		for (Object o : results) {
			if (o instanceof Employee) {
				Employee e = (Employee) o;
				//fourth floor
				if (e.getRoom_number().substring(0,1).equals("4")) {
					JButton b = fourthbuttons[e.getGridY()][e.getGridX()];
					b.setOpaque(true);
					b.setContentAreaFilled(true);
					b.setBorderPainted(true);
					b.setBackground(Color.red);
					b.addActionListener(new showInfo());
					b.setBorder(BorderFactory.createLineBorder(Color.BLACK));

					fourth.invalidate();
					fourth.validate();
					fourth.repaint();
				} else if (e.getRoom_number().substring(0,1).equals("3")) {
					JButton b = thirdbuttons[e.getGridY()][e.getGridX()];
					b.setOpaque(true);
					b.setContentAreaFilled(true);
					b.setBorderPainted(true);
					b.setBackground(Color.red);
					b.addActionListener(new showInfo());
					b.setBorder(BorderFactory.createLineBorder(Color.BLACK));

					third.invalidate();
					third.validate();
					third.repaint();
				}
			} else if (o instanceof Room) {
				Room r = (Room) o;
				if (r.getRoom_number().substring(0,1).equals("4") || r.getRoom_number().substring(0,2).equals("R4")
						|| r.getRoom_number().substring(0,2).equals("E4")
						|| r.getRoom_number().substring(0,2).equals("T4")
						|| r.getRoom_number().substring(0,2).equals("S4")
						|| r.getRoom_number().substring(0,2).equals("M4")) {
					JButton b = fourthbuttons[r.getGridY()][r.getGridX()];
					b.setOpaque(true);
					b.setContentAreaFilled(true);
					b.setBorderPainted(true);
					b.setBackground(Color.red);
					b.addActionListener(new showInfo());
					b.setBorder(BorderFactory.createLineBorder(Color.BLACK));

					fourth.invalidate();
					fourth.validate();
					fourth.repaint();
				} else if (r.getRoom_number().substring(0,1).equals("3") || r.getRoom_number().substring(0,2).equals("R3")
						|| r.getRoom_number().substring(0,2).equals("E3")
						|| r.getRoom_number().substring(0,2).equals("T3")
						|| r.getRoom_number().substring(0,2).equals("S3")
						|| r.getRoom_number().substring(0,2).equals("M3")) {
					JButton b = thirdbuttons[r.getGridY()][r.getGridX()];
					b.setOpaque(true);
					b.setContentAreaFilled(true);
					b.setBorderPainted(true);
					b.setBackground(Color.red);
					b.addActionListener(new showInfo());
					b.setBorder(BorderFactory.createLineBorder(Color.BLACK));

					third.invalidate();
					third.validate();
					third.repaint();
				}
			}
		}
	}

	/* Show results on screen */
	private void showResults() {
		String query = txt.getText();
		Collection<Object> results = db.search(query);
		String showResults = db.compileResults(results, query);
		JScrollPane scrollPane = new JScrollPane(new TextArea(showResults));
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		scrollPane.setPreferredSize(new Dimension(500,500));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		addButtons(results);

		repaint();
		//popup
		JOptionPane.showMessageDialog(frame, scrollPane);
	}

	/* Remove action listeners */
	private void removeActionListeners() {
		//remove all action listeners
		for (int k = 0; k < thirdbuttons.length; k++) {
			for (int j = 0; j < thirdbuttons[0].length; j++) {
				for (ActionListener al : thirdbuttons[j][k].getActionListeners()) {
					thirdbuttons[j][k].removeActionListener(al);
				}
				for (ActionListener al : fourthbuttons[j][k].getActionListeners()) {
					fourthbuttons[j][k].removeActionListener(al);
				}
			}
		}
	}

	/* Repaint the screen */
	private void repaint() {
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}

}
