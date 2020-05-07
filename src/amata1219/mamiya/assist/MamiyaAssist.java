package amata1219.mamiya.assist;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import amata1219.mamiya.assist.command.MamiyaCommand;
import amata1219.mamiya.assist.config.Config;
import amata1219.mamiya.assist.listener.CancelBoostingElytraAtLowTPSListener;
import amata1219.mamiya.assist.listener.TemporaryBoatListener;

public class MamiyaAssist extends JavaPlugin{

	private static MamiyaAssist plugin;

	private final HashMap<String, CommandExecutor> commands = new HashMap<>();

	@Override
	public void onEnable(){
		plugin = this;
		
		saveDefaultConfig();
		
		Plugin maybeWe = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if(!(maybeWe instanceof WorldEditPlugin))
			throw new NullPointerException("[MamiyaAssist] Not found WorldEdit");
		
		commands.put("mamiya", new MamiyaCommand((WorldEditPlugin) maybeWe));
		
		if(conf.getBoolean("Temporary boat.Enabled or not"))
		
		registerListeners(
			new CancelBoostingElytraAtLowTPSListener(),
			new TemporaryBoatListener()
		);
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
	
	public FileConfiguration config(){
		return getConfig();
	}
	
	private void registerListeners(Listener... listeners){
		for(Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
	}
	
}
