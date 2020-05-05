package amata1219.mamiya.assist.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.task.KickAFKerTask;
import net.ess3.api.events.AfkStatusChangeEvent;

public class KickAFKerListener implements Listener {
	
	private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();
	
	@EventHandler
	public void onAFKStatusChange(AfkStatusChangeEvent event){
		Player player = event.getAffected().getBase();
		if(event.getValue()) add(player);
		else remove(player);
	}
	
	@EventHandler
	public void onAFKerQuit(PlayerQuitEvent event){
		remove(event.getPlayer());
	}
	
	private void add(Player player){
		tasks.put(player.getUniqueId(), new KickAFKerTask(player).runTaskTimer(MamiyaAssist.getPlugin(), 1200, 1200));
	}
	
	private void remove(Player player){
		tasks.remove(player.getUniqueId()).cancel();
	}
	
}
