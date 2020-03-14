package zbv5.cn.XiaoYum.windows;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import zbv5.cn.XiaoYum.Main;
import zbv5.cn.XiaoYum.util.PrintUtil;
import zbv5.cn.XiaoYum.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Window
{
    public static FormWindowSimple MainWindow(Player p)
    {
        FormWindowSimple window= new FormWindowSimple(PrintUtil.cc("&5&l插件管理系统"),"");
        window.addButton(new ElementButton(PrintUtil.cc("&8插件列表")));
        window.addButton(new ElementButton(PrintUtil.cc("&8加载插件")));
        window.addButton(new ElementButton(PrintUtil.cc("&c重载插件配置")));
        window.addButton(new ElementButton(PrintUtil.cc("&4&l关闭")));
        return window;

    }
    public static FormWindowCustom LoadPluginWindow(Player p)
    {
        FormWindowCustom window= new FormWindowCustom(PrintUtil.cc("&5&l插件管理系统 &f- &4&l加载插件"));
        window.addElement(new ElementInput("加载插件", "输入插件名称", ""));
        return window;
    }
    public static FormWindowSimple PluginsWindow(Player p)
    {
        PluginManager pm = Main.getInstance().getServer().getPluginManager();

        if(pm.getPlugins().isEmpty())
        {
            FormWindowSimple window= new FormWindowSimple(PrintUtil.cc("&5&l插件管理系统 &f- &4&l插件列表"),PrintUtil.cc("&c当前服务器没有安装任何插件"));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l返回主页")));
            return window;
        } else {
            FormWindowSimple window= new FormWindowSimple(PrintUtil.cc("&5&l插件管理系统 &f- &4&l插件列表"),PrintUtil.cc("&f当前服务器安装插件："));

            for(Plugin plugin: Main.getInstance().getServer().getPluginManager().getPlugins().values())
            {
                if(plugin != null)
                {
                    if(plugin.isEnabled())
                    {
                        window.addButton(new ElementButton(PrintUtil.cc("&8"+plugin.getName()+" ("+ Utils.getPluginVersion(plugin)+") ["+Utils.getPluginApiVersion(plugin)+"]")));
                    } else {
                        window.addButton(new ElementButton(PrintUtil.cc("&c"+plugin.getName()+" &8（"+ Utils.getPluginVersion(plugin)+") &8["+Utils.getPluginApiVersion(plugin)+"]")));
                    }
                }
            }
            window.addButton(new ElementButton(PrintUtil.cc("&4&l返回主页")));
            return window;
        }
    }

    public static FormWindowSimple PluginWindow(Player p,String PluginName)
    {
        Plugin plugin = Utils.getPlugin(PluginName);
        if(plugin == null)
        {
            FormWindowSimple window= new FormWindowSimple(PrintUtil.cc("&8&5&7&5&l插件管理系统 &f- &4&l"+PluginName),PrintUtil.cc("&c获取插件异常"));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l返回插件列表")));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l返回主页")));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l关闭")));
            return window;
        } else {
            List<String> pluginText = getPluginText(plugin);
            String text = "";
            int i = 0;
            for(String s:pluginText)
            {
                if(i == 0)
                {
                    text = s;
                } else {
                    text = text+"\n"+s;
                }
                i++;
            }

            FormWindowSimple window= new FormWindowSimple(PrintUtil.cc("&8&5&7&8&5&7&5&l插件管理系统 &f- &4&l"+PluginName),PrintUtil.cc(text));

            window.addButton(new ElementButton(PrintUtil.cc("卸载插件")));
            window.addButton(new ElementButton(PrintUtil.cc("重载插件")));
            if(Main.Delete)
            {
                window.addButton(new ElementButton(PrintUtil.cc("卸载并删除插件")));
                window.addButton(new ElementButton(PrintUtil.cc("卸载并删除插件与配置文件夹")));
            }

            window.addButton(new ElementButton(PrintUtil.cc("&c&l返回插件列表")));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l返回主页")));
            window.addButton(new ElementButton(PrintUtil.cc("&4&l关闭")));
            return window;
        }
    }

    public static List<String> getPluginText(Plugin plugin)
    {
        List<String> text = new ArrayList<String>();
        text.add("&6插件名称: &3"+Utils.getPluginName(plugin));
        text.add("&6插件版本: &3"+Utils.getPluginVersion(plugin) +" &7("+ Utils.getPluginApiVersion(plugin)+")");
        text.add("&6插件作者: &3"+Utils.getPluginAuthors(plugin));
        text.add("&6插件描述: &3"+Utils.getPluginDescription(plugin));
        text.add("&6插件依赖: &3"+Utils.getPluginDepend(plugin));
        text.add("&6插件软依赖: &3"+Utils.getPluginSoftDepend(plugin));

        Map<String,Object> Commands = plugin.getDescription().getCommands();
        if(Commands != null)
        {
            if(Commands.isEmpty())
            {
                text.add("&6插件注册命令: &3无");
            } else {
                text.add("&6插件注册命令: ");
                for (Iterator iterator = Commands.entrySet().iterator(); iterator.hasNext();)
                {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    text.add("&6 - &a"+(String)entry.getKey());
                    if(PrintUtil.getEntry("&6  其他名称:&a",(Map)entry.getValue(),true,"aliases") != null)
                    {
                        text.add(PrintUtil.getEntry("&6  其他名称:&a",(Map)entry.getValue(),true,"aliases"));
                    }
                    if(PrintUtil.getEntry("&6  描述:&a",(Map)entry.getValue(),false,"description") != null)
                    {
                        text.add(PrintUtil.getEntry("&6  描述:&a",(Map)entry.getValue(),false,"description"));
                    }
                    if(PrintUtil.getEntry("&6  权限:&a",(Map)entry.getValue(),false,"permission") != null)
                    {
                        text.add(PrintUtil.getEntry("&6  权限:&a",(Map)entry.getValue(),false,"permission"));
                    }
                    if(PrintUtil.getEntry("&6  权限:&a",(Map)entry.getValue(),false,"permission") != null)
                    {
                        text.add(PrintUtil.getEntry("&6  用法:&a",(Map)entry.getValue(),false,"usage"));
                    }
                }
            }
        }
        if(plugin.getDescription().getPermissions() != null)
        {
            if(plugin.getDescription().getPermissions().isEmpty())
            {
                text.add("&6插件注册权限: &3无");
            } else {
                text.add("&6插件注册权限:");
                for (Permission permission : plugin.getDescription().getPermissions())
                {
                    if(permission.getDescription() == null)
                    {
                        text.add("&6 - &a"+permission.getName()+ " &7- &f无注释.");
                    } else {
                        text.add("&6 - &a"+permission.getName()+ " &7- &f"+permission.getDescription());
                    }
                }
            }
        }
        text.add("&6插件所在位置: &3"+Utils.getPluginPosition(plugin));

        return text;
    }
}
