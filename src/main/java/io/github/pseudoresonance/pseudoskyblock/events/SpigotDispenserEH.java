package io.github.pseudoresonance.pseudoskyblock.events;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.pseudoresonance.pseudoskyblock.WorldManager;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.CustomType;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.DataHolder;

public class SpigotDispenserEH implements Listener {
	
	private static final Random rand = new Random();

	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent e) {
		if (e.getOldCurrent() == 0 && e.getNewCurrent() > 0) {
			Location original = e.getBlock().getLocation();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						if (Math.abs(dx + dz) == 1) {
							Location loc = original.clone().add(dx, dy, dz);
							Block b = loc.getBlock();
							if (b.getType() == Material.DISPENSER) {
								DataHolder dh = WorldManager.getDataHolder(loc);
								if (dh != null) {
									if (dh.type == CustomType.AUTOBUILDER) {
										Block front = b.getRelative(dh.facing);
										if (front.isEmpty() || front.isLiquid()) {
											Dispenser dispenser = (Dispenser) b.getState();
											Inventory inv = dispenser.getInventory();
											for (int i : IntStream.range(0, 9).boxed().sorted((i1, i2) -> {
												return i1 - rand.nextInt();
											}).collect(Collectors.toCollection(ArrayList::new))) {
												ItemStack item = inv.getItem(i);
												if (item != null && item.getType() != Material.AIR) {
													if (item.getType().isBlock()) {
														front.setType(item.getType());
														dispenser.update(true);
														int amount = item.getAmount();
														if (amount <= 1)
															inv.setItem(i, new ItemStack(Material.AIR));
														else {
															item.setAmount(--amount);
															inv.setItem(i, item);
														}
													}
													return;
												}
											}
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
