package amata1219.mamiya.assist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Rails;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import amata1219.easy.carts.plus.SpeedAndYaw;
import amata1219.easy.carts.plus.Utils;

public class OneClickRideListener implements Listener{

	private boolean minecartIsEnable, boatIsEnable;
	private List<String> minecartWorlds, boatWorlds;
	private int minecartMaxSpeedPercent = 400, minecartMaxPushSpeedPercent = 180, minecartPoweredRailBoostPercent = 150;
	private boolean minecartAutoBoostOnSlope = true;
	private TreeSpecies woodType;
	private final String mustRemove = "MAMIYA_ASSIST_MUST_REMOVE_ONE_CLICK_VEHICLES";

	private HashMap<UUID, Double> previousSpeed = new HashMap<UUID, Double>();
	private HashSet<UUID> slowedCarts = new HashSet<UUID>();
	private HashMap<UUID, SpeedAndYaw> stoppedCarts = new HashMap<UUID, SpeedAndYaw>();

	public OneClickRideListener(MamiyaAssist plugin){
		load(plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)return;
		if(e.isBlockInHand())return;
		if(p.isInsideVehicle() || p.isSneaking())return;
		Block b = e.getClickedBlock();
		Material m = b.getType();
		if(Utils.rails.contains(m) && minecartIsEnable){
			Material t = e.getMaterial();
			if((t != null && t != Material.AIR && t != Material.VOID_AIR && t != Material.CAVE_AIR)
					&& (t == Material.MINECART || t == Material.CHEST_MINECART || t == Material.COMMAND_BLOCK_MINECART
					|| t == Material.FURNACE_MINECART || t == Material.HOPPER_MINECART))return;
			if(minecartWorlds.contains("ALL") || minecartWorlds.contains(p.getWorld().getName())){
				Location loc = b.getLocation();
				loc.setX(loc.getBlockX() + 0.5);
				loc.setZ(loc.getBlockZ() + 0.5);
				RideableMinecart minecart = (RideableMinecart) p.getWorld().spawnEntity(loc, EntityType.MINECART);
				minecart.setMetadata(mustRemove, new FixedMetadataValue(MamiyaAssist.getPlugin(), true));
				minecart.setMaxSpeed(0.4D * minecartMaxSpeedPercent  / 100.0D);
				minecart.addPassenger(p);
				e.setCancelled(true);
			}
		}
		if((m == Material.ICE || m == Material.FROSTED_ICE || m == Material.PACKED_ICE || m == Material.BLUE_ICE) && boatIsEnable){
			Material t = e.getMaterial();
			if((t != null && t != Material.AIR && t != Material.VOID_AIR && t != Material.CAVE_AIR)
					&& (t == Material.ACACIA_BOAT || t == Material.BIRCH_BOAT || t == Material.DARK_OAK_BOAT
					|| t == Material.JUNGLE_BOAT || t == Material.OAK_BOAT || t == Material.SPRUCE_BOAT))return;
			if(!boatWorlds.contains("ALL") && !boatWorlds.contains(p.getWorld().getName()))return;
			Location loc = b.getLocation().add(0, 1, 0);
			loc.setX(loc.getBlockX() + 0.5);
			loc.setZ(loc.getBlockZ() + 0.5);
			loc.setYaw(p.getLocation().getYaw());
			Boat boat = (Boat) p.getWorld().spawnEntity(loc, EntityType.BOAT);
			boat.setMetadata(mustRemove, new FixedMetadataValue(MamiyaAssist.getPlugin(), true));
			boat.setWoodType(woodType);
			boat.addPassenger(p);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleCreate(VehicleCreateEvent e){
		Vehicle v = e.getVehicle();
		if(v instanceof RideableMinecart){
			((RideableMinecart) v).setMaxSpeed(0.4D * minecartMaxSpeedPercent  / 100.0D);
		}
	}

	@EventHandler
	public void onVehicleMove(VehicleMoveEvent e){
		Vehicle v = e.getVehicle();
		if(v instanceof RideableMinecart){
			RideableMinecart minecart = (RideableMinecart) v;
			Vector cartVelocity = minecart.getVelocity();
			UUID cartId = minecart.getUniqueId();
			Location cartLocation = minecart.getLocation();
			Block blockUnderCart = cartLocation.getBlock();
			Rails railUnderCart = null;
			try{
				railUnderCart = (Rails) blockUnderCart.getState().getData();
			}catch(ClassCastException ex){
				return;
			}
			if (!(slowedCarts.contains(cartId))){
				Location locationInFront = cartLocation.clone();
				Vector cartDirection = cartVelocity.clone().normalize();
				for (int i = 1; i < 3; ++i){
					locationInFront.add(cartDirection.multiply(i));
					Rails railInFront = Utils.getRailInFront(locationInFront);
					if (railInFront == null){
						continue;
					}
					if ((railInFront.isCurve()) || (railInFront.isOnSlope())){
						if ((railUnderCart.isOnSlope()) && (Utils.isMovingDown(e))){
							return;
						}
						previousSpeed.put(cartId, Double.valueOf(minecart.getVelocity().length()));
						slowedCarts.add(cartId);
						slowDownCart(minecart, 0.4D);
						return;
					}
					if ((cartVelocity.length() <= 1.0D) || (!(Utils.isIntersection(locationInFront, cartDirection)))){
						continue;
					}

					slowDownCart(minecart, 1.0D);
					return;
				}

			}

			if ((railUnderCart.isCurve()) || (railUnderCart.isOnSlope())){
				if ((minecartAutoBoostOnSlope) && (railUnderCart.isOnSlope())&& (Utils.isMovingUp(e))){
					minecart.setVelocity(cartVelocity.multiply(minecartMaxPushSpeedPercent));
				}
				slowedCarts.remove(cartId);
				return;
			}
			if ((!(slowedCarts.contains(cartId))) && (previousSpeed.containsKey(cartId))){
				minecart.setMaxSpeed(0.4D * minecartMaxSpeedPercent / 100.0D);
				Vector newVel = minecart.getVelocity().normalize()
						.multiply(((Double) previousSpeed.get(cartId)).doubleValue());
				minecart.setVelocity(newVel);
				previousSpeed.remove(cartId);
			}

			boostCartOnPoweredRails(minecart, blockUnderCart);

			if (Double.isNaN(cartVelocity.length())){
				return;
			}

			if ((stoppedCarts.containsKey(minecart.getUniqueId())) && (cartVelocity.lengthSquared() > 0.0D)){
				continueCartAfterIntersection(minecart);
				return;
			}
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent e){
		Vehicle v = e.getVehicle();
		UUID cartId = v.getUniqueId();
		previousSpeed.remove(cartId);
		slowedCarts.remove(cartId);
		stoppedCarts.remove(cartId);
		if(v.hasMetadata(mustRemove)){
			v.remove();
		}
	}

	public void load(MamiyaAssist plugin){
		FileConfiguration c = plugin.getCustomConfig().getConfig();
		minecartIsEnable = c.getBoolean("OneClickRide.Minecart.Enable");
		minecartWorlds = c.getStringList("OneClickRide.Minecart.Worlds");
		minecartMaxSpeedPercent = c.getInt("OneClickRide.Minecart.MaxSpeedPercent");
		minecartMaxPushSpeedPercent = c.getInt("OneClickRide.Minecart.MaxPushSpeedPercent");
		minecartPoweredRailBoostPercent = c.getInt("OneClickRide.Minecart.PoweredRailBoostPercent");
		minecartAutoBoostOnSlope = c.getBoolean("OneClickRide.Minecart.AutoBoostOnSlope");
		boatIsEnable = c.getBoolean("OneClickRide.Boat.Enable");
		boatWorlds = c.getStringList("OneClickRide.Boat.Worlds");
		woodType = TreeSpecies.valueOf(c.getString("OneClickRide.Boat.WoodType"));
	}

	private void slowDownCart(RideableMinecart cart, double maxSpeed){
		cart.setVelocity(cart.getVelocity().clone().normalize().multiply(maxSpeed));
		cart.setMaxSpeed(maxSpeed);
	}

	private void continueCartAfterIntersection(RideableMinecart cart){
		SpeedAndYaw beforeStop = (SpeedAndYaw) stoppedCarts.get(cart.getUniqueId());
		Entity firstPassenger = Utils.GetFirstPassenger(cart);

		if (firstPassenger == null){
			return;
		}
		Vector locationOffset = Utils.getUnitVectorFromYaw(firstPassenger.getLocation().getYaw());

		Location newCartLocation = cart.getLocation().clone().add(locationOffset);

		if (!Utils.rails.contains(newCartLocation.getBlock().getState().getType()))return;
		if (beforeStop.getSpeed().doubleValue() < 0.1D){
			beforeStop.setSpeed(Double.valueOf(0.1D));
		}
		cart.setVelocity(locationOffset.clone().multiply(beforeStop.getSpeed().doubleValue()));
		teleportMineCart(cart, newCartLocation, firstPassenger.getLocation(), beforeStop.getDirection());
		stoppedCarts.remove(cart.getUniqueId());
	}

	private void boostCartOnPoweredRails(RideableMinecart cart, Block blockUnderCart){
		if(blockUnderCart.getBlockPower() == 0)return;
		Vector cartVelocity = cart.getVelocity();
		Double cartSpeed = Double.valueOf(cartVelocity.length());

		boolean isPoweredBlock = blockUnderCart.getType() == Material.POWERED_RAIL;

		if ((isPoweredBlock) && (!(slowedCarts.contains(cart.getUniqueId())))){
			if(!blockUnderCart.isBlockPowered())return;
			cart.setMaxSpeed(0.4D * minecartMaxSpeedPercent / 100.0D);
			cartVelocity.multiply(minecartPoweredRailBoostPercent / 100.0D);
			cart.setVelocity(cartVelocity);
		} else{
			if (cartSpeed.doubleValue() >= 0.2D * minecartMaxPushSpeedPercent / 100.0D){
				return;
			}
			cart.setVelocity(cartVelocity.multiply(minecartMaxPushSpeedPercent / 100.0D));
		}
	}

	private void teleportMineCart(RideableMinecart cart, Location destination, Location oldPlayerLocation, Vector oldDirection){
		Vector destinationVector = new Vector(destination.getX(), destination.getY(), destination.getZ());
		destinationVector.multiply(oldDirection);

		destination.setX(new Double(destination.getX()).intValue() + ((destination.getX() >= 0.0D) ? 0.5D : -0.5D));
		destination.setZ(new Double(destination.getZ()).intValue() + ((destination.getZ() >= 0.0D) ? 0.5D : -0.5D));

		RideableMinecart toCart = (RideableMinecart) cart.getWorld().spawn(destination, RideableMinecart.class);
		Entity passenger = Utils.GetFirstPassenger(cart);
		if (passenger == null){
			return;
		}
		if (passenger != null){
			cart.eject();
			destination.setY(oldPlayerLocation.getY());
			destination.setPitch(oldPlayerLocation.getPitch());
			destination.setYaw(oldPlayerLocation.getYaw());

			passenger.teleport(destination);
			/*Bukkit.getScheduler().runTask(easyCartsPlugin, new Runnable(toCart, passenger, cart){
				public void run(){
					toCart.addPassenger(val$passenger);
					val$passenger.setVelocity(val$cart.getVelocity());
				}
			});*/
			new BukkitRunnable(){
				public void run(){
					toCart.addPassenger(passenger);
					passenger.setVelocity(cart.getVelocity());
				}
			}.runTask(MamiyaAssist.getPlugin());
		}
		toCart.getLocation().setYaw(cart.getLocation().getYaw());
		toCart.getLocation().setPitch(cart.getLocation().getPitch());
		toCart.setVelocity(cart.getVelocity());
		cart.remove();
	}

}
