package me.zhenxin.zmusic;

/**
 * 应用程序
 *
 * @author 真心
 * @since 2024/8/24 19:31
 */
public class Application {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        System.out.println("这是一个 Minecraft 插件，请使用 Bukkit, BungeeCord 或 Velocity 来运行这个插件。");
        System.out.println("This is Minecraft Plugin, Please use Bukkit, BungeeCord or Velocity to run this plugin.");
        System.out.println("按任意键打开 ZMusic 使用文档。");
        System.out.println("Press any key to open ZMusic usage document.");

        String url = "https://zmusic.zhenxin.me";
        try {
            System.in.read();

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                String[] cmd = {"xdg-open", url};
                Runtime.getRuntime().exec(cmd);
            }
        } catch (Exception e) {
            System.out.println("无法打开浏览器，请手动打开浏览器并访问 " + url);
            System.out.println("Can't open browser, Please open browser and visit " + url);
            System.out.println("按任意键退出。");
            System.out.println("Press any key to exit.");
            try {
                System.in.read();
            } catch (Exception ignored) {
            }
        }
    }
}
