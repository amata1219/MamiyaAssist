package amata1219.mamiya.assist;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MamiyaAssist extends JavaPlugin{

	private static MamiyaAssist plugin;

	private CustomConfig config;
	private ElytraBoosterListener elytraBoosterListener;
	private OneClickRideListener oneClickRideListener;

	private HashMap<String, TabExecutor> commands;

	@Override
	public void onEnable(){
		plugin = this;
		config = new CustomConfig(plugin);
		config.saveDefaultConfig();
		commands = new HashMap<String, TabExecutor>();
		commands.put("mamiya", new MamiyaCommand(plugin));
		commands.put("otameshi", new OtameshiCommand());
		PluginManager pm = getServer().getPluginManager();
		FileConfiguration c = config.getConfig();
		if(c.getBoolean("ElytraBoosterUsageRestriction.Enable"))pm.registerEvents(elytraBoosterListener = new ElytraBoosterListener(plugin), plugin);
		if(c.getBoolean("OneClickRide.Minecart.Enable") || c.getBoolean("OneClickRide.Boat.Enable"))pm.registerEvents(oneClickRideListener = new OneClickRideListener(plugin), plugin);
	}

	@Override
	public void onDisable(){

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	public static MamiyaAssist getPlugin(){
		return plugin;
	}

	public CustomConfig getCustomConfig(){
		return config;
	}

	public void setElytraBoosterListener(ElytraBoosterListener elytraBoosterListener) {
		this.elytraBoosterListener = elytraBoosterListener;
	}

	public ElytraBoosterListener getElytraBoosterListener() {
		return elytraBoosterListener;
	}

	public OneClickRideListener getOneClickRideListener(){
		return oneClickRideListener;
	}

}
