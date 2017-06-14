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

public class EarthTest extends SimpleApplication {

	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;
	Node earth_node;
	Node LinesNode;
	Node SpheresNode;
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
		
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-2,-10,4));
		directionalLight.setColor(ColorRGBA.White.mult(1.7f));
		rootNode.addLight(directionalLight);
		viewPort.setBackgroundColor(new ColorRGBA(0.1f,0.1f,0.1f,1.0f));
		flyCam.setEnabled(false);
		ChaseCamera chaseCam = new ChaseCamera(cam,earth_geom,inputManager);
		chaseCam.setDragToRotate(true);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10.0f);
		chaseCam.setMinVerticalRotation((float)-(Math.PI/2 - 0.0001f));
		chaseCam.setMaxVerticalRotation((float)-Math.PI/2);
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
		
		
		HashMap<String,RealTimeFlight> rf = MainSystem.getRealTimeFlight();
		float chLat,chLong;
		/*
		objet orienté pariel que le monde
		lookAt pour s aligner regarder l aterre
		si on fais avancer reculer que altitude qui va changer*/
		while( !updatePositions() )
		{
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
					Vector3f w = new Vector3f(0,0,3);
					//s.setLocalTranslation(s.getLocalTranslation().add(w));
					s.rotate(0,(float)Math.PI/2,0);
					//planeSpatial.setLocalTranslation(0,0,+1);
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
			/*
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
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

	}
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
}
