# Lunar Staff Plugin
This plugin was created to allow using lunar staff mods without requiring a complete lunar api.
This plugin does not contain an api and is intended to be interacted with through commands only.

Requirements:
> Spigot 1.7.x - 1.8.x
> Lunar Client

This plugin is not supported by or affiliated with [Lunar](https://lunar.gg/) or [Moonsworth]([https://moonsworth.com/), do not bother them for support with this plugin.

## Installation
1. Download the [latest release](https://github.com/remailmc/LunarStaffPlugin/releases)
2. Add the plugin to your servers plugin folder

## Configuration
The plugin configuration file is commented with descriptions for each node.
This plugin is meant to be easy to configure, providing custom permission nodes, messages and mods.

#### The config contains 3 sections:
> Mods
> Messages
> Permissions

## Commands

#### Command Aliases: lunar, lunarmods, staffmods
> SubCommands

> check &lt;player&gt; - See a list of mods that a player currently has enabled

> enable &lt;mod&gt; - Enable a staff mod, sending the packet to yourself

> disable &lt;mod&gt; - Enable a staff mod, sending the packet to yourself

> forceenable &lt;player&gt; &lt;mod&gt; - Enable a staff mod for someone else, sending the packet to them

> forcedisable &lt;player&gt; &lt;mod&gt; - Enable a staff mod for someone else, sending the packet to them

## Permissions

#### The default permissions are:
- lunar.auto - If a player has this permission, they will enable all mods they have permission for, on login.
- lunar.check - Permission to use the **check** command
- lunar.toggle - Permission to use the **enable** and **disable** commands
- lunar.force - Permission to use the **forceenable** and **forcedisable** commands
- lunar.notify - Users with this permission will be sent a message whenever a player enables / disables a mod
- lunar.command - Required to use any plugin commands
- lunar.mod.&lt;mod&gt; -  Permission to enable each specific mod or force another player to
