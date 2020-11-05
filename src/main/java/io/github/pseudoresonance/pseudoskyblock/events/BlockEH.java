package io.github.pseudoresonance.pseudoskyblock.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.pseudoresonance.pseudoskyblock.PseudoSkyblock;
import io.github.pseudoresonance.pseudoskyblock.WorldManager;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.CustomType;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.DataHolder;

public class BlockEH implements Listener {

	public static final NamespacedKey key = new NamespacedKey(PseudoSkyblock.plugin, "CustomBlock");

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemStack is = e.getItemInHand();
		if (is.getType() == Material.DISPENSER) {
			Block b = e.getBlockPlaced();
			ItemMeta im = is.getItemMeta();
			if (im != null) {
				String type = im.getPersistentDataContainer().get(key, PersistentDataType.STRING);
				if (type != null) {
					CustomType ct = CustomType.valueOf(type);
					if (ct != null) {
						TileState ts = (TileState) b.getState();
						ts.getPersistentDataContainer().set(key, PersistentDataType.STRING, ct.toString());
						ts.update();
						WorldManager.addDispenser(b.getLocation(), ct);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
		Block b = e.getBlock();
		if (is.getType().toString().toLowerCase().contains("pickaxe") && e.getPlayer().getGameMode() != GameMode.CREATIVE && !e.isCancelled() && e.isDropItems() && b.getType() == Material.DISPENSER) {
			TileState ts = (TileState) b.getState();
			String type = ts.getPersistentDataContainer().get(key, PersistentDataType.STRING);
			if (type != null) {
				CustomType ct = CustomType.valueOf(type);
				if (ct != null) {
					e.setDropItems(false);
					b.getWorld().dropItemNaturally(b.getLocation(), getCustomBlock(ct));
					WorldManager.removeDispenser(b.getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
			Block b = e.getClickedBlock();
			if (b != null && b.getType() == Material.DISPENSER) {
				TileState ts = (TileState) b.getState();
				String type = ts.getPersistentDataContainer().get(key, PersistentDataType.STRING);
				if (type != null) {
					CustomType ct = CustomType.valueOf(type);
					if (ct != null) {
						if (ct != CustomType.AUTOBUILDER)
							e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDispense(BlockDispenseEvent e) {
		Block b = e.getBlock();
		Location loc = b.getLocation();
		DataHolder dh = WorldManager.getDataHolder(loc);
		if (dh != null)
			e.setCancelled(true);
	}

	public static ItemStack getCustomBlock(CustomType type) {
		ItemStack ret = new ItemStack(Material.DISPENSER, 1);
		ItemMeta im = ret.getItemMeta();
		im.setDisplayName(type.getItemName());
		im.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		im.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.toString());
		ret.setItemMeta(im);
		return ret;
	}

}
