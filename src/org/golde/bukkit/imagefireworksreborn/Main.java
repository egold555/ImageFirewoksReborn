package org.golde.bukkit.imagefireworksreborn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Main extends JavaPlugin implements Listener {
	public static Main plugin;
	public File dataFolder;
	private String commandUse = ChatColor.RED + "Command Use: /imgfws <give:launch> <player> <firework>";
	
	private static HashMap<String, String> fireworkList = new HashMap();

	public void onEnable()
	{
		plugin = this;
		getLogger().info("ImageFireworksReborn is starting...");
		dataFolder = getDataFolder();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		File imageDir = new File(dataFolder + File.separator + "images");
		if (!imageDir.exists()) {
			imageDir.mkdirs();
		}
		File fwsDir = new File(dataFolder + File.separator + "fireworks");
		if (!fwsDir.exists()) {
			fwsDir.mkdirs();
		}
		File fireworksFile = new File(dataFolder + File.separator + "fireworks" + File.separator + "demo.yml");
		FileConfiguration fireworks = YamlConfiguration.loadConfiguration(fireworksFile);
		if (!fireworksFile.exists())
		{
			fireworks.set("Name", "Demo");
			fireworks.set("Image", "imgfw.png");
			fireworks.set("Color.UseFullColor", true);
			fireworks.set("Color.R", 255);
			fireworks.set("Color.G", 0);
			fireworks.set("Color.B", 255);
			try
			{
				fireworks.save(fireworksFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		saveResource("images" + File.separator + "imgfw.png", true);
		updateFireworkList();
		getLogger().info("ImageFireworksReborn has started!");
		getLogger().info("Thanks to Inventivetalent for the Particle and Reflection API!");
	}
	
	private String getServerVersion() {
		return getServer().getClass().getName().split("\\.")[3];
	}
	
	void playSound(final Location center) {
		if(getServerVersion().startsWith("v1_8") || getServerVersion().startsWith("v1_7")) {
			center.getWorld().playSound(center, Sound.valueOf("FIREWORK_BLAST"), 3.0F, 1.0F);
		}else {
			center.getWorld().playSound(center, Sound.valueOf("ENTITY_FIREWORK_BLAST"), 3.0F, 1.0F);
		}
	}

	public void onDisable() {}

	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
			if (sender.hasPermission("imagefireworks.use"))
			{
				if (args.length > 0)
				{
					String option = args[0];
					if (option.equalsIgnoreCase("give"))
					{
						if (args.length <= 1) {
							sender.sendMessage(commandUse);
							return true;
						}
						
						String pName = args[1];
						String fwName = "";
						for (int i = 2; i < args.length; i++) {
							fwName = fwName + args[i] + " ";
						}
						fwName = fwName.trim();
						if (Bukkit.getPlayer(pName) != null)
						{
							Player target = Bukkit.getPlayer(pName);
							if (fireworkList.containsKey(fwName.toLowerCase()))
							{
								File fwFile = new File(dataFolder + File.separator + "fireworks" + File.separator + (String)fireworkList.get(fwName.toLowerCase()));
								FileConfiguration fw = YamlConfiguration.loadConfiguration(fwFile);

								ItemStack iS = new ItemStack(Material.FIREWORK, 1);
								ItemMeta iM = iS.getItemMeta();
								iM.setDisplayName(fw.getString("Name"));
								
								ArrayList<String> iL = new ArrayList();
								iL.add(ChatColor.RED + "Image Firework");
								iM.setLore(iL);
								iS.setItemMeta(iM);

								target.getInventory().addItem(new ItemStack[] { iS });
								target.updateInventory();
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Not a valid firework name: " + fwName);
								sender.sendMessage("Active Fireworks: " + fireworkList.keySet().toString().toLowerCase());
							}
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "You must enter a valid player name...");
						}
					}
					else if (option.equalsIgnoreCase("launch"))
					{
						if (args.length <= 1) {
							sender.sendMessage(commandUse);
							return true;
						}
						
						String pName = args[1];
						String fwName = "";
						for (int i = 2; i < args.length; i++) {
							fwName = fwName + args[i] + " ";
						}
						fwName = fwName.trim();
						if (Bukkit.getPlayer(pName) != null)
						{
							Player target = Bukkit.getPlayer(pName);
							if (fireworkList.containsKey(fwName.toLowerCase()))
							{
								CustomFirework cfw = new CustomFirework((String)fireworkList.get(fwName.toLowerCase()));
								cfw.useFirework(target.getLocation());
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "Not a valid firework name: " + fwName.toLowerCase());
								sender.sendMessage("Active Fireworks: " + fireworkList.keySet().toString().toLowerCase());
							}
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "You must enter a valid player name...");
						}
					}
					else if (option.equalsIgnoreCase("reload"))
					{
						updateFireworkList();
						sender.sendMessage(ChatColor.GREEN + "Fireworks List reloaded...");
					}
					else
					{
						sender.sendMessage(commandUse);
					}
				}
				else
				{
					sender.sendMessage(commandUse);
				}
				return true;
			}
			sender.sendMessage("You don't have permission to use this command.");
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		List<String> l = new ArrayList<String>(); 
		if(command.getName().equalsIgnoreCase("imagefireworks")){  
			
			if(args.length == 1) {
				l.add("give");
				l.add("launch");
			}
			
			if(args.length == 2) {
				for(Player p:Bukkit.getOnlinePlayers()) {
					l.add(p.getName());
				}
			}
			
			if(args.length == 3) {
				l.addAll(fireworkList.keySet());
			}
			
		}
		return l;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event)
	{
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
				(event.getItem() != null) && (event.getItem().hasItemMeta()) && 
				(((String)event.getItem().getItemMeta().getLore().get(0)).equals(ChatColor.RED + "Image Firework")))
		{
			event.setCancelled(true);
			if (fireworkList.containsKey(event.getItem().getItemMeta().getDisplayName().toLowerCase()))
			{
				CustomFirework cfw = new CustomFirework((String)fireworkList.get(event.getItem().getItemMeta().getDisplayName().toLowerCase()));
				Location loc = event.getClickedBlock().getLocation();
				loc.setY(loc.getY() + 1.0D);
				loc.setYaw(event.getPlayer().getLocation().getYaw());
				cfw.useFirework(loc);
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					event.getPlayer().getInventory().removeItem(new ItemStack[] { event.getPlayer().getItemInHand() });
				}
			}
		}
	}

	@EventHandler
	public void onDispenseFirework(BlockDispenseEvent event)
	{
		if ((event.getItem() != null) && (event.getItem().hasItemMeta()) && 
				(((String)event.getItem().getItemMeta().getLore().get(0)).equals(ChatColor.RED + "Image Firework")))
		{
			event.setCancelled(true);
			if (fireworkList.containsKey(event.getItem().getItemMeta().getDisplayName().toLowerCase()))
			{
				org.bukkit.material.Dispenser dispenserMaterial = (org.bukkit.material.Dispenser) event.getBlock().getState().getData();
				org.bukkit.block.Dispenser dispenserBlock = (org.bukkit.block.Dispenser) event.getBlock().getState();

				dispenserBlock.getInventory().removeItem(new ItemStack[] { event.getItem() });
				CustomFirework cfw = new CustomFirework((String)fireworkList.get(event.getItem().getItemMeta().getDisplayName().toLowerCase()));
				Location loc = event.getBlock().getLocation();
				loc.setX(loc.getX() + 0.5D);
				loc.setY(loc.getY() + 0.5D);
				loc.setZ(loc.getZ() + 0.5D);

				Vector offset;
				switch(dispenserMaterial.getFacing()){
				case NORTH: loc.setYaw(180); offset = new Vector(0, 0, -1); break;
				case SOUTH: loc.setYaw(0);   offset = new Vector(0, 0, 1);  break;
				case EAST: loc.setYaw(-90);  offset = new Vector(1, 0, 0);  break;
				case WEST: loc.setYaw(90);   offset = new Vector(-1, 0, 0); break;
				case UP: loc.setYaw(0);      offset = new Vector(0, 1, 0);  break;
				case DOWN: loc.setYaw(0);    offset = new Vector(0, -1, 0); break;
				default: loc.setYaw(0);      offset = new Vector(0, 0, 0);  break;
				}

				loc.add(offset);
				cfw.useFirework(loc);
			}
		}
	}

	private void updateFireworkList()
	{
		File[] files = new File(Main.plugin.dataFolder + File.separator + "fireworks").listFiles();
		File[] arrayOfFile1 = files;int j = files.length;
		for (int i = 0; i < j; i++)
		{
			File file = arrayOfFile1[i];
			if (file.isFile())
			{
				File fwFile = new File(Main.plugin.dataFolder + File.separator + "fireworks" + File.separator + file.getName());
				FileConfiguration fw = YamlConfiguration.loadConfiguration(fwFile);
				fireworkList.put(fw.getString("Name").toLowerCase(), file.getName());
			}
		}
	}

}
