package amata1219.mamiya.assist.listener;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
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

public class TemporaryBoatListener implements Listener {
	
	public static final String MAMIYA_BOTA_METADATA = "mamiya-boat";
	
	private final MamiyaAssist plugin = MamiyaAssist.getPlugin();
	
	private final Random rand = new Random();

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(!isEnabled()) return;
		
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if(event.isBlockInHand()) return;

		Player player = event.getPlayer();
		if(player.isInsideVehicle() || player.isSneaking()) return;

		if(player.hasMetadata("race") || ((Entity) player).hasMetadata("race-team")) return;

		Block block = event.getClickedBlock();
		Material clicked = block.getType();
		if(clicked == null || !clicked.name().endsWith("ICE")) return;
		
		Material hand = event.getMaterial();
		if(hand != null && hand.name().endsWith("_BOAT")) return;

		List<String> targetWorlds = targetWorlds();
		if(!targetWorlds.contains(player.getWorld().getName()) && !targetWorlds.contains("ALL")) return;

		Location loc = block.getLocation().add(0.5, 1, 0.5);
		loc.setYaw(player.getLocation().getYaw());
		
		Boat boat = (Boat) player.getWorld().spawnEntity(loc, EntityType.BOAT);
		boat.setMetadata(MAMIYA_BOTA_METADATA, new FixedMetadataValue(plugin, true));
		
		TreeSpecies[] species = TreeSpecies.values();
		boat.setWoodType(species[rand.nextInt(species.length)]);
		
		boat.addPassenger(player);
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent e){
		if(!isEnabled()) return;
		
		Vehicle vehicle = e.getVehicle();
		
		if(!(vehicle instanceof Boat)) return;
		
		if(vehicle.hasMetadata(MAMIYA_BOTA_METADATA)) vehicle.remove();
	}

	private boolean isEnabled(){
		return plugin.config().getBoolean("Temporary boat.Enabled or not");
	}
	
	private List<String> targetWorlds(){
		return plugin.config().getStringList("Temporary boat.Target worlds");
	}

}
