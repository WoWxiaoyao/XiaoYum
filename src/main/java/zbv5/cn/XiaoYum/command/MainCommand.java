package zbv5.cn.XiaoYum.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.Plugin;
import zbv5.cn.XiaoYum.Main;
import zbv5.cn.XiaoYum.util.PluginUtil;
import zbv5.cn.XiaoYum.util.PrintUtil;
import zbv5.cn.XiaoYum.util.Utils;
import zbv5.cn.XiaoYum.windows.Window;

import java.util.Iterator;
import java.util.Map;

public class MainCommand extends Command implements PluginIdentifiableCommand
{

    private final Main plugin;

    public MainCommand(Main plugin) {
        super("XiaoYum", "XiaoYum 插件指令.", "/XiaoYum <list|info>", new String[]{"yum", "xyum"});
        this.setPermission("XiaoYum.command");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                new CommandParameter("命令", false, new String[]{"list", "info", "load", "unload", "delete", "folderdelete", "reload", "open"})
        });
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled() || !this.testPermission(sender)) {
            return false;
        }
        if (args.length == 0)
        {
            PrintUtil.PrintCommandSender(sender,"&6========= [&bXiaoYum&6] &6=========");
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a list&f[l] &7- &b列出已安装插件列表");
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a info &e<插件名称> &7- &b查看插件详情");
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a load &e<插件名称> &7- &b载入插件");
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a unload &e<插件名称> &7- &3卸载插件");
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a reload &e<插件名称> &7- &c重载插件");
            if(Main.Delete)
            {
                PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a delete&f[del] &e<插件名称> &7- &c卸载并删除插件");
                PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a folderdelete&f[fdel] &e<插件名称> &7- &c卸载并删除插件与配置文件夹");
            }
            PrintUtil.PrintCommandSender(sender,"&6/"+label+"&a open &7- &d打开插件管理页面");
            return true;
        }
        if(!sender.hasPermission("XiaoYum.admin"))
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c你没有权限这样做");
            return false;
        }
        if((args[0].equalsIgnoreCase("list")) || (args[0].equalsIgnoreCase("l")))
        {
            sendPlugins(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("info"))
        {
            if (args.length == 2)
            {
                sendPluginInfo(sender,args[1]);
                return true;
            } else {
                PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" info <插件名称>");
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("load"))
        {
            if (args.length == 2)
            {
                Utils.LoadPlugin(sender,args[1]);
                return true;
            } else {
                PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" load <插件名称>");
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("unload"))
        {
            if (args.length == 2)
            {
                Utils.unLoadPlugin(sender,args[1]);
                return true;
            } else {
                PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" unload <插件名称>");
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("reload"))
        {
            if (args.length == 1)
            {
                try
                {
                    PluginUtil.reloadLoadPlugin();
                    PrintUtil.PrintCommandSender(sender,"{prefix}&a配置文件重载成功！");
                    return true;
                } catch (Exception e)
                {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c重载失败,原因："+e.getMessage());
                    e.printStackTrace();
                }
                return false;
            }
            if (args.length == 2)
            {
                Utils.reloadPlugin(sender,args[1]);
                return true;
            }
            PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" reload <插件名称>");
            return false;
        }
        if((args[0].equalsIgnoreCase("delete")) || (args[0].equalsIgnoreCase("del")) || (args[0].equalsIgnoreCase("fdel")) || (args[0].equalsIgnoreCase("folderdelete")))
        {
            if(!Main.Delete)
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c未启用该功能.");
                return false;
            }
            boolean folder = false;
            if((args[0].equalsIgnoreCase("fdel")) || (args[0].equalsIgnoreCase("folderdelete"))) folder = true;
            if (args.length == 2)
            {
                if(folder)
                {
                    Utils.deletePlugin(sender,args[1],true);
                } else {
                    Utils.deletePlugin(sender,args[1],false);
                }
                return true;
            } else {
                if(folder)
                {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" fdel <插件名称>");
                } else {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&e正确用法:/"+label+" del <插件名称>");
                }
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("open"))
        {
            if(!(sender instanceof Player))
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c控制台无法打开页面.");
                return false;
            }
            Player p = (Player) sender;
            p.showFormWindow(Window.MainWindow(p));
            return true;
        }
        PrintUtil.PrintCommandSender(sender,"{prefix}&c未知指令.");
        return false;
    }

    private static void sendPlugins(CommandSender sender)
    {
        if(Main.getInstance().getServer().getPluginManager().getPlugins().isEmpty())
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c没有安装任何插件.");
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&3服务器已安装插件:");
            for(Plugin plugin:Main.getInstance().getServer().getPluginManager().getPlugins().values())
            {
                if(plugin != null)
                {
                    if(plugin.isEnabled())
                    {
                        PrintUtil.PrintCommandSender(sender,"&f&l> &a"+Utils.getPluginName(plugin)+"  ("+Utils.getPluginVersion(plugin)+")  &7["+Utils.getPluginApiVersion(plugin)+"]");
                    } else {
                        PrintUtil.PrintCommandSender(sender,"&f&l> &c"+Utils.getPluginName(plugin)+"  ("+Utils.getPluginVersion(plugin)+")  ["+Utils.getPluginApiVersion(plugin)+"]");
                    }
                }
            }
        }
    }

    private static void sendPluginInfo(CommandSender sender,String PluginName)
    {
        Plugin plugin = Utils.getPlugin(PluginName);
        if (plugin != null && plugin.isEnabled())
        {
            PrintUtil.PrintCommandSender(sender,"&6插件名称: &3"+Utils.getPluginName(plugin));
            PrintUtil.PrintCommandSender(sender,"&6插件版本: &3"+Utils.getPluginVersion(plugin) +" &7("+ Utils.getPluginApiVersion(plugin)+")");
            PrintUtil.PrintCommandSender(sender,"&6插件作者: &3"+Utils.getPluginAuthors(plugin));
            PrintUtil.PrintCommandSender(sender,"&6插件描述: &3"+Utils.getPluginDescription(plugin));
            PrintUtil.PrintCommandSender(sender,"&6插件依赖: &3"+Utils.getPluginDepend(plugin));
            PrintUtil.PrintCommandSender(sender,"&6插件软依赖: &3"+Utils.getPluginSoftDepend(plugin));
            sendPluginCommand(sender,plugin);
            sendPluginPermissions(sender,plugin);
            PrintUtil.PrintCommandSender(sender,"&6插件所在位置: &3"+Utils.getPluginPosition(plugin));
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c不存在！");
        }
    }
    //send info 直接引用 不做Plugin Null判断
    private static void sendPluginCommand(CommandSender sender,Plugin plugin)
    {
        Map<String,Object> Commands = plugin.getDescription().getCommands();
        if(Commands != null)
        {
            if(Commands.isEmpty())
            {
                PrintUtil.PrintCommandSender(sender,"&6插件注册命令: &3无");
            } else {
                PrintUtil.PrintCommandSender(sender,"&6插件注册命令: ");
                for (Iterator iterator = Commands.entrySet().iterator(); iterator.hasNext();)
                {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    PrintUtil.PrintCommandSender(sender,"&6 - &a"+(String)entry.getKey());
                    PrintUtil.PrintCommandSenderEntry(sender,"&6  其他名称:&a",(Map)entry.getValue(),true,"aliases");
                    PrintUtil.PrintCommandSenderEntry(sender,"&6  描述:&a",(Map)entry.getValue(),false,"description");
                    PrintUtil.PrintCommandSenderEntry(sender,"&6  权限:&a",(Map)entry.getValue(),false,"permission");
                    PrintUtil.PrintCommandSenderEntry(sender,"&6  用法:&a",(Map)entry.getValue(),false,"usage");
                }

            }
        }
    }

    //send info 直接引用 不做Plugin Null判断
    private static void sendPluginPermissions(CommandSender sender,Plugin plugin)
    {
        if(plugin.getDescription().getPermissions() != null)
        {
            if(plugin.getDescription().getPermissions().isEmpty())
            {
                PrintUtil.PrintCommandSender(sender,"&6插件注册权限: &3无");
            } else {
                for (Permission permission : plugin.getDescription().getPermissions())
                {
                    if(permission.getDescription() == null)
                    {
                        PrintUtil.PrintCommandSender(sender,"&6 - &a"+permission.getName()+ " &7- &f无注释.");
                    } else {
                        PrintUtil.PrintCommandSender(sender,"&6 - &a"+permission.getName()+ " &7- &f"+permission.getDescription());
                    }
                }
            }
        }
    }
}
