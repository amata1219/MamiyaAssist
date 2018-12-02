package amata1219.easy.carts.plus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class Utils {
	public static final double MINECART_VANILLA_PUSH_SPEED = 0.2D;
	public static final double MINECART_VANILLA_MAX_SPEED = 0.4D;

	public static final List<Material> rails = new ArrayList<Material>(Arrays.asList(Material.RAIL, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL));

	public static RideableMinecart getValidMineCart(Vehicle vehicle, boolean mustHavePassenger) {
		RideableMinecart cart = null;

		if (!(vehicle instanceof RideableMinecart))
			return null;
		cart = (RideableMinecart) vehicle;

		Entity firstPassenger = GetFirstPassenger(cart);
		if (firstPassenger == null) {
			return null;
		}
		if ((mustHavePassenger) && (((cart.isEmpty()) || (!(firstPassenger instanceof Player))))) {
			return null;
		}
		return cart;
	}

	public static Entity GetFirstPassenger(Minecart toCart) {
		List<Entity> passengers = toCart.getPassengers();

		if (passengers.isEmpty()) {
			return null;
		}
		return ((Entity) passengers.get(0));
	}

	public static boolean isFlatRail(Location location) {
		if (rails.contains(location.getBlock().getType())) {
			Rails testRail = (Rails) location.getBlock().getState().getData();
			if (!(testRail.isOnSlope())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isRailPerpendicular(Location myLocation, Location otherLocation) {
		Block myBlock = myLocation.getBlock();
		Block otherBlock = otherLocation.getBlock();
		if (rails.contains(otherBlock.getType())) {
			if ((myBlock.getData() == 0) && (otherBlock.getData() == 1)) {
				return true;
			}
			if ((myBlock.getData() == 1) && (otherBlock.getData() == 0)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean isRailParallel(Location myLocation, Location otherLocation) {
		Block myBlock = myLocation.getBlock();
		Block otherBlock = otherLocation.getBlock();

		return ((rails.contains(otherBlock.getType())) && (myBlock.getData() == otherBlock.getData()));
	}

	public static Vector getUnitVectorFromYaw(float yaw)
  {
    BlockFace facing = getBlockFaceFromYaw(yaw);
    //1.$SwitchMap$org$bukkit$block$BlockFace[facing.ordinal()]
    switch (facing.ordinal())
    {
    case 1:
      return new Vector(0, 0, 1);
    case 2:
      return new Vector(-1, 0, 0);
    case 3:
      return new Vector(0, 0, -1);
    }
    return new Vector(1, 0, 0);
  }

	public static boolean isIntersection(Location myLocation, Vector movementDirection) {
		if (isFlatRail(myLocation)) {
			Location front = myLocation.clone().add(movementDirection.normalize());
			Location back = myLocation.clone().subtract(movementDirection.normalize());
			Location left = myLocation.clone().add(movementDirection.getZ(), 0.0D, -movementDirection.getX());
			Location right = myLocation.clone().add(-movementDirection.getZ(), 0.0D, movementDirection.getX());

			if ((isRailPerpendicular(myLocation, left)) && (isRailPerpendicular(myLocation, right))) {
				return true;
			}
			if (((isRailPerpendicular(myLocation, left))
					&& (((isRailParallel(myLocation, front)) || (isRailParallel(myLocation, back)))))
					|| ((isRailPerpendicular(myLocation, right))
							&& (((isRailParallel(myLocation, front)) || (isRailParallel(myLocation, back)))))) {
				return true;
			}
			if (((isRailParallel(myLocation, left))
					&& (((isRailPerpendicular(myLocation, front)) || (isRailPerpendicular(myLocation, back)))))
					|| ((isRailParallel(myLocation, right)) && (((isRailPerpendicular(myLocation, front))
							|| (isRailPerpendicular(myLocation, back)))))) {
				return true;
			}
		}
		return false;
	}

	public static BlockFace getBlockFaceFromYaw(float yaw) {
		if (yaw < 0.0F) {
			yaw += 360.0F;
		}
		yaw %= 360.0F;

		float straightAngle = 90.0F;

		if (((yaw >= 0.0F) && (yaw < straightAngle / 2.0F)) || (yaw >= 360.0F - (straightAngle / 2.0F))) {
			return BlockFace.SOUTH;
		}
		if ((yaw >= straightAngle / 2.0F) && (yaw < 135.0F)) {
			return BlockFace.WEST;
		}
		if ((yaw >= 135.0F) && (yaw < 360.0D - (straightAngle * 1.5D))) {
			return BlockFace.NORTH;
		}

		return BlockFace.EAST;
	}

	public static boolean isMovingUp(VehicleMoveEvent event) {
		return (event.getTo().getY() - event.getFrom().getY() > 0.0D);
	}

	public static boolean isMovingDown(VehicleMoveEvent event) {
		return (event.getTo().getY() - event.getFrom().getY() < 0.0D);
	}

	public static Rails getRailInFront(Location testLoc) {
		try {
			Location testLocUnder = testLoc.clone().subtract(0.0D, 1.0D, 0.0D);

			if (rails.contains(testLoc.getBlock().getType())) {
				return ((Rails) testLoc.getBlock().getState().getData());
			}
			if (rails.contains(testLocUnder.getBlock().getType())) {
				return ((Rails) testLocUnder.getBlock().getState().getData());
			}
			if (testLoc.getBlock().getType() == Material.POWERED_RAIL) {
				return ((PoweredRail) testLoc.getBlock().getState().getData());
			}
			if (testLocUnder.getBlock().getType() == Material.POWERED_RAIL) {
				return ((PoweredRail) testLocUnder.getBlock().getState().getData());
			}
		} catch (ClassCastException localClassCastException) {
		}
		return null;
	}
}
