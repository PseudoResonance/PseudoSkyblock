package io.github.pseudoresonance.pseudoskyblock.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PseudoSkyblockTC implements TabCompleter {

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> possible = new ArrayList<String>();
		if (args.length == 1) {
			possible.add("help");
			if (sender.hasPermission("pseudoskyblock.reload")) {
				possible.add("reload");
			}
			if (sender.hasPermission("pseudoskyblock.reset")) {
				possible.add("reset");
			}
			if (sender.hasPermission("pseudoskyblock.reloadlocalization")) {
				possible.add("reloadlocalization");
			}
			if (sender.hasPermission("pseudoskyblock.resetlocalization")) {
				possible.add("resetlocalization");
			}
			if (args[0].equalsIgnoreCase("")) {
				return possible;
			} else {
				List<String> checked = new ArrayList<String>();
				for (String check : possible) {
					if (check.toLowerCase().startsWith(args[0].toLowerCase())) {
						checked.add(check);
					}
				}
				return checked;
			}
		}
		return null;
	}

}
