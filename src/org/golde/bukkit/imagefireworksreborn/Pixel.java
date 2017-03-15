package org.golde.bukkit.imagefireworksreborn;

import java.awt.Color;

import org.bukkit.util.Vector;

public class Pixel {

	private final Vector loc;
	private final int red;
	private final int green;
	private final int blue;
	
	public Pixel(Vector loc, int r, int g, int b){
		this.loc = loc;
		this.red = r;
		this.green = g;
		this.blue = b;
	}
	
	public Vector getLoc() {return loc;}
	public Color getColor(){
		return new Color(red, green, blue);
	}
}
