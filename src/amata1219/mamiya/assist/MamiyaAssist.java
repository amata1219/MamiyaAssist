package amata1219.mamiya.assist;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import amata1219.mamiya.assist.command.MamiyaCommand;
import amata1219.mamiya.assist.listener.CancelBoostingElytraListener;
import amata1219.mamiya.assist.listener.KickAFKerListener;
import amata1219.mamiya.assist.listener.TemporaryBoatListener;

public class MamiyaAssist extends JavaPlugin {

	private static MamiyaAssist plugin;

	private final HashMap<String, CommandExecutor> commands = new HashMap<>();
	private final ArrayList<BukkitTask> activeTasks = new ArrayList<>();
	
	@Override
	public void onEnable(){
		plugin = this;
		
		saveDefaultConfig();
		
		Plugin maybeWe = getServer().getPluginManager().getPlugin("WorldEdit");
		if(!(maybeWe instanceof WorldEditPlugin)) throw new NullPointerException("[MamiyaAssist] Not found WorldEdit");
		
		commands.put("mamiya", new MamiyaCommand((WorldEditPlugin) maybeWe));
		
		CancelBoostingElytraListener l0 = new CancelBoostingElytraListener();
		KickAFKerListener l1 = new KickAFKerListener();
		
		activeTasks.add(l0.activate());
		activeTasks.add(l1.activate());
		
		registerListeners(
			l0,
			new TemporaryBoatListener(),
			l1
		);
	}

	@Override
	public void onDisable(){
		activeTasks.forEach(BukkitTask::cancel);
		HandlerList.unregisterAll((JavaPlugin) this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return commands.get(command.getName()).onCommand(sender, command, label, args);
	}

	public static MamiyaAssist plugin(){
		return plugin;
	}
	
	public FileConfiguration config(){
		return getConfig();
	}
	
	private void registerListeners(Listener... listeners){
		for(Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
	}
	
}
