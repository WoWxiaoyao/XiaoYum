package zbv5.cn.XiaoYum.util;

import zbv5.cn.XiaoYum.Main;
import zbv5.cn.XiaoYum.command.MainCommand;
import zbv5.cn.XiaoYum.listener.PlayerListener;

public class PluginUtil
{
    public static void LoadPlugin()
    {
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &d开始加载 &e========");
        FileUtil.LoadFile();
        Main.getInstance().getServer().getPluginManager().registerEvents(new PlayerListener(), Main.getInstance());
        Main.getInstance().getServer().getCommandMap().register("XiaoYum", new MainCommand(Main.getInstance()));
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &a加载成功 &e========");
    }

    public static void DisablePlugin()
    {
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &d开始卸载 &e========");
        PrintUtil.PrintConsole("&e> 感谢您的使用,期待下次运行~");
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &c卸载完成 &e========");
    }

    public static void reloadLoadPlugin()
    {
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &d开始重载 &e========");
        FileUtil.LoadFile();
        PrintUtil.PrintConsole("&e======== &bXiaoYum &e> &a重载成功 &e========");
    }
}
