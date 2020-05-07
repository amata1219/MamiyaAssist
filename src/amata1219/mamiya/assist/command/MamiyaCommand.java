package amata1219.mamiya.assist.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;

import amata1219.mamiya.assist.MamiyaAssist;
import amata1219.mamiya.assist.config.Config;
import amata1219.mamiya.assist.listener.CancelBoostingElytraAtLowTPSListener;
import amata1219.mamiya.assist.task.ControlBoostingElytraTask;

public class MamiyaCommand implements CommandExecutor {

	private final MamiyaAssist plugin = MamiyaAssist.getPlugin();
	private final WorldEditPlugin we;

	public MamiyaCommand(WorldEditPlugin we){
		this.we = we;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0){
			send(ChatColor.AQUA, sender, "MamiyaAssist Info");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.WHITE + "Spigot " + plugin.getDescription().getAPIVersion());
			sender.sendMessage(ChatColor.WHITE + "MamiyaAssist " + plugin.getDescription().getVersion());
			sender.sendMessage("");
			sender.sendMessage(ChatColor.WHITE + "/mamiya commands で /mamiya コマンド一覧を表示");
			return true;
		}else if(args[0].equalsIgnoreCase("commands")){
			send(ChatColor.AQUA, sender, "MamiyaAssist /mamiya コマンド一覧");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "/mamiya commands");
			sender.sendMessage(ChatColor.WHITE + "[/mamiya]コマンド一覧を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya reload");
			sender.sendMessage(ChatColor.WHITE + "[MamiyaAssist]のコンフィグを再読み込みします。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster enable [true/false]");
			sender.sendMessage(ChatColor.WHITE + "エリトラブースターの使用制限リスナーを有効にするか設定します。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster interval [second]");
			sender.sendMessage(ChatColor.WHITE + "エリトラブースターのTPS確認間隔を設定します。単位は秒です。値は半角数字で入力して下さい。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster always [true/false]");
			sender.sendMessage(ChatColor.WHITE + "エリトラブースターの使用制限をTPSにかかわらず有効にするか設定します。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster worlds [add/remove/clear] [world/ALL]");
			sender.sendMessage(ChatColor.WHITE + "エリトラブースターの使用制限を有効にするワールドを設定します。clearを指定する場合、ワールド名の指定は必要ありません。ALLを指定すると全ワールドが対象になります。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster threshold [tps]");
			sender.sendMessage(ChatColor.WHITE + "エリトラブースターの使用制限目安となるTPSしきい値を設定します。値は半角数字で入力して下さい。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya booster message [start/end/useCancel] [text]");
			sender.sendMessage(ChatColor.WHITE + "各タイミングでのメッセージを設定します。startはTPSがしきい値を下回った時、endはTPSがしきい値を上回った時、useCancelはエリトラブースターの使用が制限された時になります。");
			return true;
		}else if(args[0].equalsIgnoreCase("reload")){
			plugin.getCustomConfig().reloadConfig();
			CancelBoostingElytraAtLowTPSListener listener = plugin.getElytraBoosterListener();
			if(listener != null){
				FileConfiguration c = plugin.getCustomConfig().config();
				listener.setAlways(c.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS"));
				listener.setWorlds(c.getStringList("Restriction on elytra boosts by fireworks.Target worlds"));
				listener.setTpsThreshold(c.getInt("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies"));
				listener.setStartMessage(c.getString("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction"));
				listener.setEndMessage(c.getString("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction"));
				listener.setUseCancelMessage(c.getString("Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting"));
				listener.getElytraBoosterTask().cancel();
				ControlBoostingElytraTask elytraBoosterTask = new ControlBoostingElytraTask(listener);
				elytraBoosterTask.runTaskTimer(plugin, 0, c.getLong("Restriction on elytra boosts by fireworks.Messaging intervals"));
				listener.setElytraBoosterTask(elytraBoosterTask);
			}
			plugin.getOneClickRideListener().load(plugin);
			send(ChatColor.AQUA, sender, "[MamiyaAssist]のコンフィグを再読み込みしました。");
			return true;
		}else if(args[0].equalsIgnoreCase("booster")){
			CancelBoostingElytraAtLowTPSListener listener = plugin.getElytraBoosterListener();
			boolean n = listener == null ? true : false;
			Config config = plugin.getCustomConfig();
			FileConfiguration c = config.config();
			if(args.length == 1){

			}else if(args[1].equalsIgnoreCase("enable")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster enable [true/false]");
					send(ChatColor.WHITE, sender, "エリトラブースターの使用制限リスナーを有効にするか設定します。");
					return true;
				}else if(args[2].equalsIgnoreCase("true")){
					if(n){
						if(c.getBoolean("Restriction on elytra boosts by fireworks.Enabled or not")){
							send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に有効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Enabled or not", true);
						config.updateConfig();
						CancelBoostingElytraAtLowTPSListener elytraBoosterListener = new CancelBoostingElytraAtLowTPSListener(plugin);
						plugin.setElytraBoosterListener(elytraBoosterListener);
						plugin.getServer().getPluginManager().registerEvents(elytraBoosterListener, plugin);
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Enabled or not]を[true]に設定しました。");
						return true;
					}else{
						send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に有効です。");
						return true;
					}
				}else if(args[2].equalsIgnoreCase("false")){
					if(!n){
						if(!c.getBoolean("Restriction on elytra boosts by fireworks.Enabled or not")){
							send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に無効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Enabled or not", false);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Enabled or not]を[false]に設定しました。");
						send(ChatColor.RED, sender, "値を適用するにはサーバーを再起動して下さい。");
						return true;
					}else{
						send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に無効です。");
						return true;
					}
				}
			}else if(args[1].equalsIgnoreCase("interval")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster interval [second]");
					send(ChatColor.WHITE, sender, "エリトラブースターのTPS確認間隔を設定します。単位は秒です。値は半角数字で入力して下さい。");
					return true;
				}else{
					int i;
					try{
						i = Integer.valueOf(args[2]);
					}catch(NumberFormatException e){
						send(ChatColor.RED, sender, "値は半角数字で指定して下さい。");
						return true;
					}
					if(n){
						c.set("Restriction on elytra boosts by fireworks.Messaging intervals", i);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Messaging intervals]を[" + i + "]に設定しました。");
						return true;
					}else{
						c.set("Restriction on elytra boosts by fireworks.Messaging intervals", i);
						config.updateConfig();
						listener.getElytraBoosterTask().cancel();
						ControlBoostingElytraTask task = new ControlBoostingElytraTask(listener);
						task.runTaskTimer(plugin, 0, i);
						listener.setElytraBoosterTask(task);
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Messaging intervals]を[" + i + "]に設定しました。");
						return true;
					}
				}
			}else if(args[1].equalsIgnoreCase("always")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster always [true/false]");
					send(ChatColor.WHITE, sender, "エリトラブースターの使用制限をTPSにかかわらず有効にするか設定します。");
					return true;
				}else if(args[2].equalsIgnoreCase("true")){
					if(n){
						if(c.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に有効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS", true);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Applied or not regardless of TPS]を[true]に設定しました。");
						return true;
					}else{
						if(c.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に有効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS", true);
						config.updateConfig();
						listener.setAlways(true);
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Applied or not regardless of TPS]を[true]に設定しました。");
						return true;
					}
				}else if(args[2].equalsIgnoreCase("false")){
					if(n){
						if(!c.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に無効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS", false);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Applied or not regardless of TPS]を[false]に設定しました。");
						return true;
					}else{
						if(!c.getBoolean("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に無効です。");
							return true;
						}
						c.set("Restriction on elytra boosts by fireworks.Applied or not regardless of TPS", false);
						config.updateConfig();
						listener.setAlways(false);
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Applied or not regardless of TPS]を[false]に設定しました。");
						return true;
					}
				}
			}else if(args[1].equalsIgnoreCase("worlds")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster worlds [add/remove/clear] [world/ALL]");
					send(ChatColor.WHITE, sender, "エリトラブースターの使用制限を有効にするワールドを設定します。clearを指定する場合、ワールド名の指定は必要ありません。ALLを指定すると全ワールドが対象になります。");
					return true;
				}else if(args[2].equalsIgnoreCase("add")){
					if(args.length == 3){
						send(ChatColor.AQUA, sender, "/mamiya booster worlds add [world/ALL]");
						send(ChatColor.WHITE, sender, "エリトラブースターの使用制限を有効にするワールドを追加します。ALLを指定すると全ワールドが対象になります。");
						return true;
					}else{
						if(n){
							List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
							if(worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは既に追加されています。");
								return true;
							}
							worlds.add(args[3]);
							c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]に[" + args[3] + "]を追加しました。");
							return true;
						}else{
							List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
							if(worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは既に追加されています。");
								return true;
							}
							worlds.add(args[3]);
							c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
							config.updateConfig();
							listener.getWorlds().add(args[3]);
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]に[" + args[3] + "]を追加しました。");
							return true;
						}
					}
				}else if(args[2].equalsIgnoreCase("remove")){
					if(args.length == 3){
						send(ChatColor.AQUA, sender, "/mamiya booster worlds remove [world/ALL]");
						send(ChatColor.WHITE, sender, "エリトラブースターの使用制限を有効にするワールドを追加します。ALLを指定すると全ワールドが対象になります。");
						return true;
					}else{
						if(n){
							List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
							if(!worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは追加されていません。");
								return true;
							}
							worlds.remove(args[3]);
							c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]から[" + args[3] + "]を削除しました。");
							return true;
						}else{
							List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
							if(!worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは追加されていません。");
								return true;
							}
							worlds.remove(args[3]);
							c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
							config.updateConfig();
							listener.getWorlds().remove(args[3]);
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]から[" + args[3] + "]を削除しました。");
							return true;
						}
					}
				}else if(args[2].equalsIgnoreCase("clear")){
					if(n){
						List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
						worlds.clear();
						c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]をクリアしました。");
						return true;
					}else{
						List<String> worlds = c.getStringList("Restriction on elytra boosts by fireworks.Target worlds");
						worlds.clear();
						c.set("Restriction on elytra boosts by fireworks.Target worlds", worlds);
						config.updateConfig();
						listener.getWorlds().clear();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Target worlds]をクリアしました。");
						return true;
					}
				}
			}else if(args[1].equalsIgnoreCase("threshold")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster threshold [tps]");
					send(ChatColor.WHITE, sender, "エリトラブースターの使用制限目安となるTPSしきい値を設定します。値は半角数字で入力して下さい。");
					return true;
				}else{
					int i;
					try{
						i = Integer.valueOf(args[2]);
					}catch(NumberFormatException e){
						send(ChatColor.RED, sender, "値は半角数字で指定して下さい。");
						return true;
					}
					if(n){
						c.set("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies", i);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies]を[" + i + "]に設定しました。");
						return true;
					}else{
						c.set("Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies", i);
						config.updateConfig();
						listener.setTpsThreshold(i);
						send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.TPS Threshold to which the restriction applies]を[" + i + "]に設定しました。");
						return true;
					}
				}
			}else if(args[1].equalsIgnoreCase("message")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster message [start/end/useCancel] [text]");
					send(ChatColor.WHITE, sender, "各タイミングでのメッセージを設定します。startはTPSがしきい値を下回った時、endはTPSがしきい値を上回った時、useCancelはエリトラブースターの使用が制限された時になります。");
					return true;
				}else if(args[2].equalsIgnoreCase("start")){
					if(args.length == 3){
						send(ChatColor.AQUA, sender, "/mamiya booster start [text]");
						send(ChatColor.WHITE, sender, "TPSがしきい値を下回った時に表示されるメッセージを設定します。");
						return true;
					}else{
						StringBuilder sb = new StringBuilder();
						for(int i = 3; i < args.length; i++){
							sb.append(" " + args[i]);
						}
						String s = sb.toString().trim();
						if(n){
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction", s);
							config.updateConfig();
							listener.setStartMessage(s);
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin started appling the restriction]を[" + s + "]に設定しました。");
							return true;
						}
					}
				}else if(args[2].equalsIgnoreCase("end")){
					if(args.length == 3){
						send(ChatColor.AQUA, sender, "/mamiya booster end [text]");
						send(ChatColor.WHITE, sender, "TPSがしきい値を上回った時に表示されるメッセージを設定します。");
						return true;
					}else{
						StringBuilder sb = new StringBuilder();
						for(int i = 3; i < args.length; i++){
							sb.append(" " + args[i]);
						}
						String s = sb.toString().trim();
						if(n){
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction", s);
							config.updateConfig();
							listener.setEndMessage(s);
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin stopped appling the restriction]を[" + s + "]に設定しました。");
							return true;
						}
					}
				}else if(args[2].equalsIgnoreCase("useCancel")){
					if(args.length == 3){
						send(ChatColor.AQUA, sender, "/mamiya booster useCancel [text]");
						send(ChatColor.WHITE, sender, "エリトラブースターの使用が制限された時に表示されるメッセージを設定します。");
						return true;
					}else{
						StringBuilder sb = new StringBuilder();
						for(int i = 3; i < args.length; i++){
							sb.append(" " + args[i]);
						}
						String s = sb.toString().trim();
						if(n){
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting", s);
							config.updateConfig();
							listener.setUseCancelMessage(s);
							send(ChatColor.AQUA, sender, "[Restriction on elytra boosts by fireworks.Message.When the plugin blocked elytra boosting]を[" + s + "]に設定しました。");
							return true;
						}
					}
				}
			}
		}else if(args[0].equalsIgnoreCase("regen")){
			if(!(sender instanceof Player)){
				send(ChatColor.RED, sender, "ゲーム内から実行してください。");
				return true;
			}

			if(we == null){
				send(ChatColor.RED, sender, "WorldEditが読み込まれていません。");
				return true;
			}

			Player player = (Player) sender;
			LocalSession session = we.getSession(player);
			//WorldEdit.getInstance().getEditSessionFactory().getEditSession(player.getWorld(), WE_LIMIT, player);
			if(session == null){
				send(ChatColor.RED, sender, "範囲を指定して下さい。");
				return true;
			}

			RegionSelector selector = session.getRegionSelector(session.getSelectionWorld());
			if(selector == null){
				send(ChatColor.RED, sender, "範囲を指定して下さい。");
				return true;
			}

			Region region = selector.getIncompleteRegion();
			if(region == null){
				send(ChatColor.RED, sender, "範囲を指定して下さい。");
				return true;
			}

			if(region.getMinimumPoint() == null || region.getMaximumPoint() == null){
				send(ChatColor.RED, sender, "範囲を指定して下さい。");
				return true;
			}

			int volume = region.getWidth() * region.getHeight() * region.getLength();
			if(volume > plugin.config().getInt("Regeneration of regions.Maximum number of blocks that can be regenerated")){
				send(ChatColor.RED, sender, "指定された範囲が大きすぎます(" + volume + ")。上限は" + limit + "ブロックです。");
				return true;
			}

			//BukkitPlayer user = we.wrapPlayer(player);
			
			World origin = Bukkit.getWorld(plugin.config().getString("Regeneration of regions.Origin world"));

			region.setWorld(BukkitAdapter.adapt(origin));
			/*BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
			try {
				clipboard.setOrigin(session.getPlacementPosition(user));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
			}
			EditSession editSession = session.createEditSession(user);
			ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
			copy.setCopyingEntities(true);
			try {
				Operations.completeLegacy(copy);
			} catch (MaxChangedBlocksException e) {
				e.printStackTrace();
			}
			session.setClipboard(new ClipboardHolder(clipboard));

			ClipboardHolder holder = null;
			try {
				holder = session.getClipboard();
			} catch (EmptyClipboardException e1) {
				e1.printStackTrace();
			}
			//Clipboard clipboard = holder.getClipboard();
			//Region region = clipboard.getRegion();
			BlockVector3 to = clipboard.getOrigin();//atOrigin ? clipboard.getOrigin() : session.getPlacementPosition(player);
			Operation operation = holder.createPaste(editSession).to(to).ignoreAirBlocks(false).build();
			try {
				Operations.completeLegacy(operation);
			} catch (MaxChangedBlocksException e) {
				e.printStackTrace();
			}*/

			Bukkit.dispatchCommand(player, "/copy");
			Bukkit.dispatchCommand(player, "/paste");

			send(ChatColor.AQUA, sender, "指定された範囲を再生成しました。");
			return true;
		}
		send(ChatColor.RED, sender, "入力されたコマンドが不正です。");
		return true;
	}

	private void send(ChatColor color, CommandSender sender, String s){
		sender.sendMessage(color + "MamiyaAssist > " + sender.getName() + ": " + s);
	}

}
