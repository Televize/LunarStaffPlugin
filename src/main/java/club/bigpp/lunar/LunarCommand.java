package club.bigpp.lunar;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

@CommandAlias("lunar|lunarmods|staffmods")
@AllArgsConstructor
public class LunarCommand extends BaseCommand {
    private LunarStaffPlugin plugin;
    private ConfigHandler config;

    @Override
    public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {

        String[] parts = cmd.getCommand().split(" ");
        String command = parts[parts.length - 1].toLowerCase();

        if ("enable".equals(command) || "disable".equals(command)) {
            command = "toggle";
        }

        if ("forceenable".equals(command) || "forcedisable".equals(command)) {
            command = "force";
        }

        if (!issuer.hasPermission(this.config.getPermission("command"))) {
            issuer.sendMessage(this.config.getMessage("permission"));
            return;
        }

        if (!(command.equals("check") || command.equals("toggle") || command.equals("force"))) {
            this.onHelp(((BukkitCommandIssuer) issuer).getPlayer());
            return;
        }

        if (issuer.hasPermission(this.config.getPermission(command))) {
            super.showSyntax(issuer, cmd);
        } else {
            issuer.sendMessage(this.config.getMessage(command + ".permission"));
        }
    }

    @Override
    public boolean hasPermission(CommandIssuer issuer) {
        return issuer.hasPermission(this.config.getPermission("command"));
    }

    private boolean lacksPermission(Player sender, String command) {
        if (!this.config.hasPermission(sender, "command")) {
            this.config.sendMessage(sender, "permission");
            return true;
        }

        if (!this.config.hasPermission(sender, command)) {
            this.config.sendMessage(sender, command + ".permission");
            return true;
        }

        return false;
    }

    @Default
    @CatchUnknown
    public void onHelp(Player sender) {
        if (!this.config.hasPermission(sender, "command")) {
            this.config.sendMessage(sender, "permission");
            return;
        }

        String result = ChatColor.WHITE + "Commands for " + ChatColor.AQUA + "LunarStaffPlugin";

        if (this.config.hasPermission(sender, "check")) {
            result += ChatColor.WHITE + "\n - check <player>";
        }

        if (this.config.hasPermission(sender, "toggle")) {
            result += ChatColor.WHITE + "\n - enable <mod>";
            result += ChatColor.WHITE + "\n - disable <mod>";
        }

        if (this.config.hasPermission(sender, "force")) {
            result += ChatColor.WHITE + "\n - forceenable <player> <mod>";
            result += ChatColor.WHITE + "\n - forcedisable <player> <mod>";
        }

        sender.sendMessage(result);
    }

    @Subcommand("check")
    @CommandCompletion("*")
    @Syntax("<player>")
    public void onCheck(Player sender, OnlinePlayer playerTarget) {
        if (lacksPermission(sender, "check")) {
            return;
        }

        Player target = playerTarget.getPlayer();

        if (target == null || !target.isOnline()) {
            this.config.sendMessage(sender, "check.offline");
            return;
        }

        Set<String> mods = this.plugin.getPlayers().get(target.getUniqueId());

        if (mods == null || mods.size() == 0) {
            this.config.sendMessage(sender, "check.success", target.getName(), "0");
            return;
        }

        this.config.sendMessage(sender, "check.success", target.getName(), mods.size() + "");

        for (String mod : mods) {
            this.config.sendMessage(sender, "check.mod", mod);
        }
    }

    @Subcommand("enable")
    @CommandCompletion("@mods")
    @Syntax("<mod>")
    public void onEnable(Player sender, String modId) {
        if (lacksPermission(sender, "toggle")) {
            return;
        }

        Mod mod = this.plugin.getMod(modId);

        if (mod == null) {
            this.config.sendMessage(sender, "toggle.exist");
            return;
        }

        if (!this.config.hasPermission(sender, "mod." + mod.getId())) {
            this.config.sendMessage(sender, "toggle.modperm");
            return;
        }

        this.plugin.setModState(sender, mod, true);
        this.config.sendMessage(sender, "toggle.success", config.getMessage("toggle.enabled"), mod.getId());
    }

    @Subcommand("disable")
    @CommandCompletion("@mods")
    @Syntax("<mod>")
    public void onDisable(Player sender, String modId) {
        if (lacksPermission(sender, "toggle")) {
            return;
        }

        Mod mod = this.plugin.getMod(modId);

        if (mod == null) {
            this.config.sendMessage(sender, "toggle.exist");
            return;
        }

        if (!this.config.hasPermission(sender, "mod." + mod.getId())) {
            this.config.sendMessage(sender, "toggle.modperm");
            return;
        }

        this.plugin.setModState(sender, mod, false);
        this.config.sendMessage(sender, "toggle.success", config.getMessage("toggle.disabled"), mod.getId());
    }

    @Subcommand("forceenable")
    @CommandCompletion("* @mods")
    @Syntax("<player> <mod>")
    public void onForceEnable(Player sender, OnlinePlayer playerTarget, String modId) {
        if (lacksPermission(sender, "force")) {
            return;
        }

        Player target = playerTarget.getPlayer();

        if (target == null || !target.isOnline()) {
            this.config.sendMessage(sender, "force.offline");
            return;
        }

        Mod mod = this.plugin.getMod(modId);

        if (mod == null) {
            this.config.sendMessage(sender, "force.exist");
            return;
        }

        if (!this.config.hasPermission(sender, "mod." + mod.getId())) {
            this.config.sendMessage(sender, "force.modperm");
            return;
        }

        this.plugin.setModState(target, mod, true, sender.getName());
        this.config.sendMessage(target, "toggle.success", config.getMessage("toggle.enabled"), mod.getId());
        this.config.sendMessage(sender, "force.success", target.getName(), config.getMessage("force.enable"), mod.getId());
    }

    @Subcommand("forcedisable")
    @CommandCompletion("* @mods")
    @Syntax("<player> <mod>")
    public void onForceDisable(Player sender, OnlinePlayer playerTarget, String modId) {
        if (lacksPermission(sender, "force")) {
            return;
        }

        Player target = playerTarget.getPlayer();

        if (target == null || !target.isOnline()) {
            this.config.sendMessage(sender, "force.offline");
            return;
        }

        Mod mod = this.plugin.getMod(modId);

        if (mod == null) {
            this.config.sendMessage(sender, "force.exist");
            return;
        }

        if (!this.config.hasPermission(sender, "mod." + mod.getId())) {
            this.config.sendMessage(sender, "force.modperm");
            return;
        }

        this.plugin.setModState(target, mod, false, sender.getName());
        this.config.sendMessage(target, "toggle.success", config.getMessage("toggle.disabled"), mod.getId());
        this.config.sendMessage(sender, "force.success", target.getName(), config.getMessage("force.disable"), mod.getId());
    }
}
