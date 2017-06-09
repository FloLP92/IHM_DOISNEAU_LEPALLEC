package main;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class CubesTest extends SimpleApplication 
{
	private Geometry geom;
	private Geometry geom2;
	private Geometry geom3;
	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
		Box b = new Box(1, 1, 1);
		geom = new Geometry("Box",b);
		geom2 = new Geometry("Box",b);
		geom3 = new Geometry("Box",b);
		
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		Material mat2 = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setColor("Color", ColorRGBA.Green);
		Material mat3 = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat3.setColor("Color", ColorRGBA.Red);
		
		geom.setMaterial(mat);
		geom2.setMaterial(mat2);
		geom3.setMaterial(mat3);
		rootNode.attachChild(geom);
		rootNode.attachChild(geom2);
		rootNode.attachChild(geom3);
		geom2.setLocalTranslation(0.0f,3.0f,0.0f);
		geom3.setLocalTranslation(3.0f,0.0f,0.0f);
		
		flyCam.setEnabled(false);
		ChaseCamera chaseCam = new ChaseCamera(cam,geom,inputManager);
		chaseCam.setDragToRotate(true);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setRotationSpeed(10.0f);;
		chaseCam.setMinVerticalRotation((float)-(Math.PI/2 - 0.0001f));
		chaseCam.setMaxVerticalRotation((float)-Math.PI/2);
		chaseCam.setMaxDistance(30.0f);
			
		
	}
	@Override
	public void simpleUpdate(float tpf)
	{
		rotate(geom,3.0f,0.0f,0.0f);
		rotate(geom2,0.0f,3.0f,0.0f);
		rotate(geom3,0.0f,0.0f,3.0f);
	}
	public void rotate(Spatial geom,float xAngle, float yAngle, float zAngle)
	{
		geom.rotate(xAngle,yAngle,zAngle);		
	}

	public static void main(String[] args) 
	{
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1200, 800);
		settings.setSamples(8);
		settings.setFrameRate(60);
		settings.setVSync(true);
		
		CubesTest app = new CubesTest();
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setDisplayStatView(false);
		app.setDisplayFps(false);
		app.start();

	}

}
