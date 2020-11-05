package io.github.pseudoresonance.pseudoskyblock.events;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;

import io.github.pseudoresonance.pseudoskyblock.WorldManager;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.CustomType;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.DataHolder;

public class PaperDispenserEH implements Listener {
	
	private ArrayList<DispenserInfo> data = new ArrayList<DispenserInfo>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onDispense(BlockDispenseEvent e) {
		Block b = e.getBlock();
		Location loc = b.getLocation();
		DataHolder dh = WorldManager.getDataHolder(loc);
		if (dh != null) {
			if (dh.type == CustomType.AUTOBUILDER) {
				Block front = b.getRelative(dh.facing);
				if (front.isEmpty() || front.isLiquid()) {
					ItemStack item = e.getItem();
					if (item != null && item.getType() != Material.AIR) {
						if (item.getType().isBlock()) {
							Dispenser dispenser = (Dispenser) b.getState();
							front.setType(item.getType());
							data.add(new DispenserInfo(loc, item.getType()));
							dispenser.update(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEndTick(ServerTickEndEvent e) {
		Iterator<DispenserInfo> iter = data.iterator();
		while (iter.hasNext()) {
			DispenserInfo dat = iter.next();
			Location loc = dat.loc;
			Block b = loc.getBlock();
			Dispenser dispenser = (Dispenser) b.getState();
			Inventory inv = dispenser.getInventory();
			int slot = inv.first(dat.type);
			ItemStack item = inv.getItem(slot);
			int amount = item.getAmount();
			if (amount <= 1)
				inv.setItem(slot, new ItemStack(Material.AIR));
			else {
				item.setAmount(--amount);
				inv.setItem(slot, item);
			}
			iter.remove();
		}
	}
	
	private static class DispenserInfo {
		
		public final Location loc;
		public final Material type;
		
		public DispenserInfo(Location loc, Material type) {
			this.loc = loc;
			this.type = type;
		}
		
	}

}
