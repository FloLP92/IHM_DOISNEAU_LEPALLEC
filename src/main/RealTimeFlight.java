package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.jme3.asset.plugins.ZipLocator;
import com.jme3.scene.Spatial;

public class RealTimeFlight 
{
	private Date currentTime;
	private String idVol;
	private float latitude;
	private float longitude;
	private float altitude;
	private float vitesse;
	private float direction;
	private Date lastUpdatePosition;
	private Date lastUpdateVitesse;
	private float vitesseVert;
	private String codeICAO;
	private boolean positionSol;
	private static BufferedReader bufRead;
	private boolean update = false;
	private static Date lastDate; //garde point de la liste ou commencer
	private static boolean boolDate = false; //verifie si bon point de la liste
	private Spatial plane; //figure correspondante
	
	public RealTimeFlight(Date chCurrent, String chId,float chAltitude, float chLat,
			float chLong, float chVitesse, float chDir, Date chLastPosition,Date chLastVitesse,
			float chVitesseVert, String chCode, boolean chPos)
	{
		currentTime = chCurrent;
		idVol = chId;
		latitude = chLat;
		longitude = chLong;
		altitude = chAltitude;
		vitesse = chVitesse;
		direction = chDir;
		lastUpdatePosition = chLastPosition;
		lastUpdateVitesse = chLastVitesse;
		vitesseVert = chVitesseVert;
		codeICAO = chCode;
		positionSol = chPos;
	}
	
	public static boolean affichagePositionsAvions() throws IOException{
		try {
			FileReader file = new FileReader("ressources/realtime_flights.dat");
			bufRead = new BufferedReader(file);
			String line = bufRead.readLine();
			int compteur =1;
			while(line != null){ //Tant qu'on a des lignes a lire dans le fichier
				String[] array = line.split(",");
				String[] parts = array[0].split("///");
				//System.out.println("Avion "+compteur+" :\n");
				
				//time part
				Timestamp t1 = new Timestamp(Long.parseLong(parts[0])*1);
				Date time = new Date(t1.getTime());
				//on continue a lire
				if( (time != lastDate)&&(!boolDate) )
				{
					boolDate = true;
					continue;
				}
				if(parts[2].equals("null")){
					parts[2] = "0";
				}
				if(parts[3].equals("null")){
					parts[3] = "0";
				}
				if(parts[4].equals("null")){
					parts[4] = "0";
				}
				long b;
				
				if(parts[7] !=null){
					try{
					Float f  = Float.parseFloat(parts[7]);
					b = f.longValue();
					}catch(Exception ec){
						b=0;
					}
				}
				else{
					b = 0;
				}
				Timestamp t = new Timestamp(b*1000);
				Date date = new Date(t.getTime());
				//System.out.println("Date de la dernière MaJ de la position : "+date+"\n");
				
				if(parts[8] !=null){
					try{
					Float f  = Float.parseFloat(parts[8]);
					b = f.longValue();
					}catch(Exception ec){
						b=0;
					}
				}
				else{
					b = 0;
				}
				t = new Timestamp(b*1000);
				Date date2 = new Date(t.getTime());
				/*
				System.out.println("Date de la dernière MaJ de la vitesse : "+date2+"\n");

				System.out.println("Vitesse verticale (en m/s) : "+parts[9]+"\n");
				System.out.println("Code ICAO du pays : "+parts[10]+"\n");
				System.out.println("Au sol : "+parts[11]+"\n");*/
				Boolean bool;
				if(parts[11].equals("false")){
					bool = false;
				}
				else{
					bool = true;
				}
				line = bufRead.readLine();
				compteur++;
				
				//entrée existante, est-elle a jour?
				if(MainSystem.getRealTimeFlight().containsKey(parts[1]))
				{
					//est il déja actualisé?
					if(!MainSystem.getRealTimeFlight().get(parts[1]).getUpdate())
					{
						RealTimeFlight chR;
						if(!parts[9].equals("null"))
						{
							chR = new RealTimeFlight(time,parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),Float.parseFloat(parts[4]),Float.parseFloat(parts[5]),Float.parseFloat(parts[6]),date,date2,Float.parseFloat(parts[9]),parts[10],bool);
							MainSystem.updateRealTimeFlight(parts[1], chR);				
						}
						else{
							Float f = new Float(0.0);
							chR = new RealTimeFlight(time,parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),Float.parseFloat(parts[4]),Float.parseFloat(parts[5]),Float.parseFloat(parts[6]),date,date2,f,parts[10],bool);
							MainSystem.updateRealTimeFlight(parts[1], chR);
						}
						chR.setUpdate();
					}
					else //deja actualisé
					{
						lastDate = time;
						MainSystem.getRealTimeFlight().get(parts[1])
						.setUpdate();
						break;
					}
				}
				//nouvelle entrée, on ajoute directe
				else
				{
					if(!parts[9].equals("null"))
					{
						//System.out.println("bb"+parts[1]);
						RealTimeFlight chR = new RealTimeFlight(time,parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),Float.parseFloat(parts[4]),Float.parseFloat(parts[5]),Float.parseFloat(parts[6]),date,date2,Float.parseFloat(parts[9]),parts[10],bool);
						MainSystem.updateRealTimeFlight(parts[1], chR);				
					}
					else{
						//System.out.println("bb"+parts[1]);
						Float f = new Float(0.0);
						RealTimeFlight chR = new RealTimeFlight(time,parts[1],Float.parseFloat(parts[2]),Float.parseFloat(parts[3]),Float.parseFloat(parts[4]),Float.parseFloat(parts[5]),Float.parseFloat(parts[6]),date,date2,f,parts[10],bool);
						MainSystem.updateRealTimeFlight(parts[1], chR);
						}
				}
				
			}
			if(line==null)
			{
				return true;
			}
			else
			{
				return false;
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public float getLatitude()
	{
		return latitude;
	}
	public float getLongitude()
	{
		return longitude;
	}
	public float getAltitude()
	{
		return altitude;
	}
	public boolean getUpdate()
	{
		return update;
	}
	public void setUpdate()
	{
		update = true;
	}
	public Spatial getSpatial()
	{
		return plane;
	}
	public void getSpatial(Spatial chSpatial)
	{
		plane = chSpatial;
	}
}
