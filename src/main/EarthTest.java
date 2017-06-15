package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.Timer;

public class EarthTest extends SimpleApplication {

	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;
	Node earth_node;
	Node LinesNode;
	Node SpheresNode;
	private HashMap<String,RealTimeFlight> rf;
	private float chLat,chLong;
	private static Plane plane;
	private static ArrayList<Spatial> listPlanes;
	private static final ColorRGBA tabColor[] = {ColorRGBA.Red,ColorRGBA.Blue,ColorRGBA.Pink,
			ColorRGBA.Yellow,ColorRGBA.Gray,ColorRGBA.Cyan,ColorRGBA.Black,ColorRGBA.Magenta,
			ColorRGBA.Orange,ColorRGBA.White,ColorRGBA.Green,ColorRGBA.BlackNoAlpha};

	@Override
	public void simpleInitApp() 
	{
		final JMenuBar menubar = new JMenuBar();
		final JMenu objectsMenu = new JMenu("File");
		final JMenu helpMenu = new JMenu("Help");
		menubar.add(objectsMenu);
		menubar.add(helpMenu);
		
		LinesNode = new Node("LinesNode");
		SpheresNode = new Node("SpheresNode");
		assetManager.registerLocator("earth.zip", ZipLocator.class);
		Spatial earth_geom = assetManager.loadModel("earth/Sphere.mesh.xml");
		earth_node = new Node("earth");
		earth_node.attachChild(earth_geom);
		//earth_node.setLocalScale(5.0f);
		rootNode.attachChild(earth_node);
		
		AmbientLight ambientlLight = new AmbientLight();
		ambientlLight.setColor(ColorRGBA.White.mult(1.7f));
		rootNode.addLight(ambientlLight);
		viewPort.setBackgroundColor(new ColorRGBA(0.1f,0.1f,0.1f,1.0f));
		flyCam.setEnabled(false);
		ChaseCamera chaseCam = new ChaseCamera(cam,earth_geom,inputManager);
		chaseCam.setDragToRotate(true);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10.0f);
		chaseCam.setMinVerticalRotation((float)-(Math.PI/2 - 0.0001f));
		chaseCam.setMaxVerticalRotation((float)+Math.PI/2);
		chaseCam.setMinDistance(2.5f);
		chaseCam.setMaxDistance(30.0f);
		
		
		
		//-------------------AFFICHAGE AEROPORTS--------------------------
		for (Airport value : MainSystem.getListAirports().values()) 
		{
			displayTown(value.getLatitude(),value.getLongitude());  
		}
		//-----------------------------------------------------------------

		//-------------------INITIALISATION AVIONS--------------------------
		listPlanes = new ArrayList<Spatial>();
		for ( int i=0; i<MainSystem.getListFlight().size(); i++ ) 
		{
			Spatial planeSpatial = assetManager.loadModel("earth/plane.obj");
			listPlanes.add(planeSpatial);
		}
		//-----------------------------------------------------------------
		
		
		rf = MainSystem.getRealTimeFlight();
		
		/*
		objet orienté pariel que le monde
		lookAt pour s aligner regarder l aterre
		si on fais avancer reculer que altitude qui va changer*/
		//while( !updatePositions() )
		
		updatePositions();
		int pos = 0;
		RealTimeFlight.enableRead();
		for( RealTimeFlight r : MainSystem.getRealTimeFlight().values() )
		{	
			Vector3f oldVect=null;
			Spatial s = listPlanes.get(pos);
			if(!SpheresNode.hasChild(s))
			{
				chLat = r.getLatitude();
				chLong = r.getLongitude();
				oldVect = geoCoordTo3dCoord(chLat,chLong);
				Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
				mat.getAdditionalRenderState().setLineWidth(4.0f);
				mat.setColor("Color", tabColor[randBetween(0, 11)]);
				s.setLocalScale(0.03f);
				s.setMaterial(mat);
				s.move(oldVect);
				s.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
				s.rotate((float)Math.PI/2,0,0);
				s.rotate(0,0,r.getDirection());
				//rotation axe des z puis move up axe des y 
				//Vector3f w = new Vector3f(0,0,3);
				//Vector3f up = s.getLocalRotation().mult(new Vector3f(0,0,-1.0f));
				//s.move(up);
				//s.rotate(0,0,r.getDirection());
			}
			else
			{
				SpheresNode.detachChild(s); //on enleve avion
				chLat = r.getLatitude();
				chLong = r.getLongitude();
				Vector3f newVect = geoCoordTo3dCoord(chLat,chLong);	
				oldVect = newVect;				
				//planeSpatial.setLocalTranslation(newVect);
			}
			SpheresNode.attachChild(s);	
			pos++;
		}	
		
		//Afficher texte zine 3D
		/*
		 * guiFont = assetManager.loadFRont("Interface/Fonts/Default.fnt");
		 * BitmapText hudText = new BitmapText(guiFont, false);
		 * hudText.setColor(ColorRGBA.Blue);
		 * hudText.setText(You can write any string here");
		 * hudText.setLocalTranslation(300, hudText.getLineHeight(),0);
		 * myNode.attachChild(hudText);
		 * 
		 *Altitude
		 *Vector3f up = plane.getLocalRotation().mult(new Vector3f(0,-1,0f,0));
		 *plane(move(up);
		 */
		
		
		
