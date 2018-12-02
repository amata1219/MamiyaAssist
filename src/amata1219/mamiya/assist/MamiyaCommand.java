package amata1219.mamiya.assist;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;

public class MamiyaCommand implements TabExecutor{

	private MamiyaAssist plugin;

	public MamiyaCommand(MamiyaAssist plugin){
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
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
			sender.sendMessage(ChatColor.WHITE + "/otameshi で /otameshi コマンド一覧を表示");
			return true;
		}else if(args[0].equalsIgnoreCase("commands")){
			send(ChatColor.AQUA, sender, "MamiyaAssist /mamiya コマンド一覧");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "/mamiya commands");
			sender.sendMessage(ChatColor.WHITE + "[/mamiya]コマンド一覧を表示します。");
			sender.sendMessage(ChatColor.AQUA + "/mamiya reload");
			sender.sendMessage(ChatColor.WHITE + "[MamiyaAssist]のコンフィグを表示します。");
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
		}else if(args[0].equalsIgnoreCase("debug")){
			double[] tps = plugin.getElytraBoosterListener().getRecentTps();
			sender.sendMessage(tps[0] + ", " + tps[1] + ", " + tps[2]);
			return true;
		}else if(args[0].equalsIgnoreCase("reload")){
			plugin.getCustomConfig().reloadConfig();
			ElytraBoosterListener listener = plugin.getElytraBoosterListener();
			if(listener != null){
				FileConfiguration c = plugin.getCustomConfig().getConfig();
				listener.setAlways(c.getBoolean("ElytraBoosterUsageRestriction.Always"));
				listener.setWorlds(c.getStringList("ElytraBoosterUsageRestriction.Worlds"));
				listener.setTpsThreshold(c.getInt("ElytraBoosterUsageRestriction.TPSThreshold"));
				listener.setStartMessage(c.getString("ElytraBoosterUsageRestriction.Message.Start"));
				listener.setEndMessage(c.getString("ElytraBoosterUsageRestriction.Message.End"));
				listener.setUseCancelMessage(c.getString("ElytraBoosterUsageRestriction.Message.UseCancel"));
				listener.getElytraBoosterTask().cancel();
				ElytraBoosterTask elytraBoosterTask = new ElytraBoosterTask(listener);
				elytraBoosterTask.runTaskTimer(plugin, 0, c.getLong("ElytraBoosterUsageRestriction.MessageTaskInterval"));
				listener.setElytraBoosterTask(elytraBoosterTask);
			}
			plugin.getOneClickRideListener().load(plugin);
			send(ChatColor.AQUA, sender, "[MamiyaAssist]のコンフィグを再読み込みしました。");
			return true;
		}else if(args[0].equalsIgnoreCase("booster")){
			ElytraBoosterListener listener = plugin.getElytraBoosterListener();
			boolean n = listener == null ? true : false;
			CustomConfig config = plugin.getCustomConfig();
			FileConfiguration c = config.getConfig();
			if(args.length == 1){

			}else if(args[1].equalsIgnoreCase("enable")){
				if(args.length == 2){
					send(ChatColor.AQUA, sender, "/mamiya booster enable [true/false]");
					send(ChatColor.WHITE, sender, "エリトラブースターの使用制限リスナーを有効にするか設定します。");
					return true;
				}else if(args[2].equalsIgnoreCase("true")){
					if(n){
						if(c.getBoolean("ElytraBoosterUsageRestriction.Enable")){
							send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に有効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Enable", true);
						config.updateConfig();
						ElytraBoosterListener elytraBoosterListener = new ElytraBoosterListener(plugin);
						plugin.setElytraBoosterListener(elytraBoosterListener);
						plugin.getServer().getPluginManager().registerEvents(elytraBoosterListener, plugin);
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Enable]を[true]に設定しました。");
						return true;
					}else{
						send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に有効です。");
						return true;
					}
				}else if(args[2].equalsIgnoreCase("false")){
					if(!n){
						if(!c.getBoolean("ElytraBoosterUsageRestriction.Enable")){
							send(ChatColor.RED, sender, "エリトラブースターの使用制限リスナーは既に無効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Enable", false);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Enable]を[false]に設定しました。");
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
						c.set("ElytraBoosterUsageRestriction.MessageTaskInterval", i);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.MessageTaskInterval]を[" + i + "]に設定しました。");
						return true;
					}else{
						c.set("ElytraBoosterUsageRestriction.MessageTaskInterval", i);
						config.updateConfig();
						listener.getElytraBoosterTask().cancel();
						ElytraBoosterTask task = new ElytraBoosterTask(listener);
						task.runTaskTimer(plugin, 0, i);
						listener.setElytraBoosterTask(task);
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.MessageTaskInterval]を[" + i + "]に設定しました。");
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
						if(c.getBoolean("ElytraBoosterUsageRestriction.Always")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に有効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Always", true);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Always]を[true]に設定しました。");
						return true;
					}else{
						if(c.getBoolean("ElytraBoosterUsageRestriction.Always")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に有効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Always", true);
						config.updateConfig();
						listener.setAlways(true);
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Always]を[true]に設定しました。");
						return true;
					}
				}else if(args[2].equalsIgnoreCase("false")){
					if(n){
						if(!c.getBoolean("ElytraBoosterUsageRestriction.Always")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に無効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Always", false);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Always]を[false]に設定しました。");
						return true;
					}else{
						if(!c.getBoolean("ElytraBoosterUsageRestriction.Always")){
							send(ChatColor.RED, sender, "エリトラブースターの常時使用制限は既に無効です。");
							return true;
						}
						c.set("ElytraBoosterUsageRestriction.Always", false);
						config.updateConfig();
						listener.setAlways(false);
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Always]を[false]に設定しました。");
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
							List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
							if(worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは既に追加されています。");
								return true;
							}
							worlds.add(args[3]);
							c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]に[" + args[3] + "]を追加しました。");
							return true;
						}else{
							List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
							if(worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは既に追加されています。");
								return true;
							}
							worlds.add(args[3]);
							c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
							config.updateConfig();
							listener.getWorlds().add(args[3]);
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]に[" + args[3] + "]を追加しました。");
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
							List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
							if(!worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは追加されていません。");
								return true;
							}
							worlds.remove(args[3]);
							c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]から[" + args[3] + "]を削除しました。");
							return true;
						}else{
							List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
							if(!worlds.contains(args[3])){
								send(ChatColor.RED, sender, "指定されたワールドは追加されていません。");
								return true;
							}
							worlds.remove(args[3]);
							c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
							config.updateConfig();
							listener.getWorlds().remove(args[3]);
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]から[" + args[3] + "]を削除しました。");
							return true;
						}
					}
				}else if(args[2].equalsIgnoreCase("clear")){
					if(n){
						List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
						worlds.clear();
						c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]をクリアしました。");
						return true;
					}else{
						List<String> worlds = c.getStringList("ElytraBoosterUsageRestriction.Worlds");
						worlds.clear();
						c.set("ElytraBoosterUsageRestriction.Worlds", worlds);
						config.updateConfig();
						listener.getWorlds().clear();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Worlds]をクリアしました。");
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
						c.set("ElytraBoosterUsageRestriction.TPSThreshold", i);
						config.updateConfig();
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.TPSThreshold]を[" + i + "]に設定しました。");
						return true;
					}else{
						c.set("ElytraBoosterUsageRestriction.TPSThreshold", i);
						config.updateConfig();
						listener.setTpsThreshold(i);
						send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.TPSThreshold]を[" + i + "]に設定しました。");
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
							c.set("ElytraBoosterUsageRestriction.Message.Start", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.Start]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("ElytraBoosterUsageRestriction.Message.Start", s);
							config.updateConfig();
							listener.setStartMessage(s);
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.Start]を[" + s + "]に設定しました。");
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
							c.set("ElytraBoosterUsageRestriction.Message.End", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.End]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("ElytraBoosterUsageRestriction.Message.End", s);
							config.updateConfig();
							listener.setEndMessage(s);
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.End]を[" + s + "]に設定しました。");
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
							c.set("ElytraBoosterUsageRestriction.Message.UseCancel", s);
							config.updateConfig();
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.UseCancel]を[" + s + "]に設定しました。");
							return true;
						}else{
							c.set("ElytraBoosterUsageRestriction.Message.UseCancel", s);
							config.updateConfig();
							listener.setUseCancelMessage(s);
							send(ChatColor.AQUA, sender, "[ElytraBoosterUsageRestriction.Message.UseCancel]を[" + s + "]に設定しました。");
							return true;
						}
					}
				}
			}
		}else if(args[0].equalsIgnoreCase("minecart")){
			send(ChatColor.RED, sender, "未実装です。");
			return true;
		}else if(args[0].equalsIgnoreCase("boat")){
			send(ChatColor.RED, sender, "未実装です。");
			return true;
		}
		send(ChatColor.RED, sender, "入力されたコマンドが不正です。");
		return true;
	}

	private void send(ChatColor color, CommandSender sender, String s){
		sender.sendMessage(color + "MamiyaAssist > " + sender.getName() + ": " + s);
	}

}
