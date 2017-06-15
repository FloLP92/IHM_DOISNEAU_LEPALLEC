package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;

import main.Flight;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class MainSystem 
{
	private static HashMap<String,Airport> listAirports;
	private static HashMap<String,Flight> listFlights;
	private static HashMap<String,Pays> listPays;
	private static HashMap<String,RealTimeFlight> realTimeFlight;
	private static HashMap<String,ArrayList<Vector3f>> listVectors;
	private static JFrame frame;
	private static JPanel panel;
	private static EarthTest app;
	private static Canvas canvas; // JAVA Swing Canvas
	
	
	public MainSystem()
	{
		listAirports = new HashMap<String,Airport>();
		listFlights = new HashMap<String,Flight>();
		listPays = new HashMap<String,Pays>();
		realTimeFlight = new HashMap<String,RealTimeFlight>();
		listVectors = new HashMap<String,ArrayList<Vector3f>>();

		
		MainSystem.lireFichier("ressources/airports.dat");
		MainSystem.lireFichier("ressources/flights.dat");
		
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1200, 800);
		settings.setSamples(8);
		settings.setFrameRate(60);
		settings.setVSync(true);
		app = new EarthTest();
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setDisplayStatView(false);
		app.setDisplayFps(false);
						
		app.setPauseOnLostFocus(false);
		app.createCanvas(); // create canvas!
		JmeCanvasContext ctx = (JmeCanvasContext) app.getContext();
		canvas = ctx.getCanvas();
		Dimension dim = new Dimension(settings.getWidth(), settings.getHeight());
		canvas.setPreferredSize(dim);
		createNewJFrame();
	}
	private static void createNewJFrame() {

		frame = new JFrame("Java - Graphique - IHM");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e) 
			{
				app.stop();
			}
		});
		
		panel = new JPanel(new BorderLayout());

		// Create the menus
		final JMenuBar menubar = new JMenuBar();
		final JMenu objectsMenu = new JMenu("File");
		final JMenu helpMenu = new JMenu("Help");

		final JMenuItem createObjectItem = new JMenuItem("Create an object");
		final JMenuItem deleteObjectItem = new JMenuItem("Delete an object");
		final JMenuItem getControlsItem = new JMenuItem("Get controls");

		objectsMenu.add(createObjectItem);
		objectsMenu.add(deleteObjectItem);
		helpMenu.add(getControlsItem);
		menubar.add(objectsMenu);
		menubar.add(helpMenu);
		frame.setJMenuBar(menubar);

		getControlsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFrame dial = new JFrame("Controls");
				final JPanel pane = new JPanel();
				pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

				JTextArea cautionText = new JTextArea(
						"Add some text here to describe the controls \n" + '\n');
				cautionText.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
				cautionText.setEditable(false);
				pane.add(cautionText);

				JButton okButton = new JButton("Ok");
				okButton.setSize(50, okButton.getHeight());
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dial.dispose();
					}
				});

				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
				buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
				buttonPane.add(Box.createHorizontalGlue());
				buttonPane.add(okButton);

				pane.add(buttonPane);
				pane.add(Box.createRigidArea(new Dimension(0, 5)));
				dial.add(pane);
				dial.pack();
				dial.setLocationRelativeTo(frame);
				dial.setVisible(true);
			}
		});
		
		
		//Partie 2D de gauche
	   
		
		Container c = new Container();
		//Global Part
		c.setLayout(new BoxLayout(c, 1));
		
		
		//Partie Selection Avion
		JPanel jp = new JPanel(new GridLayout(3,1));
		JLabel jlab = new JLabel("Selectionnez un avion :");
		
		Object[] elements = new Object[]{"Element 1", "Element 2", "Element 3", "Element 4", "Element 5"};
		JComboBox j = new JComboBox(elements);
		JButton vue = new JButton("Vue avion");
		//
		
		jp.add(jlab);
		jp.add(j);
		jp.add(vue);
		jp.setPreferredSize(new Dimension(500,500));
		jp.setMaximumSize(new Dimension(400,200));
		//jp.setBounds(50, 50, 100,100);
		c.add(jp);
		
		panel.add(c, BorderLayout.WEST);
		
		// Add the canvas to the panel
		panel.add(canvas, BorderLayout.CENTER);
		
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static HashMap<String,Pays> getListPays()
	{
		return listPays;
	}
	public static HashMap<String,Airport> getListAirports()
	{
		return listAirports;
	}
	public static HashMap<String,Flight> getListFlight()
	{
		return listFlights;
	}
	public Airport getAirportDepart(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getAirportDepart();
		}
		else
			return null;
	}
	public Airport getAirportDest(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getAirportDest();
		}
		else
			return null;
	}
	public String getVilleAirportDepart(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getAirportDepart().getVille();
		}
		else
			return null;
	}
	public String getVilleAirportDest(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getAirportDest().getVille();
		}
		else
			return null;
	}
	public Pays getPaysDepart(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getPaysDepart();
		}
		else
			return null;
	}
	public Pays getPaysDest(String id)
	{
		if(listFlights.containsKey(id))
		{
			return listFlights.get(id).getPaysDest();
		}
		else
			return null;
	}
	public static void lireFichier(String nameFile)
	{
		try
		{
			FileReader file = new FileReader(nameFile);
			BufferedReader bufRead = new BufferedReader(file);
			String line = bufRead.readLine();
			if(nameFile.compareTo("ressources/airports.dat")==0)
			{
				while(line != null)
				{
					String[] array = line.split(",");
					String[] parts = array[0].split("///");
					if(!listPays.containsKey(parts[1]))
					{
						listPays.put(parts[1],new Pays(parts[1]));
					}
					Airport a = new Airport(parts[0],MainSystem.getListPays().get(parts[1]),
							parts[2],Float.parseFloat(parts[3]),Float.parseFloat(parts[4]));
					listPays.get(parts[1]).ajouterAirport(a);
					listAirports.put(parts[2],a);
					
					line = bufRead.readLine();
				}
				bufRead.close();
				file.close();
			}
			else if(nameFile.compareTo("ressources/flights.dat")==0)
			{
				while(line != null)
				{
					String[] array = line.split(",");
					String[] parts = array[0].split("///");
					Flight f = new Flight(parts[0],MainSystem.getListAirports().get(parts[1]),
							getListAirports().get(parts[2]),parts[3],parts[4]);
					listAirports.get(parts[1]).ajoutVolDepart(f);
					listAirports.get(parts[2]).ajoutVolDest(f);
					listFlights.put(parts[0],f);
					
					line = bufRead.readLine();
				}
				bufRead.close();
				file.close();
			}	
		}catch(IOException e){
			e.printStackTrace();
		}	
	}
	public static HashMap<String,RealTimeFlight> getRealTimeFlight()
	{
		return realTimeFlight;
	}
	public static void updateRealTimeFlight(String id,RealTimeFlight r)
	{
			realTimeFlight.put(id,r);
	}
	public static void addVector(String id,Vector3f v)
	{
		if(listVectors.containsKey(id))
			listVectors.get(id).add(v);
		else
		{
			ArrayList<Vector3f> a = new ArrayList<Vector3f>();
			a.add(v);
			listVectors.put(id,a);
		}	
	}
	public static HashMap<String,ArrayList<Vector3f>> getListVectors()
	{
		return listVectors;
	}
	public static void main(String[] args) throws IOException
	{
		new MainSystem();
	}
	
	
}
