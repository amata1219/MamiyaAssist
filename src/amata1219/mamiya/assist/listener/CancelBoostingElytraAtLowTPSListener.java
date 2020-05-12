package amata1219.mamiya.assist.listener;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.Reflection;
import amata1219.mamiya.assist.task.ControlBoostingElytraTask;

public class CancelBoostingElytraAtLowTPSListener implements Listener {
	
	private final MamiyaAssist plugin = MamiyaAssist.plugin();

	private double[] recentTps;

	private ControlBoostingElytraTask elytraBoosterTask;
	private BukkitTask runningTask;

	public CancelBoostingElytraAtLowTPSListener(){
		Class<?> CraftServer = Reflection.getReflectionClass("org.bukkit.craftbukkit.v" +plugin. getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "CraftServer");
		Object getCraftServer = CraftServer.cast(plugin.getServer());
		
		Field console = Reflection.getReflectionField(getCraftServer, "console");
		Object getMinecraftServer = Reflection.getReflectionValue(console, getCraftServer);
		
		Class<?> MinecraftServer = Reflection.getReflectionClass("net.minecraft.server.v" + plugin.getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "MinecraftServer");
		Object castObj = MinecraftServer.cast(getMinecraftServer);
		
		Field field = Reflection.getReflectionSuperField(castObj, "recentTps");
		recentTps = (double[]) Reflection.getReflectionValue(field, castObj);
		
		elytraBoosterTask = new ControlBoostingElytraTask();
		int intervals = plugin.config().getInt("Restriction on elytra boosts by fireworks.Messaging intervals");
		runningTask = elytraBoosterTask.runTaskTimer(plugin, 0, intervals * 20);
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
		
		if(!isAppliedRegardlessOfTPS() && !elytraBoosterTask.isRestricting()) return;
		
		List<String> targetWorlds = targetWorlds();
		if(!targetWorlds.contains(player.getWorld().getName()) && !targetWorlds.contains("ALL")) return;
		
		event.setCancelled(true);
		
		player.sendMessage(cancelMessage());
	}

	private boolean isEnabled(){
		return plugin.config().getBoolean("Restriction on elytra boosts by fireworks.Enabled or not");
	}
	
	public double[] getRecentTps() {
		return recentTps;
	}

	public void setElytraBoosterTask(ControlBoostingElytraTask elytraBoosterTask){
		this.elytraBoosterTask = elytraBoosterTask;
	}

	public BukkitTask task() {
		return runningTask;
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
