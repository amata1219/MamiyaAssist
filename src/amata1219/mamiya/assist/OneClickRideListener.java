package amata1219.mamiya.assist;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OneClickRideListener implements Listener{

	private boolean minecartIsEnable, boatIsEnable;
	private List<String> minecartWorlds, boatWorlds;
	private TreeSpecies woodType;

	public OneClickRideListener(MamiyaAssist plugin){
		FileConfiguration c = plugin.getCustomConfig().getConfig();
		minecartIsEnable = c.getBoolean("OneClickRide.Minecart.Enable");
		minecartWorlds = c.getStringList("OneClickRide.Minecart.Worlds");
		boatIsEnable = c.getBoolean("OneClickRide.Boat.Enable");
		boatWorlds = c.getStringList("OneClickRide.Boat.Worlds");
		woodType = TreeSpecies.valueOf(c.getString("OneClickRide.Boat.WoodType"));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)return;
		if(e.isBlockInHand())return;
		Player p = e.getPlayer();
		if(p.isInsideVehicle() || p.isSneaking())return;
		Block b = e.getClickedBlock();
		if(b == null)return;
		Material m = b.getType();
		if((m == Material.RAIL || m == Material.ACTIVATOR_RAIL || m == Material.DETECTOR_RAIL || m == Material.POWERED_RAIL) && minecartIsEnable){
			Material t = e.getMaterial();
			if((t != null && t != Material.AIR && t != Material.VOID_AIR && t != Material.CAVE_AIR)
					&&(t == Material.MINECART || t == Material.CHEST_MINECART || t == Material.COMMAND_BLOCK_MINECART
					|| t == Material.FURNACE_MINECART || t == Material.HOPPER_MINECART))return;
			if(minecartWorlds.contains("ALL") || minecartWorlds.contains(p.getWorld().getName()))p.getWorld().spawnEntity(b.getLocation(), EntityType.MINECART).addPassenger(p);
		}else if((m == Material.ICE || m == Material.FROSTED_ICE || m == Material.PACKED_ICE || m == Material.BLUE_ICE) && boatIsEnable){
			Material t = e.getMaterial();
			if((t != null && t != Material.AIR && t != Material.VOID_AIR && t != Material.CAVE_AIR)
					&& (t == Material.ACACIA_BOAT || t == Material.BIRCH_BOAT || t == Material.DARK_OAK_BOAT
					|| t == Material.JUNGLE_BOAT || t == Material.OAK_BOAT || t == Material.SPRUCE_BOAT))return;
			if(!boatWorlds.contains("ALL") && !boatWorlds.contains(p.getWorld().getName()))return;
			Location loc = b.getLocation().add(0, 1, 0);
			loc.setYaw(p.getLocation().getYaw());
			Boat boat = (Boat) p.getWorld().spawnEntity(loc, EntityType.BOAT);
			boat.setWoodType(woodType);
			boat.addPassenger(p);
		}
	}

}
