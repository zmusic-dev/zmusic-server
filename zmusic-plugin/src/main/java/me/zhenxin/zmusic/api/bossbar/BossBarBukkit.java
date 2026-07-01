package me.zhenxin.zmusic.api.bossbar;

import me.zhenxin.zmusic.ZMusic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.function.Consumer;


public class BossBarBukkit implements BossBar {

    private final Player p;
    private final String title;
    private final double seconds;
    private final org.bukkit.boss.BossBar bar;
    private Object foliaTask;
    private BukkitTask bukkitTask;

    public BossBarBukkit(Object p, String title, BarColor color, BarStyle style, float seconds) {
        Player player = (Player) p;
        this.bar = org.bukkit.Bukkit.getServer().createBossBar(title, org.bukkit.boss.BarColor.valueOf(color.name()), org.bukkit.boss.BarStyle.valueOf(style.name()));
        this.p = player;
        this.title = title;
        this.seconds = seconds;
    }

    @Override
    public void showTitle() {
        bar.setVisible(true);
        bar.setProgress(0);
        bar.addPlayer(p);
        // 同步定时器更新进度，避免在异步线程访问非线程安全的 BossBar
        // 兼容 Folia: 反射调用 GlobalRegionScheduler.runAtFixedRate
        Plugin plugin = ZMusicBukkitPlugin();
        Runnable tick = () -> {
            if (!bar.isVisible()) {
                cancelTask();
                return;
            }
            double step = 1F / seconds;
            double prog = Math.min(bar.getProgress() + step, 1.0);
            if (prog >= 1.0) {
                bar.setProgress(1.0);
                bar.setVisible(false);
                cancelTask();
                return;
            }
            bar.setProgress(prog);
        };
        if (ZMusic.isFolia) {
            try {
                Object scheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                java.lang.reflect.Method runAtFixedRate = scheduler.getClass().getMethod(
                        "runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
                foliaTask = runAtFixedRate.invoke(scheduler, plugin, (Consumer<Object>) t -> tick.run(), 20L, 20L);
            } catch (Exception e) {
                // 反射失败回退到 Bukkit 调度器
                bukkitTask = Bukkit.getServer().getScheduler().runTaskTimer(plugin, tick, 20L, 20L);
            }
        } else {
            bukkitTask = Bukkit.getServer().getScheduler().runTaskTimer(plugin, tick, 20L, 20L);
        }
    }

    private static Plugin ZMusicBukkitPlugin() {
        return me.zhenxin.zmusic.ZMusicBukkit.plugin;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        bar.setTitle(title);
    }

    @Override
    public void removePlayer(Object player) {
        Player p = (Player) player;
        bar.removePlayer(p);
        cancelTask();
    }

    @Override
    public void removeAll() {
        bar.removeAll();
        cancelTask();
    }

    @Override
    public boolean isVisible() {
        return bar.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        bar.setVisible(visible);
        if (!visible) {
            cancelTask();
        }
    }

    private void cancelTask() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
        if (foliaTask != null) {
            try {
                foliaTask.getClass().getMethod("cancel").invoke(foliaTask);
            } catch (Exception ignored) {
            }
            foliaTask = null;
        }
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.valueOf(bar.getColor().name());
    }

    @Override
    public void setBarColor(BarColor barColor) {
        bar.setColor(org.bukkit.boss.BarColor.valueOf(barColor.name()));
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.valueOf(bar.getStyle().name());
    }

    @Override
    public void setBarStyle(BarStyle barStyle) {
        bar.setStyle(org.bukkit.boss.BarStyle.valueOf(barStyle.name()));
    }

    @Override
    public double getProgress() {
        return (float) bar.getProgress();
    }

    @Override
    public void setProgress(double progress) {
        bar.setProgress(progress);
    }

    @Override
    public boolean hasPlayer(Object playerObj) {
        List<Player> players = bar.getPlayers();
        for (Player player : players) {
            if (player == playerObj)
                return true;
        }
        return false;
    }

    @Override
    public void addPlayer(Object playerObj) {
        bar.addPlayer((Player) playerObj);
    }
}