		//Frame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
		//frame.setJMenuBar(menubar);
		
		
		/*
		Vector3f oldVect = new Vector3f(5,0,0);
		Vector3f newVect = new Vector3f(-1,1,0);
		Line line = new Line(oldVect, newVect);
		Geometry lineGeo = new Geometry("lineGeo", line);
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(4.0f);
		mat.setColor("Color", ColorRGBA.Red);
		lineGeo.setMaterial(mat);
		lineGeo.setLocalTranslation(0.0f,0.0f,6.0f);
		LinesNode.setMaterial(mat);
		LinesNode.attachChild(lineGeo);*/
		rootNode.attachChild(LinesNode);
		rootNode.attachChild(SpheresNode);
		
		
		
		/*
		Node LinesNode = new Node("LinesNode");
		Vector3f oldVect = new Vector3f(1,0,0);
		for(int i=0;i<100;i++)
		{// ...
			float t=i/5.0f;
			Vector3f newVect = new Vector3f(FastMath.cos(t),t/5.0f,FastMath.sin(t));
			Line line = new Line(oldVect, newVect);
			Geometry lineGeo = new Geometry("lineGeo", line);
			Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
			mat.getAdditionalRenderState().setLineWidth(4.0f);
			mat.setColor("Color", ColorRGBA.Red);
			lineGeo.setMaterial(mat);
			lineGeo.setLocalTranslation(0.0f,0.0f,8.0f);
			LinesNode.setMaterial(mat);
			LinesNode.attachChild(lineGeo);
			rootNode.attachChild(LinesNode);
			oldVect = newVect;
		}*/
	}/*
	@Override
	public void simpleUpdate(float tpf)
	{
		Timer t = getTimer();
		System.out.println(String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(t),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));  );
		updateEarth();
	}*/
	
	private static Vector3f geoCoordTo3dCoord(float lat, float lon)
	{
		float lat_cor = lat + TEXTURE_LAT_OFFSET;
		float lon_cor = lon + TEXTURE_LON_OFFSET;
		return new Vector3f(- FastMath.sin(lon_cor * FastMath.DEG_TO_RAD)
							* FastMath.cos(lat_cor * FastMath.DEG_TO_RAD),
							  FastMath.sin(lat_cor * FastMath.DEG_TO_RAD),
							- FastMath.cos(lon_cor * FastMath.DEG_TO_RAD)
							* FastMath.cos(lat_cor * FastMath.DEG_TO_RAD));
	}
	private void displayTown(float latitude, float longitude)
	{
		Sphere sphere = new Sphere(16,8,0.002f);
		Geometry sphereGeo = new Geometry("lineGeo", sphere);
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(4.0f);
		mat.setColor("Color", ColorRGBA.Red);
		sphereGeo.setMaterial(mat);
		Vector3f v = geoCoordTo3dCoord(latitude,longitude);
		sphereGeo.setLocalTranslation(v);
		SpheresNode.setMaterial(mat);
		SpheresNode.attachChild(sphereGeo);	
	}
	public static boolean updatePositions()
	{
		try {
			return RealTimeFlight.affichagePositionsAvions();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Integer randBetween(int start, int end) 
    {
    	return start + (int)Math.round(Math.random() * (end - start));
	}
	public void updateEarth()
	{
		updatePositions();
		int pos = 0;
		RealTimeFlight.enableRead();
		for( RealTimeFlight r : MainSystem.getRealTimeFlight().values() )
		{	
			Vector3f oldVect=null;
			Spatial s = listPlanes.get(pos);
			if(!SpheresNode.hasChild(s))
			{
				chLat = r.getLatitude();
				chLong = r.getLongitude();
				oldVect = geoCoordTo3dCoord(chLat,chLong);
				Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
				mat.getAdditionalRenderState().setLineWidth(4.0f);
				mat.setColor("Color", tabColor[randBetween(0, 11)]);
				s.setLocalScale(0.03f);
				s.setMaterial(mat);
				s.move(oldVect);
				s.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
				s.rotate((float)Math.PI/2,0,r.getDirection());
				//rotation axe des z puis move up axe des y 
				//Vector3f w = new Vector3f(0,0,3);
				//Vector3f up = s.getLocalRotation().mult(new Vector3f(0,0,-1.0f));
				//s.move(up);
				//s.rotate(0,0,r.getDirection());
			}
			else
			{
				SpheresNode.detachChild(s); //on enleve avion
				chLat = r.getLatitude();
				chLong = r.getLongitude();
				Vector3f newVect = geoCoordTo3dCoord(chLat,chLong);	
				oldVect = newVect;				
				//planeSpatial.setLocalTranslation(newVect);
			}
			SpheresNode.attachChild(s);	
			pos++;
		}	
	}

}
