package io.github.pseudoresonance.pseudoskyblock;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import io.github.pseudoresonance.pseudoapi.bukkit.CommandDescription;
import io.github.pseudoresonance.pseudoapi.bukkit.HelpSC;
import io.github.pseudoresonance.pseudoapi.bukkit.MainCommand;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoAPI;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoPlugin;
import io.github.pseudoresonance.pseudoapi.bukkit.PseudoUpdater;
import io.github.pseudoresonance.pseudoskyblock.WorldManager.CustomType;
import io.github.pseudoresonance.pseudoskyblock.commands.ReloadLocalizationSC;
import io.github.pseudoresonance.pseudoskyblock.commands.ReloadSC;
import io.github.pseudoresonance.pseudoskyblock.commands.ResetLocalizationSC;
import io.github.pseudoresonance.pseudoskyblock.commands.ResetSC;
import io.github.pseudoresonance.pseudoskyblock.completers.PseudoSkyblockTC;
import io.github.pseudoresonance.pseudoskyblock.events.BlockEH;
import io.github.pseudoresonance.pseudoskyblock.events.InventoryClickEH;
import io.github.pseudoresonance.pseudoskyblock.events.PaperDispenserEH;
import io.github.pseudoresonance.pseudoskyblock.events.SpigotDispenserEH;

public class PseudoSkyblock extends PseudoPlugin {

	public static PseudoPlugin plugin;
	
	private static MainCommand mainCommand;
	private static HelpSC helpSubCommand;
	
	private static Config config;

	@SuppressWarnings("unused")
	private static Metrics metrics = null;
	
	public void onLoad() {
		PseudoUpdater.registerPlugin(this);
	}
	
	public void onEnable() {
		super.onEnable();
		this.saveDefaultConfig();
		plugin = this;
		config = new Config(this);
		config.updateConfig();
		mainCommand = new MainCommand(plugin);
		helpSubCommand = new HelpSC(plugin);
		initializeCommands();
		initializeTabcompleters();
		initializeSubCommands();
		initializeListeners();
		setCommandDescriptions();
		config.reloadConfig();
		PseudoAPI.registerConfig(config);
		createRecipes();
		initializeMetrics();
		WorldManager.load();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			WorldManager.tick();
		}, 1, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			WorldManager.save();
		}, Config.dataSaveInterval, Config.dataSaveInterval);
	}
	
	public void onDisable() {
		WorldManager.save();
		super.onDisable();
		Bukkit.getScheduler().cancelTasks(this);
	}

	private void initializeMetrics() {
		metrics = new Metrics(this, 7651);
	}
	
	public static Config getConfigOptions() {
		return PseudoSkyblock.config;
	}

	private void initializeCommands() {
		this.getCommand("pseudoskyblock").setExecutor(mainCommand);
	}

	private void initializeSubCommands() {
		subCommands.put("help", helpSubCommand);
		subCommands.put("reload", new ReloadSC());
		subCommands.put("reloadlocalization", new ReloadLocalizationSC());
		subCommands.put("reset", new ResetSC());
		subCommands.put("resetlocalization", new ResetLocalizationSC());
	}

	private void initializeTabcompleters() {
		this.getCommand("pseudoskyblock").setTabCompleter(new PseudoSkyblockTC());
	}
	
	private void initializeListeners() {
		getServer().getPluginManager().registerEvents(new InventoryClickEH(), this);
		getServer().getPluginManager().registerEvents(new BlockEH(), this);
		if (isPaper())
			getServer().getPluginManager().registerEvents(new PaperDispenserEH(), this);
		else
			getServer().getPluginManager().registerEvents(new SpigotDispenserEH(), this);
	}

	private void setCommandDescriptions() {
		commandDescriptions.add(new CommandDescription("pseudoskyblock", "pseudoskyblock.pseudoskyblock_help", ""));
		commandDescriptions.add(new CommandDescription("pseudoskyblock help", "pseudoskyblock.pseudoskyblock_help_help", ""));
		commandDescriptions.add(new CommandDescription("pseudoskyblock reload", "pseudoskyblock.pseudoskyblock_reload_help", "pseudoskyblock.reload"));
		commandDescriptions.add(new CommandDescription("pseudoskyblock reloadlocalization", "pseudoskyblock.pseudoskyblock_reloadlocalization_help", "pseudoskyblock.reloadlocalization"));
		commandDescriptions.add(new CommandDescription("pseudoskyblock reset", "pseudoskyblock.pseudoskyblock_reset_help", "pseudoskyblock.reset"));
		commandDescriptions.add(new CommandDescription("pseudoskyblock resetlocalization", "pseudoskyblock.pseudoskyblock_resetlocalization_help", "pseudoskyblock.resetlocalization"));
	}

	private static void createRecipes() {
		ShapedRecipe autoMiner;
		autoMiner = new ShapedRecipe(new NamespacedKey(plugin, CustomType.AUTOMINER.toString().toLowerCase()), BlockEH.getCustomBlock(CustomType.AUTOMINER));
		autoMiner.shape("***", "*%*", "***");
		autoMiner.setIngredient('*', Material.COBBLESTONE);
		autoMiner.setIngredient('%', Material.STONE_PICKAXE);
		Bukkit.getServer().addRecipe(autoMiner);
		ShapedRecipe autoDigger;
		autoDigger = new ShapedRecipe(new NamespacedKey(plugin, CustomType.AUTODIGGER.toString().toLowerCase()), BlockEH.getCustomBlock(CustomType.AUTODIGGER));
		autoDigger.shape("***", "*%*", "***");
		autoDigger.setIngredient('*', Material.COBBLESTONE);
		autoDigger.setIngredient('%', Material.STONE_SHOVEL);
		Bukkit.getServer().addRecipe(autoDigger);
		ShapedRecipe autoCutter;
		autoCutter = new ShapedRecipe(new NamespacedKey(plugin, CustomType.AUTOCUTTER.toString().toLowerCase()), BlockEH.getCustomBlock(CustomType.AUTOCUTTER));
		autoCutter.shape("***", "*%*", "***");
		autoCutter.setIngredient('*', Material.COBBLESTONE);
		autoCutter.setIngredient('%', Material.STONE_AXE);
		Bukkit.getServer().addRecipe(autoCutter);
		ShapedRecipe autoBuilder;
		autoBuilder = new ShapedRecipe(new NamespacedKey(plugin, CustomType.AUTOBUILDER.toString().toLowerCase()), BlockEH.getCustomBlock(CustomType.AUTOBUILDER));
		autoBuilder.shape("***", "*%*", "***");
		autoBuilder.setIngredient('*', Material.COBBLESTONE);
		autoBuilder.setIngredient('%', Material.REDSTONE);
		Bukkit.getServer().addRecipe(autoBuilder);
	}
	
	private static boolean isPaper() {
		try {
			if (Class.forName("com.destroystokyo.paper.VersionHistoryManager") != null)
				return true;
		} catch (ClassNotFoundException e) {
		}
		return false;
	}

}