package amata1219.mamiya.assist.task;

import java.lang.reflect.Field;

import org.bukkit.scheduler.BukkitRunnable;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.Reflection;

public class ControlBoostingElytraTask extends BukkitRunnable {

	private final MamiyaAssist plugin = MamiyaAssist.plugin();
	
	private double[] recentTps;
	
	private boolean isRestricting;
	
	public ControlBoostingElytraTask(){
		Class<?> CraftServer = Reflection.getReflectionClass("org.bukkit.craftbukkit.v" +plugin. getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "CraftServer");
		Object getCraftServer = CraftServer.cast(plugin.getServer());
		
		Field console = Reflection.getReflectionField(getCraftServer, "console");
		Object getMinecraftServer = Reflection.getReflectionValue(console, getCraftServer);
		
		Class<?> MinecraftServer = Reflection.getReflectionClass("net.minecraft.server.v" + plugin.getServer().getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1") + "." + "MinecraftServer");
		Object castObj = MinecraftServer.cast(getMinecraftServer);
		
		Field field = Reflection.getReflectionSuperField(castObj, "recentTps");
		recentTps = (double[]) Reflection.getReflectionValue(field, castObj);
	}

	@Override
	public void run() {
		if(isRestricting){
			if(recentTps[0] > tpsThreshold()){
				plugin.getServer().broadcastMessage(endMessage());
				isRestricting = false;
			}
		}else{
			if(recentTps[0] <= tpsThreshold()){
				plugin.getServer().broadcastMessage(startMessage());
				isRestricting = true;
			}
		}
	}
	
	public boolean isRestricting(){
		return isRestricting;
	}
	
	private int tpsThreshold() {
		return plugin.config().getInt("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies");
	}
	
	private String startMessage(){
		return plugin.config().getString("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction");
	}
	
	private String endMessage(){
		return plugin.config().getString("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction");
	}

}
