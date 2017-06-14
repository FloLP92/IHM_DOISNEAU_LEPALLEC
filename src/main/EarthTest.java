package main;

import java.util.ArrayList;
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

	@Override
	public void simpleInitApp() 
	{
		LinesNode = new Node("LinesNode");
		SpheresNode = new Node("SpheresNode");
		assetManager.registerLocator("earth.zip", ZipLocator.class);
		Spatial earth_geom = assetManager.loadModel("earth/Sphere.mesh.xml");
		earth_node = new Node("earth");
		earth_node.attachChild(earth_geom);
		//earth_node.setLocalScale(5.0f);
		rootNode.attachChild(earth_node);
		for (Airport value : MainSystem.getListAirports().values()) 
		{
			displayTown(value.getLatitude(),value.getLongitude());  
		}
		int compteur = 0;
		for(Flight f : MainSystem.getListFlight().values())
		{
			Airport airportDepart = f.getAirportDepart();
			
			
			Material matPlane = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
			matPlane.getAdditionalRenderState().setLineWidth(4.0f);
			matPlane.setColor("Color", ColorRGBA.Red);
			
			
			if(MainSystem.getRealTimeFlight().containsKey(f.getId()+"   "))
			{
				ArrayList<RealTimeFlight> rf = MainSystem.getRealTimeFlight().get(f.getId()+"   ");
				float chLat,chLong;
				Vector3f oldVect=null;
				for(int i=1;i< rf.size();i++)
				{
					if(i == 1)
					{
						chLat = rf.get(0).getLatitude();
						chLong = rf.get(0).getLongitude();
						oldVect = geoCoordTo3dCoord(chLat,chLong);
						MainSystem.addVector(f.getId()+"   ",oldVect);
					}
					chLat = rf.get(i).getLatitude();
					chLong = rf.get(i).getLongitude();
					Vector3f newVect = geoCoordTo3dCoord(chLat,chLong);
					Line line = new Line(oldVect, newVect);
					Geometry lineGeo = new Geometry("lineGeo", line);
					Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
					mat.getAdditionalRenderState().setLineWidth(4.0f);
					mat.setColor("Color", ColorRGBA.Red);
					lineGeo.setMaterial(mat);
					//float altitude = rf.get(i).getAltitude();
					//lineGeo.setLocalTranslation(0.0f,0.0f,altitude/80);
					lineGeo.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
					
					
					
					/*
					objet orienté pariel que le monde
					lookAt pour s aligner regarder l aterre
					si on fais avancer reculer que altitude qui va changer*/
					
					LinesNode.setMaterial(mat);
					LinesNode.attachChild(lineGeo);
					rootNode.attachChild(LinesNode);
					oldVect = newVect;
					MainSystem.addVector(f.getId()+"   ",newVect);
					Spatial planeSpatial = assetManager.loadModel("earth/plane.obj");
					planeSpatial.setMaterial(matPlane);
					planeSpatial.move(newVect);
					//planeSpatial.setLocalTranslation(newVect);
					planeSpatial.lookAt(new Vector3f(0,0,0), new Vector3f(0,1,0));
					Vector3f w = new Vector3f(0,0,3);
					planeSpatial.setLocalTranslation(planeSpatial
							.getLocalTranslation().add(w));
					planeSpatial.rotate(0,(float)Math.PI/2,0);
					//planeSpatial.setLocalTranslation(0,0,+1);
					planeSpatial.setLocalScale(0.03f);
					SpheresNode.attachChild(planeSpatial);					
				}
			}
			compteur++;
		}
		
		
		final JMenuBar menubar = new JMenuBar();
		final JMenu objectsMenu = new JMenu("File");
		final JMenu helpMenu = new JMenu("Help");
		menubar.add(objectsMenu);
		menubar.add(helpMenu);
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
}
