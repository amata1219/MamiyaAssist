package amata1219.mamiya.assist.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import amata1219.mamiya.assist.MamiyaAssist;

public class ClickToSummonBoatListener implements Listener {
	
	public static final String MAMIYA_BOTA_METADATA = "mamiya-boat";

	private boolean  enable;
	//private boolean water;
	private List<String> worlds;
	private TreeSpecies woodType;

	private final HashSet<Material> materials = new HashSet<>(
				Arrays.asList(
					Material.ICE,
					Material.FROSTED_ICE,
					Material.PACKED_ICE,
					Material.BLUE_ICE
				)
			);
	
	private final HashSet<Material> handMaterials = new HashSet<>(
				Arrays.asList(
					Material.ACACIA_BOAT,
					Material.BIRCH_BOAT,
					Material.DARK_OAK_BOAT,
					Material.JUNGLE_BOAT,
					Material.OAK_BOAT,
					Material.SPRUCE_BOAT
				)
			);

	public ClickToSummonBoatListener(MamiyaAssist plugin){
		load(plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if(event.isBlockInHand()) return;

		Player player = event.getPlayer();
		if(player.isInsideVehicle() || player.isSneaking()) return;

		if(player.hasMetadata("race") || ((Entity) player).hasMetadata("race-team")) return;

		Block block = event.getClickedBlock();
		if(!materials.contains(block.getType()) || !enable) return;
		//if(!(materials.contains(block.getType()) || isClickedWater(player)) || !enable) return;
		
		Material type = event.getMaterial();
		if((type != null && type != Material.AIR && type != Material.VOID_AIR && type != Material.CAVE_AIR) && handMaterials.contains(type))
			return;

		if(!worlds.contains(player.getWorld().getName()) && !worlds.contains("ALL")) return;

		Location loc = block.getLocation().add(0, 1, 0);
		loc.setX(loc.getBlockX() + 0.5);
		loc.setZ(loc.getBlockZ() + 0.5);
		loc.setYaw(player.getLocation().getYaw());
		
		Boat boat = (Boat) player.getWorld().spawnEntity(loc, EntityType.BOAT);
		boat.setMetadata(MAMIYA_BOTA_METADATA, new FixedMetadataValue(MamiyaAssist.getPlugin(), true));
		boat.setWoodType(woodType);
		boat.addPassenger(player);
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent e){
		Vehicle vehicle = e.getVehicle();
		
		if(!(vehicle instanceof Boat)) return;
		
		if(vehicle.hasMetadata(MAMIYA_BOTA_METADATA)) vehicle.remove();
	}

	public void load(MamiyaAssist plugin){
		FileConfiguration config = plugin.getCustomConfig().config();
		enable = config.getBoolean("Temporary boat.Enabled or not");
		//water = config.getBoolean("Temporary boat.Water");
		worlds = config.getStringList("Temporary boat.Target worlds");
		woodType = TreeSpecies.valueOf(config.getString("Temporary boat.Wood type"));
	}

	/*public boolean isClickedWater(Player player){
		if(!water)
			return false;

		EntityHuman entityhuman = ((CraftPlayer) player).getHandle();
		World world = entityhuman.getWorld();
		float f1 = entityhuman.lastPitch + (entityhuman.pitch - entityhuman.lastPitch) * 1.0F;
		float f2 = entityhuman.lastYaw + (entityhuman.yaw - entityhuman.lastYaw) * 1.0F;
		double d0 = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * 1.0D;
		double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * 1.0D
				+ (double) entityhuman.getHeadHeight();
		double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * 1.0D;
		Vec3D vec3d = new Vec3D(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		Vec3D vec3d1 = vec3d.add((double) f7 * 5.0D, (double) f6 * 5.0D, (double) f8 * 5.0D);
		MovingObjectPosition movingobjectposition = world.rayTrace(vec3d, vec3d1, FluidCollisionOption.ANY);
		if (movingobjectposition == null) {
			return false;
		} else {
			Vec3D vec3d2 = entityhuman.f(1.0F);
			boolean flag = false;
			List<net.minecraft.server.v1_15_R1.Entity> list = world.getEntities(entityhuman,
					entityhuman.getBoundingBox().b(vec3d2.x * 5.0D, vec3d2.y * 5.0D, vec3d2.z * 5.0D).g(1.0D));

			for (int event = 0; event < list.size(); ++event) {
				net.minecraft.server.v1_15_R1.Entity blockposition = (net.minecraft.server.v1_15_R1.Entity) list.get(event);
				if (blockposition.isInteractable()) {
					AxisAlignedBB entityboat = blockposition.getBoundingBox().g((double) blockposition.aM());
					if (entityboat.b(vec3d)) {
						flag = true;
					}
				}
			}

			if (flag) {
				return false;
			} else if (movingobjectposition.type == EnumMovingObjectType.BLOCK) {
				return true;
			}else {
				return false;
			}
		}
	}*/

}
