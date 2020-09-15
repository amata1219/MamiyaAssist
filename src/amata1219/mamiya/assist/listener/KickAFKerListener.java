package amata1219.mamiya.assist.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import amata1219.mamiya.assist.task.KickAFKerTask;
import net.ess3.api.events.AfkStatusChangeEvent;

public class KickAFKerListener implements Listener {
	
	private final MamiyaAssistOld plugin = MamiyaAssistOld.plugin();

	private final TPSObserverForKickingAFKer observer = new TPSObserverForKickingAFKer();
	
	private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();
	
	public BukkitTask activate(){
		int intervals = plugin.config().getInt("Kicking AFKer.Messaging intervals");
		return observer.runTaskTimer(plugin, 1200, intervals * 20);
	}
	
	@EventHandler
	public void onAFKStatusChange(AfkStatusChangeEvent event){
		if(!isEnabled() || !(isAppliedRegardlessOfTPS() || observer.isAtLowTPS())) return;
		
		Player player = event.getAffected().getBase();
		if(event.getValue()) add(player);
		else remove(player);
	}
	
	@EventHandler
	public void onAFKerQuit(PlayerQuitEvent event){
		remove(event.getPlayer());
	}
	
	private void add(Player player){
		tasks.put(player.getUniqueId(), new KickAFKerTask(player).runTaskTimer(plugin, 1200, 1200));
	}
	
	private void remove(Player player){
		BukkitTask task = tasks.remove(player.getUniqueId());
		if(task != null) task.cancel();
	}
	
	private boolean isEnabled(){
		return plugin.config().getBoolean("Kicking AFKer.Enabled or not");
	}
	
	private boolean isAppliedRegardlessOfTPS() {
		return plugin.config().getBoolean("Kicking AFKer.Applied or not regardless of TPS");
	}
	
}
