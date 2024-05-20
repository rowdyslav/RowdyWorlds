package org.rowdyslav.rowdyworlds.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Contract;
import org.rowdyslav.rowdyworlds.RowdyWorlds;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class Island implements CommandExecutor, TabCompleter {

    private final RowdyWorlds main;

    @Contract(pure = true)
    public Island(RowdyWorlds main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(label.equals("island") || label.equals("world"))) {
            return false;
        }
        var mm = MiniMessage.miniMessage();

        if (!(sender instanceof Player) && args.length != 2) {
            sender.sendMessage(
                    mm.deserialize("<red>Использование для консоли: /island <владелец мира> <игрок для тп>"));
            return false;
        }

        Component message;
        switch (args.length) {
            case 0 -> {
                main.teleportToPluginWorld((Player) sender, (Player) sender);
                message = mm.deserialize("<green>Добро пожаловать в ваш мир!");
            }
            case 1 -> {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[0]);

                main.teleportToPluginWorld(owner, (Player) sender);
                message = mm.deserialize("<green>Добро пожаловать в мир игрока " + owner.getName());
            }
            case 2 -> {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[0]);
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(mm.deserialize("<red>Игрок " + args[1] + " оффлайн."));
                    return false;
                }

                main.teleportToPluginWorld(owner, target);
                target.sendMessage("Вы были отправлены в мир игрока " + owner.getName());
                message = mm
                        .deserialize("<green>Вы отправили " + target.getName() + " в мир игрока " + owner.getName());
            }
            default -> {
                sender.sendMessage(mm.deserialize("<red>Использование: /island [владелец мира] [игрок для тп]"));
                return false;
            }
        }

        sender.sendMessage(message);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                      String @NotNull [] args) {

        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                List<String> worldNames = new ArrayList<>();
                for (World world : Bukkit.getWorlds()) {
                    worldNames.add(world.getName());
                }
                worldNames.remove("world");
                worldNames.remove("world_nether");
                worldNames.remove("world_the_end");

                for (String worldName : worldNames) {
                    if (worldName.toLowerCase().startsWith(args[0].toLowerCase())) {
                        result.add(worldName);
                    }
                }

                return result;
            }

            case 2 -> {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }

                for (String playerName : playerNames) {
                    if (playerName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(playerName);
                    }
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

}
