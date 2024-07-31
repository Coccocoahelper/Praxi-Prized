package me.lrxh.practice.util;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ga.windpvp.windspigot.knockback.KnockbackConfig;

import java.lang.reflect.Field;

public class KnockbackUtil {

    public void setKnockback(Player player, String kb) {
        KnockbackProfile knockbackProfile = KnockbackConfig.getKbProfileByName(kb);
        EntityHuman entityPlayer =  ((CraftPlayer) player).getHandle();
        Class entityclass = entityPlayer.getClass().getSuperclass();

        try {
            Field filed = entityclass.getDeclaredField("knockback");
            filed.setAccessible(true);
            filed.set(entityPlayer, knockbackProfile);
            filed.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
