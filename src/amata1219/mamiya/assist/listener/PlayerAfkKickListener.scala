package amata1219.mamiya.assist.listener

import net.ess3.api.events.AfkStatusChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.collection.mutable

object PlayerAfkKickListener extends Listener {

  val state: mutable.HashMap[Player, Long] = mutable.HashMap()

  @EventHandler
  def on(event: AfkStatusChangeEvent): Unit = {

  }

  @EventHandler
  def on(event: PlayerQuitEvent): Unit = {
  }

}
