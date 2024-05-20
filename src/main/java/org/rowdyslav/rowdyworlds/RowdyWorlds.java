package org.rowdyslav.rowdyworlds;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.rowdyslav.rowdyworlds.commands.Island;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public final class RowdyWorlds extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!isWorldGuardInstalled()) {
            getLogger().warning("WorldGuard не найден! Убедитесь, что плагин установлен.");
        }
        Objects.requireNonNull(getCommand("island")).setExecutor(new Island(this));
    }

    public void teleportToPluginWorld(OfflinePlayer world_owner, @NotNull Player target) {
        World world = createOrGetPlayerWorld(world_owner);
        target.teleport(world.getSpawnLocation());
    }

    private @NotNull World createOrGetPlayerWorld(@NotNull OfflinePlayer player) {
        String worldName = player.getName();
        assert worldName != null;
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            world = Bukkit.createWorld(WorldCreator.name(worldName));
            assert world != null;
            world.setSpawnLocation(0, 64, 0);

            if (isWorldGuardInstalled()) {
                RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer()
                        .get(BukkitAdapter.adapt(world));
                assert regions != null;
                GlobalProtectedRegion globalRegion = new GlobalProtectedRegion("__global__");
                regions.addRegion(globalRegion);
                Objects.requireNonNull(regions.getRegion("__global__")).getOwners().addPlayer(player.getName());
            }
        }

        return world;
    }

    private boolean isWorldGuardInstalled() {
        return getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

}
