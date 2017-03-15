package org.golde.bukkit.imagefireworksreborn;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.darkblade12.particleeffect.ParticleEffect;
import com.darkblade12.particleeffect.ParticleEffect.ParticleColor;

public class CustomFirework
{
	private String image;
	private final ParticleEffect particle = ParticleEffect.REDSTONE;
	private Color color;
	@SuppressWarnings("unused")
	private String name;
	private boolean useColor;

	public CustomFirework(String fireworkFile)
	{
		File fwFile = new File(Main.plugin.dataFolder + File.separator + "fireworks" + File.separator + fireworkFile);
		FileConfiguration fw = YamlConfiguration.loadConfiguration(fwFile);

		this.name = fw.getString("Name");
		this.image = fw.getString("Image");
		this.useColor = fw.getBoolean("Color.UseFullColor");
		this.color = new Color(fw.getInt("Color.R"), fw.getInt("Color.G"), fw.getInt("Color.B"));
	}

	public void useFirework(final Location center)
	{
		final Firework item = (Firework)center.getWorld().spawnEntity(center, EntityType.FIREWORK);
		FireworkMeta fM = item.getFireworkMeta();
		fM.setPower(2);
		item.setFireworkMeta(fM);
		final float yaw = center.getYaw();

		new BukkitRunnable()
		{
			int timer = 100;

			public void run()
			{
				if (this.timer > 0)
				{
					this.timer -= 1;
					if (item.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.AIR)
					{
						Location loc = item.getLocation().clone();
						loc.setY(loc.getY() + 4.0D);
						loc.setYaw(yaw);
						CustomFirework.this.explodeFirework(loc);
						item.remove();
						cancel();
					}
					if (item.getLocation().getY() >= center.getY() + 10.0D)
					{
						Location loc = item.getLocation().clone();
						loc.setY(loc.getY() + 4.0D);
						loc.setYaw(yaw);
						CustomFirework.this.explodeFirework(loc);
						item.remove();
						cancel();
					}
				}
				else
				{
					cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 0L, 2L);
	}

	private void explodeFirework(final Location center)
	{
		Main.plugin.playSound(center);
		final ArrayList<Pixel> firework = generateFirework(this.image);
		final int xIni = center.getBlockX();
		final int yIni = center.getBlockY();
		final int zIni = center.getBlockZ();
		final double rot = center.getYaw();
		
		new BukkitRunnable()
		{
			int times = 30;

			public void run()
			{
				if (this.times > 0)
				{
					this.times -= 1;
					for (int i = 0; i < firework.size(); i++)
					{
						Vector v = new Vector((firework.get(i).getLoc()).getX() / 5.0D, (firework.get(i).getLoc()).getY() / 5.0D, 0);
						
						// rotate v by "rot" around vertical (y) axis.
						Vector rotated = rotateVector(v, rot);
						
						center.setX(xIni + rotated.getX());
						center.setY(yIni + rotated.getY());
						center.setZ(zIni + rotated.getZ());
						
						try
						{
							ParticleColor pc = new ParticleEffect.OrdinaryColor(firework.get(i).getColor().getRed(), firework.get(i).getColor().getGreen(), firework.get(i).getColor().getBlue());
							particle.display(pc, center, 100);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					cancel();
				}
			}
		}.runTaskTimer(Main.plugin, 0L, 0L);
	}

	public static Vector rotateVector(Vector v, double angleInDegrees)
	{
		// rotate v by "rot" around vertical (y) axis.
		double angRad = (Math.PI * (angleInDegrees + 180)) / 180.0;
		double rotatedX = v.getX() * Math.cos(angRad) - v.getZ() * Math.sin(angRad);
		double rotatedZ = v.getX() * Math.sin(angRad) + v.getZ() * Math.cos(angRad);
		double rotatedY = v.getY();

		return new Vector(rotatedX, rotatedY, rotatedZ);
	}

	private ArrayList<Pixel> generateFirework(String image)
	{
		BufferedImage imagen;
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ArrayList<Pixel> result = new ArrayList();
		File imageFile = new File(Main.plugin.dataFolder + File.separator + "images" + File.separator + image);
		if (!imageFile.exists()) {
			try
			{
				throw new Exception(ChatColor.RED + "Could not find " + image);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			imagen = ImageIO.read(imageFile);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Exception: " + e.getMessage() + " - File:" + imageFile.getAbsolutePath());
		}
		if (imagen == null) {
			return result;
		}
		process(imagen, result);
		return result;
	}

	private void process(BufferedImage image, ArrayList<Pixel> result)
	{
		int offsetX = -image.getWidth() / 2;
		int offsetY = image.getHeight() / 2;
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color c = new Color(image.getRGB(x, y), true);
				if (c.getAlpha() != 0){
					if(useColor){
						result.add(new Pixel(new Vector(x + offsetX, -1 * y + offsetY, 0), c.getRed(), c.getGreen(), c.getBlue()));
					}else{
						result.add(new Pixel(new Vector(x + offsetX, -1 * y + offsetY, 0), color.getRed(), color.getGreen(), color.getBlue()));
					}
				}
			}
		}
	}
}
