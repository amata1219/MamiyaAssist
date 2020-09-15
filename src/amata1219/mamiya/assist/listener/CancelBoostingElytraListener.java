package amata1219.mamiya.assist.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class CancelBoostingElytraListener implements Listener {
	
	private final MamiyaAssistOld plugin = MamiyaAssistOld.plugin();

	private final TPSObserverForBoostingElytra observer = new TPSObserverForBoostingElytra();

	public BukkitTask activate(){
		int intervals = plugin.config().getInt("Restriction on elytra boosts by fireworks.Messaging intervals");
		return observer.runTaskTimer(plugin, 1200, intervals * 20);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(!isEnabled()) return;
		
		Action action = event.getAction();
		if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
		
		Player player = event.getPlayer();
		if(!player.isGliding()) return;
		
		ItemStack item = event.getItem();
		if(item == null || item.getType() != Material.FIREWORK_ROCKET) return;
		
		if(!isAppliedRegardlessOfTPS() && !observer.isAtLowTPS()) return;
		
		List<String> targetWorlds = targetWorlds();
		if(!targetWorlds.contains(player.getWorld().getName()) && !targetWorlds.contains("ALL")) return;
		
		event.setCancelled(true);
		
		player.sendMessage(cancelMessage());
	}

	private boolean isEnabled(){
		return plugin.config().getBoolean("Restriction on elytra boosts by fireworks.Enabled or not");
	}
	
	private boolean isAppliedRegardlessOfTPS() {
		return plugin.config().getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS");
	}

	private List<String> targetWorlds() {
		return plugin.config().getStringList("Restriction on elytra boosts by fireworks.Target worlds");
	}

	private String cancelMessage() {
		return plugin.config().getString("Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting");
	}

}
