package zbv5.cn.XiaoYum.util;

import cn.nukkit.utils.Config;
import zbv5.cn.XiaoYum.Main;

import java.io.File;

public class FileUtil
{
    public static Config config;

    public static void LoadFile()
    {
        try
        {
            File Config_Yml = new File(Main.getInstance().getDataFolder(), "config.yml");
            if (!Config_Yml.exists())
            {
                Main.getInstance().saveResource("config.yml", false);
            }
            config = new Config(new File(Main.getInstance().getDataFolder() + "/config.yml"));

            Main.Ignore = config.getStringList("Ignore");
            for(String plugin:Main.Ignore)
            {
                PrintUtil.PrintConsole("&e > &e禁止操作插件:&b"+plugin);
            }
            Main.Delete = config.getBoolean("Delete");
            if(Main.Delete)
            {
                PrintUtil.PrintConsole("&e > &a启用删除功能");
            } else {
                PrintUtil.PrintConsole("&e > &c禁用删除功能");
            }
            PrintUtil.PrintConsole("&e > &a配置文件版本 "+config.getString("Version"));
            PrintUtil.PrintConsole("&a&l√ &a配置文件加载完成.");
        }
        catch (Exception e)
        {
            PrintUtil.PrintConsole("&c&l× &4加载配置文件出现问题,请检查服务器.");
            e.printStackTrace();
        }
    }
}
