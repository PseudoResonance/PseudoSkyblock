package io.github.pseudoresonance.pseudoskyblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.pseudoresonance.pseudoapi.bukkit.SubCommandExecutor;
import io.github.pseudoresonance.pseudoapi.bukkit.Chat.Errors;
import io.github.pseudoresonance.pseudoapi.bukkit.language.LanguageManager;
import io.github.pseudoresonance.pseudoskyblock.PseudoSkyblock;

public class ResetLocalizationSC implements SubCommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || sender.hasPermission("pseudoskyblock.resetlocalization")) {
			try {
				LanguageManager.copyDefaultPluginLanguageFiles(PseudoSkyblock.plugin, true);
			} catch (Exception e) {
				PseudoSkyblock.plugin.getChat().sendPluginError(sender, Errors.GENERIC);
				return false;
			}
			PseudoSkyblock.plugin.getChat().sendPluginMessage(sender, LanguageManager.getLanguage(sender).getMessage("pseudoapi.localization_reset"));
			return true;
		} else {
			PseudoSkyblock.plugin.getChat().sendPluginError(sender, Errors.NO_PERMISSION, LanguageManager.getLanguage(sender).getMessage("pseudoapi.permission_reset_localization"));
			return false;
		}
	}

}
