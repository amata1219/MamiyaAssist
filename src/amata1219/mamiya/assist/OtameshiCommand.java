package amata1219.mamiya.assist;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class OtameshiCommand implements TabExecutor{

	private HashMap<String, Stopwatch> stopwatches = new HashMap<String, Stopwatch>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0){
			send(ChatColor.AQUA, sender, "MamiyaAssist /otameshi コマンド一覧");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.AQUA + "/otameshi register [name]");
			sender.sendMessage(ChatColor.WHITE + "指定した名前でStopwatchを作成し登録します。");
			sender.sendMessage(ChatColor.AQUA + "/otameshi unregister [name]");
			sender.sendMessage(ChatColor.WHITE + "指定したStopwatchを登録解除し削除します。");
			sender.sendMessage(ChatColor.AQUA + "/otameshi clear");
			sender.sendMessage(ChatColor.WHITE + "登録されているStopwatchを全て登録解除し削除します。");
			sender.sendMessage(ChatColor.AQUA + "/otameshi list");
			sender.sendMessage(ChatColor.WHITE + "登録されているStopwatchの名前を状態を全て表示します。");
			sender.sendMessage(ChatColor.AQUA + "/otameshi [name] [start/split/stop/print/reset]");
			sender.sendMessage(ChatColor.WHITE + "指定したStopwatchを操作します。詳細は[/otameshi [name]]で確認出来ます。");
			return true;
		}else if(args[0].equalsIgnoreCase("register")){
			if(args.length == 1){
				send(ChatColor.AQUA, sender, "/otameshi register [name]");
				send(ChatColor.WHITE, sender, "指定した名前でStopwatchを作成し登録します。");
				return true;
			}else{
				if(stopwatches.containsKey(args[1])){
					send(ChatColor.RED, sender, "指定された名前は既に使用されています。");
					return true;
				}
				stopwatches.put(args[1], new Stopwatch(args[1]));
				send(ChatColor.AQUA, sender, "Stopwatch(" + args[1] + ")を作成し登録しました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("unregister")){
			if(args.length == 1){
				send(ChatColor.AQUA, sender, "/otameshi unregister [name]");
				send(ChatColor.WHITE, sender, "指定した名前のStopwatchを登録解除し削除します。");
				return true;
			}else{
				if(!stopwatches.containsKey(args[1])){
					send(ChatColor.RED, sender, "指定された名前は使用されていません。");
					return true;
				}
				stopwatches.remove(args[1]);
				send(ChatColor.AQUA, sender, "Stopwatch(" + args[1] + ")を登録解除し削除しました。");
				return true;
			}
		}else if(args[0].equalsIgnoreCase("list")){
			send(ChatColor.AQUA, sender, "Stopwatch一覧");
			sender.sendMessage("");
			stopwatches.forEach((k, v) -> sender.sendMessage("・" + v.getName() + " # " + v.getState()));
			return true;
		}else if(args[0].equalsIgnoreCase("clear")){
			stopwatches.clear();
			send(ChatColor.AQUA, sender, "Stopwatchを全てクリアしました。");
			return true;
		}else if(stopwatches.containsKey(args[0])){
			if(args.length == 1){
				send(ChatColor.AQUA, sender, "MamiyaAssist /otameshi [name] コマンド一覧");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.AQUA + "/otameshi [name] start");
				sender.sendMessage(ChatColor.WHITE + "指定したStopwatchの計測を開始します。");
				sender.sendMessage(ChatColor.AQUA + "/otameshi [name] split");
				sender.sendMessage(ChatColor.WHITE + "指定したStopwatchでスピリットタイムを出力します。");
				sender.sendMessage(ChatColor.AQUA + "/otameshi [name] stop");
				sender.sendMessage(ChatColor.WHITE + "指定したStopwatchの計測を終了します。");
				sender.sendMessage(ChatColor.AQUA + "/otameshi [name] print");
				sender.sendMessage(ChatColor.WHITE + "指定したStopwatchの記録を全出力します。");
				sender.sendMessage(ChatColor.AQUA + "/otameshi [name] reset");
				sender.sendMessage(ChatColor.WHITE + "指定したStopwatchの記録をリセットします。");
				return true;
			}else if(args[1].equalsIgnoreCase("start")){
				Stopwatch s = stopwatches.get(args[0]);
				if(s.hasStart()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は既に開始されています。");
					return true;
				}else{
					s.setStart(System.currentTimeMillis());
					send(ChatColor.AQUA, sender, "Stopwatch(" + args[0] + ")の計測を開始しました。");
					return true;
				}
			}else if(args[1].equalsIgnoreCase("split")){
				Stopwatch s = stopwatches.get(args[0]);
				if(!s.hasStart()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は開始されていません。");
					return true;
				}else if(s.hasEnd()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は既に終了されています。");
					return true;
				}
				long l = System.currentTimeMillis();
				s.getSplitTimes().add(l);
				send(ChatColor.AQUA, sender, "Stopwatch(" + args[0] + ") # Split " + s.getSplitTimes().size() + " " + s.timeToString(l) + " !");
				return true;
			}else if(args[1].equalsIgnoreCase("stop")){
				Stopwatch s = stopwatches.get(args[0]);
				if(!s.hasStart()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は開始されていません。");
					return true;
				}
				if(s.hasEnd()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は既に終了されています。");
					return true;
				}else{
					s.setEnd(System.currentTimeMillis());
					send(ChatColor.AQUA, sender, "Stopwatch(" + args[0] + ")の計測を終了しました。");
					return true;
				}
			}else if(args[1].equalsIgnoreCase("print")){
				Stopwatch s = stopwatches.get(args[0]);
				if(!s.hasStart()){
					send(ChatColor.RED, sender, "Stopwatch(" + args[0] + ")の計測は開始されていません。");
					return true;
				}
				send(ChatColor.AQUA, sender, "Stopwatch(" + args[0] + ")");
				sender.sendMessage("");
				int i = 1;
				boolean b = false;
				for(Long time : s.getSplitTimes()){
					sender.sendMessage((b ? ChatColor.AQUA : ChatColor.WHITE) + String.valueOf(i) + ". " + s.timeToString(time));
					i++;
					b = !b;
				}
				sender.sendMessage("");
				sender.sendMessage((b ? ChatColor.AQUA : ChatColor.WHITE) + "End. " + s.timeToString(s.getEnd()));
				return true;
			}else if(args[1].equalsIgnoreCase("reset")){
				Stopwatch s = stopwatches.get(args[0]);
				s.setStart(0);
				s.getSplitTimes().clear();
				s.setEnd(0);
				send(ChatColor.AQUA, sender, "Stopwatch(" + s.getName() + ")をクリアしました。");
				return true;
			}
		}
		send(ChatColor.RED, sender, "入力されたコマンドが不正です。");
		return true;
	}

	private void send(ChatColor color, CommandSender sender, String s){
		sender.sendMessage(color + "MamiyaAssist > " + sender.getName() + ": " + s);
	}

}
