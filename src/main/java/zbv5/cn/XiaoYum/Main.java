package zbv5.cn.XiaoYum;

import cn.nukkit.plugin.PluginBase;
import zbv5.cn.XiaoYum.util.PluginUtil;

import java.util.ArrayList;
import java.util.List;

public class Main extends PluginBase
{
    private static Main instance;
    public static List<String> Ignore = new ArrayList<String>();
    public static boolean Delete = true;

    public static Main getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;
        PluginUtil.LoadPlugin();
    }

    @Override
    public void onDisable()
    {
        PluginUtil.DisablePlugin();
    }
}
