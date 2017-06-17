package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Airport 
{
	private String ville;
	private Pays pays;
	private String idIATA;
	private float latitude;
	private float longitude;
	private ArrayList<Flight> listDepart;
	private ArrayList<Flight> listDestination;
	
	public Airport(String chVille, Pays chPays, String chId,
			float chLatitude, float chLongitude)
	{
		ville = chVille;
		pays = chPays;
		idIATA = chId;
		latitude = chLatitude;
		longitude = chLongitude;
		listDepart = new ArrayList<Flight>();
		listDestination = new ArrayList<Flight>();
		
	}
	public float getLatitude()
	{
		return latitude;
	}
	public float getLongitude()
	{
		return longitude;
	}
	public void ajoutVolDepart(Flight f)
	{
		listDepart.add(f);
	}
	public void ajoutVolDest(Flight f)
	{
		listDestination.add(f);
	}
	public String getVille()
	{
		return ville;
	}
	public Pays getPays()
	{
		return pays;
	}
	public ArrayList<Flight> getListDepart()
	{
		return listDepart;
	}
	public ArrayList<Flight> getListDestination()
	{
		return listDestination;
	}

	public String toString(){
		return idIATA+" ("+pays.toString()+" - "+ville+" )";
	}
}
