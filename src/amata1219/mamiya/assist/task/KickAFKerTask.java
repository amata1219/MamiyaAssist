package amata1219.mamiya.assist.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import amata1219.mamiya.assist.MamiyaAssist;

public class KickAFKerTask extends BukkitRunnable {
	
	private final MamiyaAssist plugin = MamiyaAssist.getPlugin();

	private final Player player;
	private int elapsedMinutes;
	
	public KickAFKerTask(Player player){
		this.player = player;
	}

	@Override
	public void run() {
		elapsedMinutes++;
		
		if(elapsedMinutes >= afkedTimeRequiredForKicks()) player.kickPlayer(kickMessage());
	}
	
	private int afkedTimeRequiredForKicks(){
		return plugin.config().getInt("Kicking AFKer.AFKed time required for kicks");
	}
	
	private String kickMessage(){
		return plugin.config().getString("Kicking AFKer.Message to send when the plugin kick AFKer");
	}

}
