package me.lrxh.practice.kit;

import me.lrxh.practice.Locale;
import me.lrxh.practice.kit.menu.KitEditorSelectKitMenu;
import me.lrxh.practice.kit.menu.KitManagementMenu;
import me.lrxh.practice.profile.Profile;
import me.lrxh.practice.profile.ProfileState;
import me.lrxh.practice.profile.hotbar.Hotbar;
import me.lrxh.practice.profile.hotbar.HotbarItem;
import me.lrxh.practice.util.CC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;

public class KitEditorListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getKitEditorData().isRenaming()) {
            event.setCancelled(true);

            if (event.getMessage().length() > 16) {
                event.getPlayer().sendMessage(Locale.KIT_EDITOR_NAME_TOO_LONG.format(event.getPlayer()));
                return;
            }

            String previousName = profile.getKitEditorData().getSelectedKitLoadout().getCustomName();
            String newName = CC.translate(event.getMessage());

            event.getPlayer().sendMessage(Locale.KIT_EDITOR_RENAMED.format(event.getPlayer(), previousName, newName));

            Kit selectedKit = profile.getKitEditorData().getSelectedKit();

            profile.getKitEditorData().setSelectedKit(null);
            profile.getKitEditorData().getSelectedKitLoadout().setCustomName(newName);
            profile.getKitEditorData().setActive(false);
            profile.getKitEditorData().setRename(false);

            if (profile.getState() != ProfileState.FIGHTING) {
                new KitManagementMenu(selectedKit).openMenu(event.getPlayer());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

            if (hotbarItem != null) {
                boolean cancelled = true;

                if (hotbarItem == HotbarItem.KIT_EDITOR) {
                    Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

                    if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
                        new KitEditorSelectKitMenu().openMenu(event.getPlayer());
                    }
                } else {
                    cancelled = false;
                }

                event.setCancelled(cancelled);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    return;
                }
            }

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getState() != ProfileState.FIGHTING && player.getGameMode() == GameMode.SURVIVAL) {
                Inventory clicked = event.getClickedInventory();

                if (profile.getKitEditorData().isActive()) {
                    if (clicked == null) {
                        event.setCancelled(true);
                        event.setCursor(null);
                        player.updateInventory();
                    } else if (clicked.equals(player.getOpenInventory().getTopInventory())) {
                        if (event.getCursor().getType() != Material.AIR &&
                                event.getCurrentItem().getType() == Material.AIR ||
                                event.getCursor().getType() != Material.AIR &&
                                        event.getCurrentItem().getType() != Material.AIR) {
                            event.setCancelled(true);
                            event.setCursor(null);
                            player.updateInventory();
                        }
                    }
                } else {
                    if (clicked != null && clicked.equals(player.getInventory())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
