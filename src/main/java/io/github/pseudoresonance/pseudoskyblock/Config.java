package io.github.pseudoresonance.pseudoskyblock;

import org.bukkit.configuration.file.FileConfiguration;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.data.PluginConfig;

public class Config extends PluginConfig {

	public static long dataSaveInterval = 3;

	public static boolean affectedByRedstone = true;
	public static long autoMinerSpeed = 300;

	public void reloadConfig() {
		FileConfiguration fc = PseudoSkyblock.plugin.getConfig();
		dataSaveInterval = getLong(fc, "DataSaveInterval", dataSaveInterval);
		
		affectedByRedstone = getBoolean(fc, "AffectedByRedstone", affectedByRedstone);
		autoMinerSpeed = getLong(fc, "AutoMinerSpeed", autoMinerSpeed);
	}

	public Config(PseudoPlugin plugin) {
		super(plugin);
	}

}