package zbv5.cn.XiaoYum.util;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import zbv5.cn.XiaoYum.Main;

import java.util.List;
import java.util.Map;

public class PrintUtil
{
    public static void PrintConsole(String s)
    {
        Main.getInstance().getServer().getConsoleSender().sendMessage(cc(s));
    }

    public static void PrintPlayer(Player p, String s)
    {
        p.sendMessage(cc(s));
    }
    public static void Broadcast(String s)
    {
        Main.getInstance().getServer().broadcastMessage(cc(s));
    }

    public static String cc(String s)
    {
         s = s.replace("{prefix}","&6[&bXiaoYum&6]");
        s = TextFormat.colorize('&', s);
        return s;
    }

    public static void PrintCommandSender(CommandSender sender, String s)
    {
        sender.sendMessage(cc(s));
    }

    public static void PrintCommandSenderEntry(CommandSender sender, String s, Map<String, Object> map,boolean list,String key)
    {
        String entry = getEntry(s,map,list,key);
        if(entry != null)
        {
            PrintCommandSender(sender,entry);
        }
    }

    public static String getEntry(String s, Map<String, Object> map,boolean list,String key)
    {

        if(list)
        {
            List<String> object = (List<String>) map.get(key);
            if (object != null)
            {
                String message = "";
                int i = 0;
                for (String ss : object) {
                    if(i == 0)
                    {
                        message = ss;
                    } else {
                        message = message+ ",";
                    }
                    i ++;
                }
                return s+message;
            }
        } else {
            Object object = map.get(key);
            if (object != null) {
                return s+object.toString();
            }
        }
        return null;
    }
}
