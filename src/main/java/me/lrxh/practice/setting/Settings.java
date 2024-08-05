package me.lrxh.practice.setting;

import me.lrxh.practice.Practice;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Settings {

    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.SHOW_PLAYERS.ENABLED")) {
        SHOW_PLAYERS(Practice.getInstance().getMenusConfig().getString("SETTINGS.SHOW_PLAYERS.TITLE"), Material.COMPASS, Practice.getInstance().getMenusConfig().getString("SETTINGS.SHOW_PLAYERS.DESCRIPTION")),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.SHOW_SCOREBOARD.ENABLED")) {
        SHOW_SCOREBOARD(Practice.getInstance().getMenusConfig().getString("SETTINGS.SHOW_SCOREBOARD.TITLE"), Material.EYE_OF_ENDER, Practice.getInstance().getMenusConfig().getString("SETTINGS.SHOW_SCOREBOARD.DESCRIPTION")),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.ALLOW_SPECTATORS.ENABLED")) {
        ALLOW_SPECTATORS(Practice.getInstance().getMenusConfig().getString("SETTINGS.ALLOW_SPECTATORS.TITLE"), Material.EGG, "Allow players to spectate."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.ALLOW_DUELS.ENABLED")) {
        ALLOW_DUELS(Practice.getInstance().getMenusConfig().getString("SETTINGS.ALLOW_DUELS.TITLE"), Material.DIAMOND_SWORD, "Allow Duel Requests."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.KILL_EFFECTS.ENABLED")) {
        KILL_EFFECTS(Practice.getInstance().getMenusConfig().getString("SETTINGS.KILL_EFFECTS.TITLE"), Material.DIAMOND_AXE, "Select Kill Effect."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.THEME.ENABLED")) {
        THEME(Practice.getInstance().getMenusConfig().getString("SETTINGS.THEME.TITLE"), Material.BOOK, "Select Color Theme."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.PING_RANGE.ENABLED")) {
        PING_RANGE(Practice.getInstance().getMenusConfig().getString("SETTINGS.PING_RANGE.TITLE"), Material.STICK, "Change Ping Range."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.TIME_CHANGE.ENABLED")) {
        TIME_CHANGE(Practice.getInstance().getMenusConfig().getString("SETTINGS.TIME_CHANGE.TITLE"), Material.WATCH, "Change Ping Range."),
    }
    if (Practice.getInstance().getMenusConfig().getBoolean("SETTINGS.MENU_SOUNDS.ENABLED")) {
        MENU_SOUNDS(Practice.getInstance().getMenusConfig().getString("SETTINGS.MENU_SOUNDS.TITLE"), Material.REDSTONE_COMPARATOR, "Toggle Menu Sounds.");
    }

    private final String name;
    private final Material material;
    private final String description;

    Settings(String name, Material material, String description) {
        this.name = name;
        this.material = material;
        this.description = description;
    }
}
