package club.bigpp.lunar;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.text.MessageFormat;
import java.util.*;

@Getter
public final class LunarStaffPlugin extends JavaPlugin implements PluginMessageListener {
    private Set<Mod> mods;
    private ConfigHandler configHandler;
    private Map<UUID, Set<String>> players;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.mods = new HashSet<>();
        this.players = new HashMap<>();
        this.configHandler = new ConfigHandler(this);

        this.configHandler.getModIds().forEach(modId -> mods.add(new Mod(modId)));

        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        commandManager.registerCommand(new LunarCommand(this, this.configHandler));
        commandManager.getCommandCompletions().registerAsyncCompletion("mods", c -> this.configHandler.getModIds());

        commandManager.setFormat(MessageType.INFO, ChatColor.WHITE, ChatColor.WHITE, ChatColor.WHITE, ChatColor.WHITE);
        commandManager.setFormat(MessageType.HELP, ChatColor.WHITE, ChatColor.WHITE, ChatColor.WHITE, ChatColor.WHITE);
        commandManager.setFormat(MessageType.ERROR, ChatColor.RED, ChatColor.RED, ChatColor.RED, ChatColor.RED);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.RED, ChatColor.RED, ChatColor.RED, ChatColor.RED);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Lunar-Client");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "Lunar-Client", this);  //Doesn't work unless its registered as incoming too
        this.getServer().getPluginManager().registerEvents(new LunarListener(this), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    Mod getMod(String id) {
        id = id.toLowerCase();

        for (Mod mod : mods) {
            if (mod.getId().equals(id)) {
                return mod;
            }
        }

        return null;
    }

    void setModState(Player target, Mod mod, boolean state) {
        this.setModState(target, mod, state, null);
    }

    void setModState(Player target, Mod mod, boolean state, String forceName) {

        players.computeIfAbsent(target.getUniqueId(), k -> new HashSet<>());

        if (state) {
            players.get(target.getUniqueId()).add(mod.getId());
        } else {
            players.get(target.getUniqueId()).remove(mod.getId());
        }

        target.sendPluginMessage(this, "Lunar-Client", state ? mod.getEnabled() : mod.getDisabled());

        String message = forceName == null ?
                MessageFormat.format(this.configHandler.getMessage("notify.toggle", ""), target.getName(), (state ? this.configHandler.getMessage("toggle.enabled") : this.configHandler.getMessage("toggle.disabled")), mod.getId()) :
                MessageFormat.format(this.configHandler.getMessage("notify.force", ""), forceName, target.getName(), (state ? this.configHandler.getMessage("force.enable") : this.configHandler.getMessage("force.disable")), mod.getId());

        if (message.equals("") || (forceName != null && forceName.equals("LOGIN"))) {
            return;
        }

        for (Player player : this.getServer().getOnlinePlayers()) {
            if (this.configHandler.hasPermission(player, "notify")) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

    }
}
