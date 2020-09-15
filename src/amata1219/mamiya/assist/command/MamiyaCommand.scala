package amata1219.mamiya.assist.command

import amata1219.mamiya.assist.Main
import org.bukkit.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.plugin.PluginDescriptionFile

object MamiyaCommand extends CommandExecutor {

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if (args.isEmpty) {
      val plugin: PluginDescriptionFile = Main.INSTANCE.getDescription
      sender.sendMessage(s"${ChatColor.AQUA}MamiyaAssist - v${plugin.getVersion}")
      sender.sendMessage(s"${ChatColor.AQUA}* Java Development Kit: 1.8.0_261")
      sender.sendMessage(s"${ChatColor.AQUA}* Scala Development Kit: 2.13.3")
      sender.sendMessage(s"${ChatColor.AQUA}* Spigot: v${plugin.getAPIVersion}")
      sender.sendMessage(s"${ChatColor.AQUA}* EssentialsX: v.2.18.0.0")
      sender.sendMessage(s"${ChatColor.AQUA}* Xeflection: 1.0.0")
      return true
    }

    args.apply(0) match {
      case "reload" =>
        Main.INSTANCE.reloadConfig()
        sender.sendMessage(s"${ChatColor.AQUA}コンフィグをリロードしました。")
      case _ =>
    }
    true
  }

}
