package io.github.pseudoresonance.pseudoskyblock;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Snow;
import org.bukkit.inventory.ItemStack;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ConfigFile;
import io.github.pseudoresonance.pseudoapi.bukkit.utils.ItemUtils;

public class WorldManager {

	private static ConcurrentHashMap<String, DataHolder> dispensers = new ConcurrentHashMap<String, DataHolder>();
	private static AtomicBoolean updated = new AtomicBoolean(false);
	private static ItemStack pick = new ItemStack(Material.STONE_PICKAXE, 1);
	private static ItemStack shovel = new ItemStack(Material.STONE_SHOVEL, 1);
	private static ItemStack axe = new ItemStack(Material.STONE_AXE, 1);
	private static File dataFile = new File(PseudoSkyblock.plugin.getDataFolder(), "data.yml");
	private static ConfigFile data = null;

	public static void addDispenser(Location loc, CustomType type) {
		try {
			Block b = loc.getBlock();
			Directional dir = (Directional) b.getBlockData();
			DataHolder dh = new DataHolder(loc, type, dir.getFacing());
			String locStr = serializeLocation(loc);
			dispensers.put(locStr, dh);
			data.getConfig().set(locStr, type.toString() + "," + dir.getFacing().toString() + "," + dh.initTime);
			updated.set(true);
		} catch (ClassCastException e) {
		}
	}

	public static void removeDispenser(Location loc) {
		dispensers.remove(serializeLocation(loc));
		data.getConfig().set(loc.getWorld().getUID().toString() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), null);
		updated.set(true);
	}

	public static void load() {
		try {
			dataFile.createNewFile();
		} catch (IOException e) {
			PseudoSkyblock.plugin.getChat().sendConsolePluginError(Errors.CUSTOM, "Unable to save data to: " + dataFile.getAbsolutePath() + " Please check permissions!");
			e.printStackTrace();
		}
		data = new ConfigFile(dataFile.getParentFile(), dataFile.getName(), PseudoSkyblock.plugin);
		Bukkit.getScheduler().runTaskAsynchronously(PseudoSkyblock.plugin, () -> {
			for (String key : data.getConfig().getConfigurationSection("").getKeys(false)) {
				Location loc = deserializeLocation(key);
				String[] dataSplit = data.getConfig().getString(key).split(",");
				dispensers.put(serializeLocation(loc), new DataHolder(loc, CustomType.valueOf(dataSplit[0]), BlockFace.valueOf(dataSplit[1]), Long.valueOf(dataSplit[2])));
			}
		});
	}

	public static void save() {
		if (updated.get()) {
			Bukkit.getScheduler().runTaskAsynchronously(PseudoSkyblock.plugin, () -> {
				data.save();
			});
		}
	}

	public static void tick() {
		for (Entry<String, DataHolder> entry : dispensers.entrySet()) {
			DataHolder dh = entry.getValue();
			Location loc = dh.location;
			if (dh.type == CustomType.AUTOMINER) {
				if (++dh.time >= Config.autoMinerSpeed) {
					dh.time = 0;
					if (loc.getChunk().isLoaded()) {
						Block front = loc.getBlock().getRelative(dh.facing);
						if (!loc.getBlock().isBlockIndirectlyPowered() && !front.isLiquid() && ItemUtils.getCanToolBreakBlock(pick, front))
							front.breakNaturally(pick);
					}
				}
			} else if (dh.type == CustomType.AUTODIGGER) {
				if (++dh.time >= Config.autoMinerSpeed) {
					dh.time = 0;
					if (loc.getChunk().isLoaded()) {
						Block front = loc.getBlock().getRelative(dh.facing);
						if (!loc.getBlock().isBlockIndirectlyPowered() && !front.isLiquid() && ItemUtils.getCanToolBreakBlock(shovel, front)) {
							if (front.getType() != Material.SNOW)
								front.breakNaturally(shovel);
							else {
								front.getWorld().dropItemNaturally(front.getLocation(), new ItemStack(Material.SNOWBALL, ((Snow) front.getBlockData()).getLayers()));
								front.breakNaturally();
							}
						}
					}
				}
			} else if (dh.type == CustomType.AUTOCUTTER) {
				if (++dh.time >= Config.autoMinerSpeed) {
					dh.time = 0;
					if (loc.getChunk().isLoaded()) {
						Block front = loc.getBlock().getRelative(dh.facing);
						if (!loc.getBlock().isBlockIndirectlyPowered() && !front.isLiquid() && ItemUtils.getCanToolBreakBlock(axe, front))
							front.breakNaturally(axe);
					}
				}
			}
		}
	}
	
	public static DataHolder getDataHolder(Location loc) {
		return dispensers.get(serializeLocation(loc));
	}
	
	public static DataHolder getDataHolder(String locStr) {
		return dispensers.get(locStr);
	}
	
	private static String serializeLocation(Location loc) {
		return loc.getWorld().getUID().toString() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	private static Location deserializeLocation(String str) {
		String[] split = str.split(",");
		return new Location(Bukkit.getWorld(UUID.fromString(split[0])), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
	}

	public static class DataHolder {

		public final Location location;
		public final CustomType type;
		public final BlockFace facing;
		public final long initTime;
		public long time = 0;

		public DataHolder(Location location, CustomType type, BlockFace facing) {
			this.location = location;
			this.type = type;
			this.facing = facing;
			initTime = System.currentTimeMillis();
			time = 0;
		}

		public DataHolder(Location location, CustomType type, BlockFace facing, long initTime) {
			this.location = location;
			this.type = type;
			this.facing = facing;
			this.initTime = initTime;
			time = (System.currentTimeMillis() - initTime) % Config.autoMinerSpeed;
		}
	}

	public static enum CustomType {
		AUTOMINER("Auto Miner"), AUTODIGGER("Auto Digger"), AUTOCUTTER("Auto Cutter"), AUTOBUILDER("Auto Builder");

		private final String itemName;

		private CustomType(String itemName) {
			this.itemName = itemName;
		}

		public String getItemName() {
			return ChatColor.COLOR_CHAR + "5" + itemName;
		}
	}

}
