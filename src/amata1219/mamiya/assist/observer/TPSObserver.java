package amata1219.mamiya.assist.observer;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TPSObserver extends BukkitRunnable {
	
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
