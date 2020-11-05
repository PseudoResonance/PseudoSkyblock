package io.github.pseudoresonance.pseudoskyblock.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoskyblock.PseudoSkyblock;

public class InventoryClickEH implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory i = e.getView().getTopInventory();
		Player p = (Player) e.getWhoClicked();
		if (i instanceof AnvilInventory) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.DISPENSER) {
				ItemMeta im = e.getCurrentItem().getItemMeta();
				if (im != null && im.getPersistentDataContainer().has(BlockEH.key, PersistentDataType.STRING)) {
					e.setCancelled(true);
					PseudoSkyblock.plugin.getChat().sendPluginError(p, Errors.CUSTOM, LanguageManager.getLanguage(p).getMessage("pseudoskyblock.error_custom_anvil"));
				}
			}
		}
	}

}
