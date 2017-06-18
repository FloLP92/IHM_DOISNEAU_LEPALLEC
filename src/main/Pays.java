package main;

import java.util.ArrayList;

public class Pays 
{
	//Hello
	private String nomPays;
	private ArrayList<String> listVilles;
	private ArrayList<Airport> listAirports;

	
	public Pays(String chNom)
	{
		nomPays = chNom;
		listVilles = new ArrayList<String>();
		listAirports = new ArrayList<Airport>();
	}
	public void ajouterVille(String v)
	{
		listVilles.add(v);
	}
	public void ajouterAirport(Airport a)
	{
		listAirports.add(a);
	}
	public ArrayList<Flight> getListVolsDepart()
	{
		ArrayList<Flight> allFlightPays;
		allFlightPays = new ArrayList<Flight>();
		for(Airport a : listAirports)
		{
			for(Flight f : a.getListDepart())
			{
				allFlightPays.add(f);
			}
		}
		return allFlightPays;
	}
	public ArrayList<Flight> getListVolsDestination()
	{
		ArrayList<Flight> allFlightPays;
		allFlightPays = new ArrayList<Flight>();
		for(Airport a : listAirports)
		{
			for(Flight f : a.getListDestination())
			{
				allFlightPays.add(f);
			}
		}
		return allFlightPays;
	}
	public String getNomPays() {
		return nomPays;
	}
	public void setNomPays(String nomPays) {
		this.nomPays = nomPays;
	}
	public String toString(){
		return nomPays;
	}
	public ArrayList<Airport> getListAirports()
	{
		return listAirports;
	}
}
