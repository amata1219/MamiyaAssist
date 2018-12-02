package amata1219.easy.carts.plus;

import org.bukkit.util.Vector;

public class SpeedAndYaw {
	private Double speed;
	private Vector direction;

	public SpeedAndYaw(double _speed, Vector _direction) {
		this.speed = Double.valueOf(_speed);
		this.direction = _direction;
	}

	public Double getSpeed() {
		return this.speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Vector getDirection() {
		return this.direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}
}
