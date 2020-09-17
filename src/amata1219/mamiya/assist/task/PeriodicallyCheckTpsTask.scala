package amata1219.mamiya.assist.task

import amata1219.mamiya.assist.Main
import amata1219.xeflection.Reflect
import org.bukkit.{ChatColor, Server}
import org.bukkit.scheduler.BukkitRunnable

abstract class PeriodicallyCheckTpsTask() extends BukkitRunnable {

  var isLessThanOrEqualToThreshold: Boolean = false

  override def run(): Unit = {
    val server: Server = Main.INSTANCE.getServer
    val tps: Int = server.recentTps().apply(0).toInt
    if (isLessThanOrEqualToThreshold) {
      if (tps <= tpsThreshold()) return
      val message: String = ChatColor.translateAlternateColorCodes('&', message2End())
      server.broadcastMessage(message)
      isLessThanOrEqualToThreshold = false
    } else {
      if (tps > tpsThreshold()) return
      val message: String = ChatColor.translateAlternateColorCodes('&', message2Start())
      server.broadcastMessage(message)
      isLessThanOrEqualToThreshold = true
    }
  }

  def tpsThreshold(): Int

  def message2Start(): String

  def message2End(): String

  implicit class XServer(server: Server) {
    def recentTps(): Array[Double] = Reflect.on(server)
      .field("console")
      .get[Array[Double]]("recentTps")
  }

}
