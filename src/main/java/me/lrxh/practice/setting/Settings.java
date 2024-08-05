package me.lrxh.practice.setting;

import me.lrxh.practice.Practice;
import me.lrxh.practice.util.config.BasicConfigurationFile;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Settings {
    private BasicConfigurationFile menuConfig = Practice.getInstance().getMenusConfig();

    if (menuConfig.getBoolean("SETTINGS.SHOW_PLAYERS.ENABLED")) {
        SHOW_PLAYERS(menuConfig.getString("SETTINGS.SHOW_PLAYERS.TITLE"), Material.COMPASS, menuConfig.getString("SETTINGS.SHOW_PLAYERS.DESCRIPTION")),
    }

    if (menuConfig.getBoolean("SETTINGS.SHOW_SCOREBOARD.ENABLED")) {
        SHOW_SCOREBOARD(menuConfig.getString("SETTINGS.SHOW_SCOREBOARD.TITLE"), Material.EYE_OF_ENDER, menuConfig.getString("SETTINGS.SHOW_SCOREBOARD.DESCRIPTION")),
    }

    if (menuConfig.getBoolean("SETTINGS.ALLOW_SPECTATORS.ENABLED")) {
        ALLOW_SPECTATORS(menuConfig.getString("SETTINGS.ALLOW_SPECTATORS.TITLE"), Material.EGG, "Allow players to spectate."),
    }

    if (menuConfig.getBoolean("SETTINGS.ALLOW_DUELS.ENABLED")) {
        ALLOW_DUELS(menuConfig.getString("SETTINGS.ALLOW_DUELS.TITLE"), Material.DIAMOND_SWORD, "Allow Duel Requests."),
    }

    if (menuConfig.getBoolean("SETTINGS.KILL_EFFECTS.ENABLED")) {
        KILL_EFFECTS(menuConfig.getString("SETTINGS.KILL_EFFECTS.TITLE"), Material.DIAMOND_AXE, "Select Kill Effect."),
    }

    if (menuConfig.getBoolean("SETTINGS.THEME.ENABLED")) {
        THEME(menuConfig.getString("SETTINGS.THEME.TITLE"), Material.BOOK, "Select Color Theme."),
    }

    if (menuConfig.getBoolean("SETTINGS.PING_RANGE.ENABLED")) {
        PING_RANGE(menuConfig.getString("SETTINGS.PING_RANGE.TITLE"), Material.STICK, "Change Ping Range."),
    }

    if (menuConfig.getBoolean("SETTINGS.TIME_CHANGE.ENABLED")) {
        TIME_CHANGE(menuConfig.getString("SETTINGS.TIME_CHANGE.TITLE"), Material.WATCH, "Change Ping Range."),
    }

    if (menuConfig.getBoolean("SETTINGS.MENU_SOUNDS.ENABLED")) {
        MENU_SOUNDS(menuConfig.getString("SETTINGS.MENU_SOUNDS.TITLE"), Material.REDSTONE_COMPARATOR, "Toggle Menu Sounds.");
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
