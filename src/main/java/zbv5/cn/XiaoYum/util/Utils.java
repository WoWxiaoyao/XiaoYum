package zbv5.cn.XiaoYum.util;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Event;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.plugin.RegisteredListener;
import zbv5.cn.XiaoYum.Main;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;

public class Utils
{

    public static void LoadPlugin(CommandSender sender,String PluginName)
    {
        long start = System.currentTimeMillis();
        if(isIgnore(PluginName))
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c禁止执行任何操作.");
            return;
        }
        if((Main.getInstance().getServer().getPluginManager().getPlugin(PluginName) == null) || (Main.getInstance().getServer().getPluginManager().getPlugin(PluginName).isDisabled()))
        {
            String jar = PluginName;
            if(!jar.endsWith(".jar"))
            {
                jar = jar+".jar";
            }
            File plugins = new File("plugins");
            if (!plugins.isDirectory())
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c插件目录plugins不存在或操作异常!");
                return;
            }
            File jarFile = new File(plugins, jar);
            if(!jarFile.isFile())
            {
                jarFile = null;
                for(File file:plugins.listFiles())
                {
                    if(file.getName().endsWith(".jar"))
                    {
                        PluginDescription desc = Main.getInstance().getPluginLoader().getPluginDescription(file);
                        if (desc.getName().equalsIgnoreCase(PluginName))
                        {
                            jarFile = file;
                            break;
                        }
                    }
                }

            }
            if (jarFile == null)
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c无法从插件目录中找到&b"+PluginName+"&c插件");
                return;
            }
            Plugin target = null;
            try {
                target = Main.getInstance().getServer().getPluginManager().loadPlugin(jarFile);

                if(target != null)
                {
                    List<String> depends = target.getDescription().getDepend();
                    if(depends != null)
                    {
                        for(String depend:depends)
                        {
                            Plugin DependPlugin = getPlugin(depend);
                            if((DependPlugin == null) || (DependPlugin.isDisabled()))
                            {
                                PrintUtil.PrintCommandSender(sender,"{prefix}&c错误: 缺少前置插件 "+depend);
                                close(target);
                                return;
                            }
                        }
                    }
                } else {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c错误: 加载目标错误.");
                    return;
                }
                target.onLoad();
                Main.getInstance().getServer().getPluginManager().enablePlugin(target);
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getMessage() == null)
                {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c错误: NullPointerException");
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c信息: 出现空指针错误");
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c分析: 建议尝试重启服务器重新载入插件.");
                    return;
                }
                if(e.getMessage().endsWith("has been already initialized"))
                {
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c错误: "+e.getMessage());
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c信息: 插件已载入服务器");
                    PrintUtil.PrintCommandSender(sender,"{prefix}&c分析: 不支持或缺少前置插件.");
                    return;
                }
            }
            PrintUtil.PrintCommandSender(sender,"{prefix}&a插件&b"+PluginName+"&a加载成功！&7("+Long.valueOf(System.currentTimeMillis() - start)+"ms)");
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c已加载！");
        }
    }

    public static void unLoadPlugin(CommandSender sender,String PluginName)
    {
        long start = System.currentTimeMillis();
        if(isIgnore(PluginName))
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c禁止执行任何操作.");
            return;
        }
        Plugin plugin = getPlugin(PluginName);
        if (plugin != null && plugin.isEnabled())
        {
            PluginManager pm = Main.getInstance().getServer().getPluginManager();
            if (pm == null)
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c卸载失败，插件管理器异常！");
                return;
            }
            PluginName = plugin.getName();
            SimpleCommandMap commandMap = null;
            Map<String, Command> commands = null;
            Map<Event, SortedSet<RegisteredListener>> listeners = null;
            boolean listener = true;
            try
            {
                try {
                    Field listenersField = pm.getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pm);
                } catch (Exception e)
                {
                    listener = false;
                }
                Field commandMapField = pm.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pm);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);
            }
            catch (NoSuchFieldException |IllegalAccessException e)
            {
                PrintUtil.PrintCommandSender(sender,"{prefix}&c卸载失败，获取插件信息错误！");
                e.printStackTrace();
                return;
            }
            //取消加载插件
            pm.disablePlugin(plugin);
            //删除监听
            if ((listeners != null) && (listener))
            {
                for (SortedSet<RegisteredListener> set : listeners.values())
                {
                    for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext(); )
                    {
                        RegisteredListener value = it.next();
                        if (value.getPlugin() == plugin)
                        {
                            it.remove();
                        }
                    }
                }
            }
            //删除plugin.yml中注册的指令
            if (commandMap != null)
            {
                for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry<String, Command> entry = it.next();
                    if (entry.getValue() instanceof PluginCommand)
                    {
                        PluginCommand c = (PluginCommand) entry.getValue();
                        if (c.getPlugin() == plugin) {
                            c.unregister(commandMap);
                            it.remove();
                        }
                    }
                }
            }

            //删除plugins
            if(pm.getPlugins() != null)
            {
                Main.getInstance().getServer().getPluginManager().getPlugins().remove(PluginName);
            }
            close(plugin);
            System.gc();

            PrintUtil.PrintCommandSender(sender,"{prefix}&a成功卸载插件&b"+PluginName+ "&7("+Long.valueOf(System.currentTimeMillis() - start)+"ms)");
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c无法找到插件&b"+PluginName+"&c卸载失败！");
        }
    }

    public static void reloadPlugin(CommandSender sender,String PluginName)
    {
        long start = System.currentTimeMillis();
        if(isIgnore(PluginName))
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c禁止执行任何操作.");
            return;
        }
        Plugin plugin = getPlugin(PluginName);
        if (plugin != null && plugin.isEnabled())
        {
            unLoadPlugin(sender,PluginName);
            LoadPlugin(sender,PluginName);
            PrintUtil.PrintCommandSender(sender,"{prefix}&a成功重载插件&b"+PluginName+ "&7("+Long.valueOf(System.currentTimeMillis() - start)+"ms)");
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c无法找到插件&b"+PluginName+"&c重载失败！");
        }
    }

    public static void deletePlugin(CommandSender sender,String PluginName,boolean Folder)
    {
        long start = System.currentTimeMillis();
        if(!Main.Delete)
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c未启用该功能.");
            return;
        }
        if(isIgnore(PluginName))
        {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c插件&b"+PluginName+"&c禁止执行任何操作.");
            return;
        }
        Plugin plugin = getPlugin(PluginName);
        if (plugin != null && plugin.isEnabled())
        {
            File jar = getPluginJar(plugin);
            unLoadPlugin(sender,PluginName);
            jar.delete();
            if(Folder)
            {
                String[] folders = plugin.getDataFolder().list();
                if (folders != null)
                {
                    for (String folder : folders)
                    {
                        File file = new File(plugin.getDataFolder(), folder);
                        try
                        {
                            file.delete();
                            PrintUtil.PrintCommandSender(sender,"{prefix}&a删除&e"+file.getAbsolutePath()+"&a成功！");
                        } catch (Exception e)
                        {
                            PrintUtil.PrintCommandSender(sender,"{prefix}&c删除&e"+file.getAbsolutePath()+"&c失败！ "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            PrintUtil.PrintCommandSender(sender,"{prefix}&a成功卸载并删除插件&b"+PluginName+ "&7("+Long.valueOf(System.currentTimeMillis() - start)+"ms)");
        } else {
            PrintUtil.PrintCommandSender(sender,"{prefix}&c无法找到插件&b"+PluginName+"&c卸载删除失败！");
        }
    }

    public static Plugin getPlugin(String PluginName)
    {
        Plugin plugin = null;

        for(Plugin plugins:Main.getInstance().getServer().getPluginManager().getPlugins().values())
        {
            if (plugins != null && plugins.isEnabled())
            {
                String PluginsName = getPluginName(plugins);
                if(PluginName.equalsIgnoreCase(PluginsName))
                {
                    plugin = plugins;
                }
            }
        }
        return plugin;
    }
    public static String getPluginName(Plugin plugin)
    {
        if (plugin != null)
        {
            return plugin.getName();
        }
        return "null";
    }

    public static String getPluginVersion(Plugin plugin)
    {
        if (plugin != null)
        {
            return plugin.getDescription().getVersion();
        }
        return "null";
    }

    public static String getPluginApiVersion(Plugin plugin)
    {
        if (plugin != null)
        {
            String Api = "";
            int i = 0;
            for(String s:plugin.getDescription().getCompatibleAPIs())
            {
                if(i == 0)
                {
                    Api = s;
                } else {
                    Api =  Api + "," + s;
                }
                i++;
            }
            return  Api;
        }
        return "null";
    }

    public static String getPluginAuthors(Plugin plugin)
    {
        if (plugin != null)
        {
            String Authors = "未标明";
            int i = 0;
            for(String s:plugin.getDescription().getAuthors())
            {
                if(i == 0)
                {
                    Authors = s;
                } else {
                    Authors = Authors + "," + s;
                }
                i++;
            }
            return Authors;
        }
        return "null";
    }

    public static String getPluginDescription(Plugin plugin)
    {
        if (plugin != null)
        {
            String s = plugin.getDescription().getDescription();
            if(s == null)
            {
                return "无";
            } else {
                return s;
            }
        }
        return "null";
    }

    public static String getPluginDepend(Plugin plugin)
    {
        if (plugin != null)
        {
            String Depend = "无";
            int i = 0;
            for(String s:plugin.getDescription().getDepend())
            {
                if(i == 0)
                {
                    Depend = s;
                } else {
                    Depend = Depend + "," + s;
                }
                i++;
            }
            return Depend;
        }
        return "null";
    }

    public static String getPluginSoftDepend(Plugin plugin)
    {
        if (plugin != null)
        {
            String SoftDepend = "无";
            int i = 0;
            for(String s:plugin.getDescription().getSoftDepend())
            {
                if(i == 0)
                {
                    SoftDepend = s;
                } else {
                    SoftDepend = SoftDepend + "," + s;
                }
                i++;
            }
            return SoftDepend;
        }
        return "null";
    }

    public static String getPluginPosition(Plugin plugin)
    {
        if (plugin != null)
        {
            return getPluginJar(plugin).getAbsolutePath();
        }
        return "null";
    }

    public static File getPluginJar(Plugin plugin)
    {
        File file = null;
        ClassLoader PluginClass = plugin.getClass().getClassLoader();
        if ((PluginClass instanceof URLClassLoader))
        {
            URLClassLoader ucl = (URLClassLoader)PluginClass;
            URL url = ucl.getURLs()[0];
            try
            {
                file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {}
        }
        return file;
    }

    public static void close(Plugin plugin)
    {
        ClassLoader cl = plugin.getClass().getClassLoader();
        try
        {
            ((URLClassLoader)cl).close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isIgnore(String PluginName)
    {
        boolean c = false;
        for(String s:Main.Ignore)
        {
            if(s.equalsIgnoreCase(PluginName))
            {
               c = true;
               break;
            }
        }
        return c;
    }
}
