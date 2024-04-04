package me.lrxh.practice.profile;

import me.lrxh.practice.Locale;
import me.lrxh.practice.Practice;
import me.lrxh.practice.profile.hotbar.HotbarItem;
import me.lrxh.practice.profile.meta.option.button.AllowSpectatorsOptionButton;
import me.lrxh.practice.profile.meta.option.button.DuelRequestsOptionButton;
import me.lrxh.practice.profile.meta.option.button.ShowScoreboardOptionButton;
import me.lrxh.practice.profile.option.OptionsOpenedEvent;
import me.lrxh.practice.profile.visibility.VisibilityLogic;
import me.lrxh.practice.util.CC;
import me.lrxh.practice.util.PlaceholderUtil;
import me.lrxh.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (!profile.isBusy() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            Practice.getInstance().getHotbar().giveHotbarItems(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getState() != ProfileState.FIGHTING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getState() != ProfileState.FIGHTING) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && event.getRightClicked() instanceof Player
                && Profile.getByUuid(player.getUniqueId()).getState().equals(ProfileState.LOBBY)) {
            Player clickedPlayer = (Player) event.getRightClicked();
            player.chat("/duel " + clickedPlayer.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getState() == ProfileState.LOBBY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

            if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
                event.setCancelled(true);

                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    PlayerUtil.teleportToSpawn((Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void soilChangePlayer(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onOptionsOpenedEvent(OptionsOpenedEvent event) {
        event.getButtons().add(new ShowScoreboardOptionButton());
        event.getButtons().add(new AllowSpectatorsOptionButton());
        event.getButtons().add(new DuelRequestsOptionButton());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();

            HotbarItem hotbarItem = Practice.getInstance().getHotbar().fromItemStack(event.getItem());

            if (hotbarItem != null) {
                if (hotbarItem.getCommand() != null) {
                    event.setCancelled(true);
                    player.chat("/" + hotbarItem.getCommand());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        Profile profile = new Profile(player.getUniqueId());

        try {
            profile.load();
        } catch (Exception e) {
            event.getPlayer().kickPlayer(ChatColor.RED + "Failed to load your profile. Try again later.");
            return;
        }
        Profile.getProfiles().put(player.getUniqueId(), profile);

        PlayerUtil.teleportToSpawn(player);
        player.setPlayerTime(profile.getOptions().time().getTime(), false);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            VisibilityLogic.handle(player, otherPlayer);
            VisibilityLogic.handle(otherPlayer, player);
        }
        for (String line : Practice.getInstance().getMessagesConfig().getStringList("JOIN_MESSAGE")) {
            ArrayList<String> list = new ArrayList<>();
            list.add(CC.translate(line));
            player.sendMessage(PlaceholderUtil.format(list, player).toString().replace("[", "").replace("]", ""));
        }

        if (player.hasPermission("practice.donor.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.updateInventory();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Practice.getInstance().getHotbar().giveHotbarItems(event.getPlayer());
            }
        }.runTaskLater(Practice.getInstance(), 10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (!profile.getFollowers().isEmpty()) {
            for (UUID playerUUID : profile.getFollowers()) {
                Bukkit.getPlayer(playerUUID).sendMessage(Locale.FOLLOW_END.format(Bukkit.getPlayer(playerUUID), event.getPlayer().getName()));
                Bukkit.getPlayer(playerUUID).sendMessage(Locale.FOLLOWED_LEFT.format(Bukkit.getPlayer(playerUUID), event.getPlayer().getName()));
                Profile.getByUuid(playerUUID).getFollowing().remove(event.getPlayer().getUniqueId());
            }
        }

        if (!profile.getFollowing().isEmpty()) {
            List<UUID> followingCopy = new ArrayList<>(profile.getFollowing());

            for (UUID playerUUID : followingCopy) {
                Profile followerProfile = Profile.getByUuid(playerUUID);
                followerProfile.getFollowers().remove(event.getPlayer().getUniqueId());

                profile.getFollowing().remove(playerUUID);
            }
        }

        if (profile.getMatch() != null) {
            profile.getMatch().sendDeathMessage(event.getPlayer(), null, false);
            profile.getMatch().end();
            profile.setMatch(null);

        }
        if (profile.getState().equals(ProfileState.QUEUEING)) {
            profile.getQueueProfile().getQueue().removeQueue();
        }
        if (profile.getQueueProfile() != null) {
            Practice.getInstance().getCache().getPlayers().remove(profile.getQueueProfile());
        }

        if (profile.getRematchData() != null) {
            profile.getRematchData().validate();
        }

    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        if (event.getReason() != null) {
            if (event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
            }
        }
    }

}
