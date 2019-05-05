package amata1219.mamiya.assist;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
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
		FileConfiguration conf = config.getConfig();
		commands = new HashMap<String, TabExecutor>();
		commands.put("mamiya", new MamiyaCommand(plugin, getServer().getWorld(conf.getString("Regen.OriginWorld")), conf.getInt("Regen.Limit")));
		PluginManager manager = getServer().getPluginManager();
		if(conf.getBoolean("ElytraBoosterUsageRestriction.Enable"))
			manager.registerEvents(elytraBoosterListener = new ElytraBoosterListener(plugin), plugin);
		if( conf.getBoolean("OneClickRide.Boat.Enable"))
			manager.registerEvents(oneClickRideListener = new OneClickRideListener(plugin), plugin);
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
