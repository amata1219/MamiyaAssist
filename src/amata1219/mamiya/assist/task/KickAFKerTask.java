package amata1219.mamiya.assist.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.config.Config;

public class KickAFKerTask extends BukkitRunnable {
	
	private final Config config = MamiyaAssist.getPlugin().getCustomConfig();
	private final Player player;
	private int elapsedMinutes;
	
	public KickAFKerTask(Player player){
		this.player = player;
	}

	@Override
	public void run() {
		elapsedMinutes++;
		
		if(elapsedMinutes >= ?) player.kickPlayer("");
	}

}
