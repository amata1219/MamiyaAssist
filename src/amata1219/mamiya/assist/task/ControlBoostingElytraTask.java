package amata1219.mamiya.assist.task;

import org.bukkit.scheduler.BukkitRunnable;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.listener.CancelBoostingElytraAtLowTPSListener;

public class ControlBoostingElytraTask extends BukkitRunnable {

	private final MamiyaAssist plugin = MamiyaAssist.getPlugin();
	private CancelBoostingElytraAtLowTPSListener listener;
	private boolean appliedRestriction;

	public ControlBoostingElytraTask(CancelBoostingElytraAtLowTPSListener listener){
		this.listener = listener;
	}

	@Override
	public void run() {
		if(appliedRestriction){
			if(listener.getRecentTps()[0] > listener.getTpsThreshold()){
				plugin.getServer().broadcastMessage(listener.getEndMessage());
				appliedRestriction = false;
			}
		}else if(listener.getRecentTps()[0] <= listener.getTpsThreshold()){
			plugin.getServer().broadcastMessage(listener.getStartMessage());
			appliedRestriction = true;
		}
	}

}
