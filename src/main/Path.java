package main;

import java.util.ArrayList;

public class Path 
{
	private ArrayList<RealTimeFlight> listPos;
	
	public Path()
	{
		listPos = new ArrayList<RealTimeFlight>();
	}
	public void addPos(RealTimeFlight r)
	{
		listPos.add(r);
	}
	public ArrayList<RealTimeFlight> getListPos()
	{
		return listPos;
	}
}
