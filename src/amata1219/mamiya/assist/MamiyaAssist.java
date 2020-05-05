package amata1219.mamiya.assist;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.mamiya.assist.command.MamiyaCommand;
import amata1219.mamiya.assist.config.Config;
import amata1219.mamiya.assist.listener.CancelBoostingElytraAtLowTPSListener;
import amata1219.mamiya.assist.listener.ClickToSummonBoatListener;

public class MamiyaAssist extends JavaPlugin{

	private static MamiyaAssist plugin;

	private Config config;
	private CancelBoostingElytraAtLowTPSListener boostingListener;
	private ClickToSummonBoatListener boatListener;

	private HashMap<String, CommandExecutor> commands;

	@Override
	public void onEnable(){
		plugin = this;
		
		config = new Config(plugin);
		config.saveDefaultConfig();
		
		FileConfiguration conf = config.config();
		commands = new HashMap<String, CommandExecutor>();
		commands.put("mamiya", new MamiyaCommand(plugin, getServer().getWorld(conf.getString("Regen.OriginWorld")), conf.getInt("Regen.Limit")));
		
		PluginManager manager = getServer().getPluginManager();
		if(conf.getBoolean("Restriction on elytra boosts by fireworks.Enabled or not"))
			manager.registerEvents(boostingListener = new CancelBoostingElytraAtLowTPSListener(plugin), plugin);
		
		if(conf.getBoolean("Temporary boat.Enabled or not"))
			manager.registerEvents(boatListener = new ClickToSummonBoatListener(plugin), plugin);
	}

	@Override
	public void onDisable(){
		HandlerList.unregisterAll((JavaPlugin) this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	public static MamiyaAssist getPlugin(){
		return plugin;
	}

	public Config getCustomConfig(){
		return config;
	}

	public void setElytraBoosterListener(CancelBoostingElytraAtLowTPSListener elytraBoosterListener) {
		this.boostingListener = elytraBoosterListener;
	}

	public CancelBoostingElytraAtLowTPSListener getElytraBoosterListener() {
		return boostingListener;
	}

	public ClickToSummonBoatListener getOneClickRideListener(){
		return boatListener;
	}

}
