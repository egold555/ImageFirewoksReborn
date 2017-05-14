package org.golde.bukkit.imagefireworksreborn.api;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.bukkit.Location;
import org.golde.bukkit.imagefireworksreborn.CustomFirework;

public class ImageFireworksAPI {
	
	/**
	 * Launches a ImageFirework using the color of each pixel.
	 * @param loc - Location of launched firework
	 * @param image - Image to be displayed in particles
	 */
	public static void launchFullColorFirework(Location loc, BufferedImage image) {
		CustomFirework cfw = new CustomFirework(image, -1, -1, -1);
		cfw.useFirework(loc);
	}
	
	/**
	 * Launches a ImageFirework using the color the RGB params.
	 * @param loc - Location of launched firework
	 * @param image - launchFullColorFirework
	 * @param color - Pixel color
	 */
	public static void launchSingleColorFirework(Location loc, BufferedImage image, Color color) {
		launchSingleColorFirework(loc, image, color.getRed(), color.getGreen(), color.getBlue());
	}
	
	/**
	 * Launches a ImageFirework using the color the RGB params.
	 * @param loc - Location of launched firework
	 * @param image - launchFullColorFirework
	 * @param red - Pixel red color
	 * @param green - Pixel green color
	 * @param blue - Pixel blue color
	 */
	public static void launchSingleColorFirework(Location loc, BufferedImage image, int red, int green, int blue) {
		CustomFirework cfw = new CustomFirework(image, red, green, blue);
		cfw.useFirework(loc);
	}
	
}
