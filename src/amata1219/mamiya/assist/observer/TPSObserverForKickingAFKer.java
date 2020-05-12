package amata1219.mamiya.assist.observer;

public class TPSObserverForKickingAFKer extends TPSObserver {

	@Override
	protected int tpsThreshold() {
		return plugin.config().getInt("Kicking AFKer.TPS Threshold to which the rule applies");
	}

	@Override
	protected String startMessage() {
		return plugin.config().getString("Kicking AFKer.Message.When the plugin started appling the rule");
	}

	@Override
	protected String endMessage() {
		return plugin.config().getString("Kicking AFKer.Message.When the plugin stopped appling the rule");
	}

}
