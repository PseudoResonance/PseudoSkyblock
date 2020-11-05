package io.github.pseudoresonance.pseudoskyblock.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.Chat;
import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoskyblock.PseudoSkyblock;

public class ResetSC implements SubCommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoskyblock.reset")) {
			try {
				File conf = new File(PseudoSkyblock.plugin.getDataFolder(), "config.yml");
				conf.delete();
				PseudoSkyblock.plugin.saveDefaultConfig();
				PseudoSkyblock.plugin.reloadConfig();
			} catch (Exception e) {
				PseudoSkyblock.plugin.getChat().sendPluginError(sender, Chat.Errors.GENERIC);
				return false;
			}
			PseudoSkyblock.getConfigOptions().reloadConfig();
			PseudoSkyblock.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.config_reset"));
			return true;
		} else {
			PseudoSkyblock.plugin.getChat().sendPluginError(sender, Chat.Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_config"));
			return false;
		}
	}

}
