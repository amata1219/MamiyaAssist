package amata1219.mamiya.assist.observer;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.Reflection;

public abstract class TPSObserver extends BukkitRunnable {
	
	private static final double[] recentTps;

	static {
		Server server = Bukkit.getServer();
		String version = server.getClass().getPackage().getName().replaceFirst(".*(\\d+_\\d+_R\\d+).*", "$1");
		
		Class<?> CraftServer = Reflection.getReflectionClass("org.bukkit.craftbukkit.v" + version + "." + "CraftServer");
		Object craftServer = CraftServer.cast(server);
		
		Field console = Reflection.getReflectionField(craftServer, "console");
		Object consoleServer = Reflection.getReflectionValue(console, craftServer);
		
		Class<?> MinecraftServer = Reflection.getReflectionClass("net.minecraft.server.v" + version + "." + "MinecraftServer");
		Object minecraftServer = MinecraftServer.cast(consoleServer);
		
		Field field = Reflection.getReflectionSuperField(minecraftServer, "recentTps");
		recentTps = (double[]) Reflection.getReflectionValue(field, minecraftServer);
	}
	
	protected final MamiyaAssist plugin = MamiyaAssist.plugin();
	private boolean atLowTPS;
	
	@Override
	public void run() {
		if(atLowTPS){
			if(recentTps[0] > tpsThreshold()){
				plugin.getServer().broadcastMessage(endMessage());
				atLowTPS = false;
			}
		}else{
			if(recentTps[0] <= tpsThreshold()){
				plugin.getServer().broadcastMessage(startMessage());
				atLowTPS = true;
			}
		}
	}
	
	public boolean isAtLowTPS(){
		return atLowTPS;
	}
	
	protected abstract int tpsThreshold();
	
	protected abstract String startMessage();
	
	protected abstract String endMessage();

}
