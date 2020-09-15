package amata1219.mamiya.assist

import amata1219.mamiya.assist.command.MamiyaCommand
import amata1219.xeflection.Reflect
import org.bukkit.Server
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class Main extends JavaPlugin {

  Main.INSTANCE = this

  val commands: Map[String, CommandExecutor] = Map(
    "mamiya" -> MamiyaCommand
  )

  override def onEnable(): Unit = {
    saveDefaultConfig()
  }

  override def onDisable(): Unit = {
    HandlerList.unregisterAll(this)
  }

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    commands.apply(label).onCommand(sender, command, label, args)
  }

  implicit class XServer(server: Server) {
    def recentTps(): Array[Double] = Reflect.on(server)
      .field("console")
      .get[Array[Double]]("recentTps")
  }

}

object Main {

  var INSTANCE: Main = _

}
