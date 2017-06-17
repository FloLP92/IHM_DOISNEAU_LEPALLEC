package main;

import java.util.ArrayList;

public class Flight 
{
	private String idVol;
	private Airport airportDepart;
	private Airport airportDestination;
	private String codeCompagnie;
	private String modele;
	
	public Flight(String chId, Airport chAirportDep, Airport chAirportDest,
			String chCode, String chModele)
	{
		idVol = chId;
		airportDepart = chAirportDep;
		airportDestination = chAirportDest;
		airportDepart.ajoutVolDepart(this);
		airportDestination.ajoutVolDest(this);
		codeCompagnie = chCode;
		modele = chModele;
	}
	public String getId()
	{
		return idVol;
	}
	public Airport getAirportDepart()
	{
		return airportDepart;
	}
	public Airport getAirportDest()
	{
		return airportDestination;
	}
	public String getVilleAirportDepart()
	{
		return airportDepart.getVille();
	}
	public String getVilleAirportDest()
	{
		return airportDestination.getVille();
	}
	public Pays getPaysDepart()
	{
		return airportDepart.getPays();
	}
	public Pays getPaysDest()
	{
		return airportDestination.getPays();
	}
	public String toString()
	{
		return "Flight "+idVol+" "+airportDepart.toString()+" -> "+airportDestination.toString()
				+" "+codeCompagnie+" "+modele;
	}
}
