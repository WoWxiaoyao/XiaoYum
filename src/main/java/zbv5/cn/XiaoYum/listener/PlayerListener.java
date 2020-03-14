package zbv5.cn.XiaoYum.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.Plugin;
import org.json.JSONObject;
import zbv5.cn.XiaoYum.Main;
import zbv5.cn.XiaoYum.util.PluginUtil;
import zbv5.cn.XiaoYum.util.PrintUtil;
import zbv5.cn.XiaoYum.util.Utils;
import zbv5.cn.XiaoYum.windows.Window;

import java.util.HashMap;

public class PlayerListener implements Listener
{

    public static HashMap<String, String> see = new HashMap<String, String>();

    @EventHandler
    public void onClickWindow(PlayerFormRespondedEvent e)
    {
        if (e.getPlayer() == null) {
            return;
        }
        if (e.getResponse() == null) {
            return;
        }
        FormWindow gui = e.getWindow();
        Player p = e.getPlayer();
        JSONObject json = new JSONObject(e.getWindow().getJSONData());
        String title = json.getString("title");

        if (gui instanceof FormWindowSimple)
        {
            String ButtonName = ((FormResponseSimple)e.getResponse()).getClickedButton().getText();
            if(title.contains(PrintUtil.cc("&5&l插件管理系统")))
            {
                if(ButtonName.equalsIgnoreCase(PrintUtil.cc("&4&l返回主页")))
                {
                    p.showFormWindow(Window.MainWindow(p));
                }
                if(ButtonName.equalsIgnoreCase(PrintUtil.cc("&c&l返回插件列表")))
                {
                    p.showFormWindow(Window.PluginsWindow(p));
                }
            }
            if(title.equalsIgnoreCase(PrintUtil.cc("&5&l插件管理系统")))
            {
                if(ButtonName.equalsIgnoreCase(PrintUtil.cc("&8插件列表")))
                {
                    p.showFormWindow(Window.PluginsWindow(p));
                }
                if(ButtonName.equalsIgnoreCase(PrintUtil.cc("&8加载插件")))
                {
                    p.showFormWindow(Window.LoadPluginWindow(p));
                }
                if(ButtonName.equalsIgnoreCase(PrintUtil.cc("&c重载插件配置")))
                {
                    try
                    {
                        PluginUtil.reloadLoadPlugin();
                        PrintUtil.PrintCommandSender(p,"{prefix}&a配置文件重载成功！");
                    } catch (Exception ex)
                    {
                        PrintUtil.PrintCommandSender(p,"{prefix}&c重载失败,原因："+ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            if(title.equalsIgnoreCase(PrintUtil.cc("&5&l插件管理系统 &f- &4&l插件列表")))
            {
                for(Plugin plugin:Main.getInstance().getServer().getPluginManager().getPlugins().values())
                {
                    if(ButtonName.contains(PrintUtil.cc(plugin.getName())))
                    {
                        p.showFormWindow(Window.PluginWindow(p,plugin.getName()));
                        see.put(p.getName(),plugin.getName());
                        break;
                    }
                }
            }

            if(title.startsWith(PrintUtil.cc("&8&5&7&8&5&7&5&l插件管理系统 &f-")))
            {
                if(see.containsKey(p.getName()))
                {
                    String PluginName = see.get(p.getName());
                    if(ButtonName.equalsIgnoreCase(PrintUtil.cc("卸载插件")))
                    {
                        Utils.unLoadPlugin(p,PluginName);
                    }
                    if(ButtonName.equalsIgnoreCase(PrintUtil.cc("重载插件")))
                    {
                        Utils.reloadPlugin(p,PluginName);
                    }
                    if(ButtonName.equalsIgnoreCase(PrintUtil.cc("卸载并删除插件")))
                    {
                        Utils.deletePlugin(p,PluginName,false);
                    }
                    if(ButtonName.equalsIgnoreCase(PrintUtil.cc("卸载并删除插件与配置文件夹")))
                    {
                        Utils.deletePlugin(p,PluginName,true);
                    }
                    see.remove(p.getName());
                }
            }
        }

        if (gui instanceof FormWindowCustom)
        {
            FormWindowCustom formWindowCustom = (FormWindowCustom)e.getWindow();
            if(title.equalsIgnoreCase(PrintUtil.cc("&5&l插件管理系统 &f- &4&l加载插件")))
            {
                String PluginName = ((FormWindowCustom) e.getWindow()).getResponse().getInputResponse(0);
                if(PluginName != null)
                {
                    Utils.LoadPlugin(p,PluginName);
                } else {
                    PrintUtil.PrintCommandSender(p,"{prefix}&c请输入插件名称.");
                }
            }

        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(see.containsKey(p.getName()))
        {
            see.remove(p.getName());
        }
    }
}
