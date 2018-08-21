package amata1219.mamiya.assist;

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

public class ElytraBoosterListener implements Listener{

	private double[] recentTps;

	private ElytraBoosterTask elytraBoosterTask;

	private boolean always;
	private List<String> worlds;
	private int tpsThreshold;
	private String startMessage, endMessage, useCancelMessage;

	public ElytraBoosterListener(MamiyaAssist plugin){
		Class<?> CraftServer = getReflectionClass("org.bukkit.craftbukkit.v" +plugin. getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "CraftServer");
		Object getCraftServer = CraftServer.cast(plugin.getServer());
		Field console = getReflectionField(getCraftServer, "console");
		Object getMinecraftServer = getReflectionValue(console, getCraftServer);
		Class<?> MinecraftServer = getReflectionClass("net.minecraft.server.v" + plugin.getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "MinecraftServer");
		Object castObj = MinecraftServer.cast(getMinecraftServer);
		Field field = getReflectionSuperField(castObj, "recentTps");
		recentTps = (double[]) getReflectionValue(field, castObj);
		FileConfiguration c = plugin.getCustomConfig().getConfig();
		always = c.getBoolean("ElytraBoosterUsageRestriction.Always");
		worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
		tpsThreshold = c.getInt("ElytraBoosterUsageRestriction.TPSThreshold");
		startMessage = c.getString("ElytraBoosterUsageRestriction.Message.Start");
		endMessage = c.getString("ElytraBoosterUsageRestriction.Message.End");
		useCancelMessage = c.getString("ElytraBoosterUsageRestriction.Message.UseCancel");
		elytraBoosterTask = new ElytraBoosterTask(this);
		elytraBoosterTask.runTaskTimer(plugin, 0, c.getLong("ElytraBoosterUsageRestriction.MessageTaskInterval"));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Action a = e.getAction();
		if(!(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK))return;
		Player p = e.getPlayer();
		if(!p.isGliding())return;
		ItemStack item = e.getItem();
		if(item != null && item.getType() == Material.FIREWORK_ROCKET){
			if(always || recentTps[0] <= tpsThreshold){
				if(worlds.contains("ALL") || worlds.contains(p.getWorld().getName())){
					e.setCancelled(true);
					p.sendMessage(useCancelMessage);
				}
			}
		}
	}

	private Class<?> getReflectionClass(String s){
		Class<?> c = null;
		try{
			c = Class.forName(s);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return c;
	}

	private Field getReflectionField(Object obj, String s){
		try{
			Field f = obj.getClass().getDeclaredField(s);
			f.setAccessible(true);
			return f;
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}

	private Field getReflectionSuperField(Object obj, String s){
		try{
			Field f = obj.getClass().getSuperclass().getDeclaredField(s);
			f.setAccessible(true);
			return f;
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}

	public Object getReflectionValue(Field f, Object obj){
		try {
			return f.get(obj);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
		return null;
	}

	public double[] getRecentTps() {
		return recentTps;
	}

	public void setElytraBoosterTask(ElytraBoosterTask elytraBoosterTask){
		this.elytraBoosterTask = elytraBoosterTask;
	}

	public ElytraBoosterTask getElytraBoosterTask() {
		return elytraBoosterTask;
	}

	public boolean isAlways() {
		return always;
	}

	public void setAlways(boolean always) {
		this.always = always;
	}

	public List<String> getWorlds() {
		return worlds;
	}

	public void setWorlds(List<String> worlds) {
		this.worlds = worlds;
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
