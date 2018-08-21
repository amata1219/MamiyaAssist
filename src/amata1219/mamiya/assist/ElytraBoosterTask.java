package amata1219.mamiya.assist;

import org.bukkit.scheduler.BukkitRunnable;

public class ElytraBoosterTask extends BukkitRunnable{

	private ElytraBoosterListener elytraBoosterListener;
	private boolean isRestricted;

	public ElytraBoosterTask(ElytraBoosterListener elytraBoosterListener){
		this.elytraBoosterListener = elytraBoosterListener;
	}

	@Override
	public void run() {
		if(isRestricted){
			if(elytraBoosterListener.getRecentTps()[0] > elytraBoosterListener.getTpsThreshold()){
				MamiyaAssist.getPlugin().getServer().broadcastMessage(elytraBoosterListener.getEndMessage());
				isRestricted = false;
			}
		}else if(elytraBoosterListener.getRecentTps()[0] <= elytraBoosterListener.getTpsThreshold()){
			MamiyaAssist.getPlugin().getServer().broadcastMessage(elytraBoosterListener.getStartMessage());
			isRestricted = true;
		}
	}

}
