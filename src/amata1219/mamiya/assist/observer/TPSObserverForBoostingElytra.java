package amata1219.mamiya.assist.observer;

public class TPSObserverForBoostingElytra extends TPSObserver {

	@Override
	protected int tpsThreshold() {
		return plugin.config().getInt("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies");
	}

	@Override
	protected String startMessage() {
		return plugin.config().getString("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction");
	}

	@Override
	protected String endMessage() {
		return plugin.config().getString("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction");
	}

}
