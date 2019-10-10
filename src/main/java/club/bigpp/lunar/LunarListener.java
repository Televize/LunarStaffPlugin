package club.bigpp.lunar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;

public class LunarListener implements Listener {
    private LunarStaffPlugin plugin;
    private ConfigHandler config;

    LunarListener(LunarStaffPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigHandler();

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.onJoin(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        /*
        Add 1 tick delay
         */
        new BukkitRunnable() {
            @Override
            public void run() {
                onJoin(event.getPlayer());
            }
        }.runTaskLater(this.plugin, 10L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        this.plugin.getPlayers().remove(event.getPlayer().getUniqueId());
    }

    private void onJoin(Player target) {
        if (target == null || !target.isOnline()) {
            return;
        }

        if (this.config.hasPermission(target, "auto")) {
            int count = 0;

            for (Mod mod : this.plugin.getMods()) {
                if (this.config.hasPermission(target, "mod." + mod.getId())) {
                    this.plugin.setModState(target, mod, true, "LOGIN");
                    count++;
                }
            }

            if (count > 0) {
                this.config.sendMessage(target, "auto", count + "");

                String message = MessageFormat.format(
                        this.config.getMessage("notify.auto", ""),
                        target.getName(),
                        count + ""
                );

                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    if (this.config.hasPermission(player, "notify")) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }
}
