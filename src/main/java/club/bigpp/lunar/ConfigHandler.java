package club.bigpp.lunar;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ConfigHandler {
    @Getter
    private Set<String> modIds;
    private Map<String, String> messages;
    private Map<String, String> permissions;

    ConfigHandler(LunarStaffPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        this.loadPermissions(config);
        this.loadMods(config);
        this.loadMessages(config);
    }

    private void loadMods(FileConfiguration config) {
        this.modIds = new HashSet<>();

        config.getStringList("mods").forEach(mod -> {
            String modId = mod.toLowerCase();
            this.modIds.add(modId);
            this.permissions.put("mod." + modId, config.getString("permission.mod." + modId, "lunar.mod." + modId));
        });
    }

    private void loadMessages(FileConfiguration config) {
        this.messages = new HashMap<>();

        this.messages.put("auto", "");
        this.messages.put("permission", "");

        this.messages.put("check.mod", "");
        this.messages.put("check.offline", "");
        this.messages.put("check.success", "");
        this.messages.put("check.permission", "");

        this.messages.put("force.exist", "");
        this.messages.put("force.offline", "");
        this.messages.put("force.success", "");
        this.messages.put("force.enable", "");
        this.messages.put("force.disable", "");
        this.messages.put("force.modperm", "");
        this.messages.put("force.permission", "");

        this.messages.put("toggle.exist", "");
        this.messages.put("toggle.success", "");
        this.messages.put("toggle.enabled", "");
        this.messages.put("toggle.disabled", "");
        this.messages.put("toggle.modperm", "");
        this.messages.put("toggle.permission", "");

        this.messages.put("notify.auto", "");
        this.messages.put("notify.force", "");
        this.messages.put("notify.toggle", "");

        this.messages.forEach((key, value) -> {
            String def = config.getDefaults().getString("messages." + key, key);
            this.messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("messages." + key, def)));
        });
    }

    private void loadPermissions(FileConfiguration config) {
        this.permissions = new HashMap<>();

        this.permissions.put("auto", "");
        this.permissions.put("check", "");
        this.permissions.put("force", "");
        this.permissions.put("notify", "");
        this.permissions.put("toggle", "");
        this.permissions.put("command", "");

        this.permissions.forEach((key, value) -> {
            String def = config.getDefaults().getString("permission." + key, key);
            this.permissions.put(key, config.getString("permission." + key, def));
        });
    }

    String getMessage(String id) {
        return this.messages.getOrDefault(id, id);
    }

    String getMessage(String id, String def) {
        return this.messages.getOrDefault(id, def);
    }

    void sendMessage(Player player, String id, Object... args) {
        String message = this.messages.get(id);

        if (message == null || message.equals("")) {
            return;
        }

        if (args == null || args.length == 0) {
            player.sendMessage(message);
        } else {
            player.sendMessage(MessageFormat.format(message, args));
        }
    }

    String getPermission(String key) {
        return this.permissions.getOrDefault(key, "lunar." + key);
    }

    boolean hasPermission(Player player, String key) {
        return this.permissions.containsKey(key) && player.hasPermission(this.permissions.get(key));
    }
}
