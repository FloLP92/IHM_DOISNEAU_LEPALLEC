package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.Flight;
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
	private static Pays paysSelection;
	private static JTextArea infosAvion;
	
	
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
						"Le panel de contr�le de gauche se d�compose en 3 parties : partie gestion de la lecture du fichier"
						+ ", la partie s�l�ction d'un vol particuler et un filtre des vols et a�roports.\n Le panel de gauche correspond � la vue de l'application"
						+ " qui prend notamment en compte les param�tres du panel de gauche.  \n" + '\n');
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
		BoxLayout b = new BoxLayout(c,1);
		c.setLayout(b);
		
		//Partie Lecture du fichier
		JPanel panelLecture = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panelLecture.setPreferredSize(new Dimension(400,200));
		
		JLabel statut = new JLabel("Statut :");
		statut.setFont(new Font("Tahoma", Font.BOLD, 12));
		JButton lectureAction = new JButton(new ImageIcon("ressources/play_icon.png"));
		lectureAction.setBackground(new Color(59, 89, 182));
		lectureAction.setForeground(Color.WHITE);
		lectureAction.setFocusPainted(false);
		lectureAction.setFont(new Font("Tahoma", Font.BOLD, 12));
		JLabel lectureStatut = new JLabel("Reprendre la lecture");
		lectureStatut.setFont(new Font("Tahoma", Font.BOLD, 12));
		final int vitesseMin = 1;
		final int vitesseMax = 10;
		JSlider vitesseLecture = new JSlider(JSlider.HORIZONTAL,vitesseMin,vitesseMax,vitesseMin);
		vitesseLecture.setFont(new Font("Tahoma", Font.BOLD, 12));
		vitesseLecture.setForeground(new Color(59, 89, 182));
		vitesseLecture.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				app.enqueue(new Callable<Object>()
				{
					public Object call() throws Exception
					{
						app.setVitesseLecture(vitesseLecture.getValue());
						return null;
					}
				});	
			}
		});
		vitesseLecture.setMajorTickSpacing(1);
		vitesseLecture.setPaintTicks(true);
		vitesseLecture.setPaintLabels(true);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 80;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(10,10,10,0);
		panelLecture.add(statut,gbc);
		gbc.gridy=1;
		gbc.gridwidth = 2;
		panelLecture.add(lectureAction,gbc);
		gbc.gridx=2;
		gbc.gridwidth = 1;
		panelLecture.add(lectureStatut,gbc);
		gbc.gridy=2;
		gbc.gridx=0;
		gbc.gridwidth = 3;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		panelLecture.add(vitesseLecture,gbc);
		panelLecture.setBorder(BorderFactory.createTitledBorder(
	                "Lecture du fichier"));
		
		lectureAction.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(lectureStatut.getText().equals("Reprendre la lecture")){//On �tait en pause
					lectureAction.setIcon(new ImageIcon("ressources/pause_icon.png"));
					lectureStatut.setText("Arreter la lecture");
					app.enqueue(new Callable<Object>()
					{
						public Object call() throws Exception
						{
							app.setLecture();
							return null;}
					});
					
				}
				else
				{
					lectureAction.setIcon(new ImageIcon("ressources/play_icon.png"));
					lectureStatut.setText("Reprendre la lecture");
					app.enqueue(new Callable<Object>()
					{
						public Object call() throws Exception
						{
							app.setLecture();
							return null;}
					});
				}
			}
		});
		
		
		
		//Partie Selection Avion
		JPanel panelAvion = new JPanel(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		panelAvion.setPreferredSize(new Dimension(400,200));

		JLabel jlab = new JLabel("Selectionnez un avion :");
		jlab.setFont(new Font("Tahoma", Font.BOLD, 12));
		infosAvion = new JTextArea("Ici apparaitera les informations sur l'avion selectionne ...");
		infosAvion.setWrapStyleWord(true);
		infosAvion.setFont(new Font("Tahoma", Font.BOLD, 12));
		JScrollPane scrolltxt = new JScrollPane(infosAvion,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrolltxt.setWheelScrollingEnabled(true); 
        scrolltxt.getVerticalScrollBar().isVisible();
		JComboBox j = new JComboBox();
		j.setBackground(new Color(59, 89, 182));
		j.setForeground(Color.WHITE);
		j.setFont(new Font("Tahoma", Font.BOLD, 12));
		j.addItem(" Aucun Avion selectionne");
		for (HashMap.Entry<String,Flight> entry : listFlights.entrySet()){
			j.addItem(""+entry.getKey());
		}
		j = trierCombo(j);
		JButton vue = new JButton(new ImageIcon("ressources/view_icon.png"));
		vue.setBackground(new Color(59, 89, 182));
		vue.setForeground(Color.WHITE);
		vue.setFocusPainted(false);
		vue.setFont(new Font("Tahoma", Font.BOLD, 12));
		j.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1){//Nouvel objet
					//System.out.println(listFlights.get(e.getItem()));
					infosAvion.setText(listFlights.get(e.getItem()).toString());
					app.enqueue(new Callable<Object>()
					{
						public Object call() throws Exception
						{
							app.selectionColor(realTimeFlight.get(e.getItem()).getSpatial());
							return null;}
					});
				}
			}
		});
		gbc1.gridx=0;
		gbc1.gridy=0;
		gbc1.ipadx = 80;
		gbc1.gridwidth = 1;
		gbc1.gridheight = 1;
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		gbc1.insets = new Insets(10,10,10,10);
		panelAvion.add(jlab,gbc1);
		gbc1.gridy=1;
		panelAvion.add(j,gbc1);
		gbc1.gridheight = 3;
		gbc1.gridwidth=10;
		gbc1.gridy=2;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.weightx = 10;
		gbc1.weighty = 3;
		gbc1.ipady = 70; 
		panelAvion.add(scrolltxt,gbc1);
		gbc1.ipady=0;
		gbc1.gridheight = 1;
		gbc1.fill = GridBagConstraints.NONE;
		gbc1.gridy=5;
		gbc1.gridx=0;
		gbc1.gridwidth=1;
		panelAvion.add(vue,gbc1);
		panelAvion.setBorder(BorderFactory.createTitledBorder(
	                "Selection Avion"));
		
		//Partie Filtrer vols
		JPanel panelVols = new JPanel(new GridBagLayout());
		panelVols.setPreferredSize(new Dimension(400,300));
		GridBagConstraints gbc2 = new GridBagConstraints();
		
		JLabel typeVol = new JLabel("Type vol :");
		ButtonGroup radioButtons = new ButtonGroup();
		
		JRadioButton entrants = new JRadioButton("entrants");
		entrants.setFocusPainted(false);
		entrants.setFont(new Font("Tahoma", Font.BOLD, 12));
		entrants.setActionCommand("entrants");
		entrants.setMnemonic(0);
		JRadioButton sortants = new JRadioButton("sortants");
		sortants.setFocusPainted(false);
		sortants.setFont(new Font("Tahoma", Font.BOLD, 12));
		sortants.setMnemonic(1);
		entrants.setActionCommand("sortants");
		
		radioButtons.add(entrants);
		radioButtons.add(sortants);
		
		
		JComboBox aeroportSelected = new JComboBox();
		aeroportSelected.setBackground(new Color(59, 89, 182));
		aeroportSelected.setForeground(Color.WHITE);
		aeroportSelected.setFont(new Font("Tahoma", Font.BOLD, 12));
		final JComboBox innerAero = aeroportSelected; //Duplication pour etre utilise dans les listeners
		aeroportSelected.addItem(" Aucun Aeroport selectionne");
		for (HashMap.Entry<String,Airport> entry : listAirports.entrySet()){
			aeroportSelected.addItem(""+entry.getKey());
		}
		aeroportSelected = trierCombo(aeroportSelected);
		aeroportSelected.setSelectedItem(" Aucun Aeroport selectionne");
		
		JComboBox paysSelected = new JComboBox();
		paysSelected.setBackground(new Color(59, 89, 182));
		paysSelected.setForeground(Color.WHITE);
		paysSelected.setFont(new Font("Tahoma", Font.BOLD, 12));
		final JComboBox innerPays = paysSelected; //Duplication pour etre utilise dans les listeners
		paysSelected.addItem(" Aucun Pays selectionne");
		ArrayList <String> aeroportsEnCours = new ArrayList<String>();
		for (HashMap.Entry<String,Pays> entry : listPays.entrySet()){
			paysSelected.addItem(""+entry.getKey());
		}
		paysSelected = trierCombo(paysSelected);
		
		paysSelected.addItemListener(new ItemListener() {
		     @Override
		     public void itemStateChanged(ItemEvent e) {
		    	 if(e.getStateChange() == 1){//Nouvel objet
		    		String pays = e.getItem()+"";
		    		//On update la liste des aeroports par rapport a la liste des pays
		    		innerAero.removeAllItems();
		    		if(pays.equals(" Aucun Pays selectionne")){
		    			innerAero.addItem(" Aucun Aeroport selectionne");
		    			paysSelection = null;
		    			for (HashMap.Entry<String,Airport> entry : listAirports.entrySet()){
		    				innerAero.addItem(entry.getValue().getIdIATA());
		    			}
		    			JComboBox innerAero2 = trierCombo(innerAero);
		    		}//
		    		else{
		    			innerAero.addItem(" Aucun Aeroport selectionne");
			    		for (HashMap.Entry<String,Airport> entry : listAirports.entrySet()){
			    			String pays2 = entry.getValue().getPays().getNomPays();
			    			
			    			if(pays2.equals(pays)){
			    				innerAero.addItem(entry.getValue().getIdIATA());
			    				paysSelection = listPays.get(pays);
			    				JComboBox innerAero2 = trierCombo(innerAero);
			    			}
			    		}
		    		}
		    		
		    	 }
		     }

		 });
		
		JButton appliquerFiltre = new JButton("Appliquer le filtre");
		appliquerFiltre.setBackground(new Color(59, 89, 182));
		appliquerFiltre.setForeground(Color.WHITE);
		appliquerFiltre.setFocusPainted(false);
		appliquerFiltre.setFont(new Font("Tahoma", Font.BOLD, 12));
		JButton annulerFiltre = new JButton("Annuler le filtre");
		annulerFiltre.setBackground(new Color(59, 89, 182));
		annulerFiltre.setForeground(Color.WHITE);
		annulerFiltre.setFocusPainted(false);
		annulerFiltre.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.ipadx = 50;
		gbc2.gridheight = 1;
	    gbc2.gridwidth = 1;
	    gbc2.insets = new Insets(10,10,10,10);
	    panelVols.add(typeVol,gbc2);
		
	    gbc2.gridy = 1;
	    panelVols.add(entrants,gbc2);
	    gbc2.gridx =1;
	    panelVols.add(sortants,gbc2);
	    gbc2.gridx=0;
	    gbc2.gridy=2;
	    gbc2.gridwidth = 2;
	    gbc2.fill =1;
	    panelVols.add(paysSelected,gbc2);
	    gbc2.gridy = 3;
	    panelVols.add(aeroportSelected,gbc2);
	    gbc2.gridy =4;
	    gbc2.fill =0;
	    gbc2.gridwidth = 1;
	    panelVols.add(annulerFiltre,gbc2);
	    gbc2.gridx = 1;
	    panelVols.add(appliquerFiltre,gbc2);
	    panelVols.setBorder(BorderFactory.createTitledBorder(
                "Filtrer les vols"));
	    
	    appliquerFiltre.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				app.enqueue(new Callable<Object>()
				{
					public Object call() throws Exception
					{
				       

						if(innerAero.getSelectedItem().equals(" Aucun Aeroport selectionne")){
							app.displayAirportPays(paysSelection);
							String result = radioButtons.getSelection().getActionCommand();
					        if(radioButtons.getSelection().getMnemonic()==0){
					        	app.displayPlaneFrom(paysSelection);
					        }
							else if(radioButtons.getSelection().getMnemonic()==1){
								app.displayPlaneTo(paysSelection);
							}
						}
						else{
							Airport airportSelected = listAirports.get(innerAero.getSelectedItem());
							System.out.println(innerAero.getSelectedIndex());
							app.displayAirportPays(airportSelected);
					        if(radioButtons.getSelection().getMnemonic()==0){
					        	app.displayPlaneFrom(airportSelected);
					        }
							else if(radioButtons.getSelection().getMnemonic()==1){
								app.displayPlaneTo(airportSelected);
							}
						}
							return null;
						
					}
				});
			}
		});
	    
		annulerFiltre.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//Clear des JRadioButtons
				//radioButtons.clearSelection();
				
				//Clear de la Box des aeros
				/*innerAero.removeAllItems();
				innerAero.addItem(" Aucun Aeroport Selectionne");
				for (HashMap.Entry<String,Airport> entry : listAirports.entrySet()){
					innerAero.addItem(entry.getValue().getIdIATA());
					JComboBox innerAero2 = trierCombo(innerAero);
				}*/
				
				//Clear de la box des pays
				//innerPays.setSelectedItem(" Aucun Pays selectionne");
				app.enqueue(new Callable<Object>()
				{
					public Object call() throws Exception
					{
						app.displayAirportPays();
						return null;}
				});
				
			}
		});
	    
	    
	    //Partie global
	    c.add(panelLecture);
		c.add(panelAvion);
		c.add(panelVols);
		c.setBackground(Color.WHITE);
		panel.add(c, BorderLayout.WEST);
		//
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
	
	public static JComboBox trierCombo (JComboBox Combo){
				Object aux;
		int indice = Combo.getItemCount();
			 
				for(int i=0;i<indice-1;i++){
					for(int j=i+1;j<indice;j++){
						if(Combo.getItemAt(i).toString().compareTo(Combo.getItemAt(j).toString()) > 0){
							aux = Combo.getItemAt(i);
							Combo.removeItemAt(i);
							Combo.insertItemAt(Combo.getItemAt(j-1),i);
							Combo.removeItemAt(j);
							Combo.insertItemAt(aux,j-1);
						}
				    }
				}
				return Combo;
			}
			
	public static void update(JComboBox combo){
		
	}
	public static void changeActualVol(String id)
	{
		try
		{
			infosAvion.setText(listFlights.get(id.replaceAll("\\s+","")).toString());
		}
		catch(NullPointerException e)
		{
			infosAvion.setText("Vol non présent dans notre fichier");
		}

	}
	
}