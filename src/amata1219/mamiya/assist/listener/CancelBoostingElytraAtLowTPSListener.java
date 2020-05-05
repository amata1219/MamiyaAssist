package amata1219.mamiya.assist.listener;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.Reflection;
import amata1219.mamiya.assist.task.ControlBoostingElytraTask;

public class CancelBoostingElytraAtLowTPSListener implements Listener{

	private double[] recentTps;

	private ControlBoostingElytraTask elytraBoosterTask;

	private boolean appliedOrNotRegardlessOfTPS;
	private List<String> targetWorlds;
	private int tpsThreshold;
	private String startMessage, endMessage, useCancelMessage;

	public CancelBoostingElytraAtLowTPSListener(MamiyaAssist plugin){
		Class<?> CraftServer = Reflection.getReflectionClass("org.bukkit.craftbukkit.v" +plugin. getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "CraftServer");
		Object getCraftServer = CraftServer.cast(plugin.getServer());
		
		Field console = Reflection.getReflectionField(getCraftServer, "console");
		Object getMinecraftServer = Reflection.getReflectionValue(console, getCraftServer);
		
		Class<?> MinecraftServer = Reflection.getReflectionClass("net.minecraft.server.v" + plugin.getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "MinecraftServer");
		Object castObj = MinecraftServer.cast(getMinecraftServer);
		
		Field field = Reflection.getReflectionSuperField(castObj, "recentTps");
		recentTps = (double[]) Reflection.getReflectionValue(field, castObj);
		
		FileConfiguration config = plugin.getCustomConfig().config();
		
		appliedOrNotRegardlessOfTPS = config.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS");
		targetWorlds = config.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
		tpsThreshold = config.getInt("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies");
		startMessage = config.getString("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction");
		endMessage = config.getString("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction");
		useCancelMessage = config.getString("Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting");
		elytraBoosterTask = new ControlBoostingElytraTask(this);
		elytraBoosterTask.runTaskTimer(plugin, 0, config.getLong("Restriction on elytra boosts by fireworks.Messaging intervals") * 20);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		Action action = event.getAction();
		if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
		
		Player player = event.getPlayer();
		if(!player.isGliding()) return;
		
		ItemStack item = event.getItem();
		if(item == null || item.getType() != Material.FIREWORK_ROCKET) return;
		
		if(!appliedOrNotRegardlessOfTPS && recentTps[0] > tpsThreshold) return;
		
		if(!targetWorlds.contains(player.getWorld().getName()) && !targetWorlds.contains("ALL")) return;
		
		event.setCancelled(true);
		player.sendMessage(useCancelMessage);
	}

	public double[] getRecentTps() {
		return recentTps;
	}

	public void setElytraBoosterTask(ControlBoostingElytraTask elytraBoosterTask){
		this.elytraBoosterTask = elytraBoosterTask;
	}

	public ControlBoostingElytraTask getElytraBoosterTask() {
		return elytraBoosterTask;
	}

	public boolean isAlways() {
		return appliedOrNotRegardlessOfTPS;
	}

	public void setAlways(boolean always) {
		this.appliedOrNotRegardlessOfTPS = always;
	}

	public List<String> getWorlds() {
		return targetWorlds;
	}

	public void setWorlds(List<String> worlds) {
		this.targetWorlds = worlds;
	}

	public int getTpsThreshold() {
		return tpsThreshold;
	}

	public void setTpsThreshold(int tpsThreshold) {
		this.tpsThreshold = tpsThreshold;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public String getEndMessage() {
		return endMessage;
	}

	public void setEndMessage(String endMessage) {
		this.endMessage = endMessage;
	}

	public String getUseCancelMessage() {
		return useCancelMessage;
	}

	public void setUseCancelMessage(String useCancelMessage) {
		this.useCancelMessage = useCancelMessage;
	}

}
