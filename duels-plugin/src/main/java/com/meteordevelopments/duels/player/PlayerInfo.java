package com.meteordevelopments.duels.player;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import com.meteordevelopments.duels.util.PlayerUtil;
import com.meteordevelopments.duels.util.inventory.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

/**
 * Merged PlayerInfo:
 * - Keeps old behaviour (restoreExperience flag, overloads)
 * - Adds off-hand capture/restore from the new code
 */
@Getter
public class PlayerInfo {

    private final Map<String, Map<Integer, ItemStack>> items = new HashMap<>();
    private final List<PotionEffect> effects;
    private final double health;
    private final float experience;
    private final int level;
    private final int hunger;
    private final boolean restoreExperience; // from old code
    private final List<ItemStack> extra = new ArrayList<>();

    // NEW: persist off-hand item
    private final ItemStack offHand;

    @Setter
    private Location location;

    /**
     * Old-style base ctor, no off-hand parameter; assumes we don't know off-hand (sets to null)
     */
    public PlayerInfo(final List<PotionEffect> effects,
                      final double health,
                      final float experience,
                      final int level,
                      final int hunger,
                      final Location location,
                      final boolean restoreExperience) {
        this(effects, health, experience, level, hunger, location, restoreExperience, null);
    }

    /**
     * New merged base ctor with off-hand
     */
    public PlayerInfo(final List<PotionEffect> effects,
                      final double health,
                      final float experience,
                      final int level,
                      final int hunger,
                      final Location location,
                      final boolean restoreExperience,
                      final ItemStack offHand) {
        this.effects = effects;
        this.health = health;
        this.experience = experience;
        this.level = level;
        this.hunger = hunger;
        this.location = location;
        this.restoreExperience = restoreExperience;
        this.offHand = offHand;
    }

    /**
     * Old constructor: capture from player (restoreExperience defaults to true)
     */
    public PlayerInfo(final Player player, final boolean excludeInventory) {
        this(Lists.newArrayList(player.getActivePotionEffects()),
                player.getHealth(),
                player.getExp(),
                player.getLevel(),
                player.getFoodLevel(),
                player.getLocation().clone(),
                true,
                safeClone(player.getInventory().getItemInOffHand()));
        if (!excludeInventory) {
            InventoryUtil.addToMap(player.getInventory(), items);
        }
    }

    /**
     * Old constructor with explicit restoreExperience flag
     */
    public PlayerInfo(final Player player, final boolean excludeInventory, final boolean restoreExperience) {
        this(Lists.newArrayList(player.getActivePotionEffects()),
                player.getHealth(),
                player.getExp(),
                player.getLevel(),
                player.getFoodLevel(),
                player.getLocation().clone(),
                restoreExperience,
                safeClone(player.getInventory().getItemInOffHand()));
        if (!excludeInventory) {
            InventoryUtil.addToMap(player.getInventory(), items);
        }
    }

    public void restore(final Player player) {
        final double maxHealth = PlayerUtil.getMaxHealth(player);

        player.addPotionEffects(effects);
        player.setHealth(Math.min(health, maxHealth));

        if (restoreExperience) {
            player.setExp(experience);
            player.setLevel(level);
        }

        player.setFoodLevel(hunger);

        // Restore inventory contents & extras
        InventoryUtil.fillFromMap(player.getInventory(), items);
        InventoryUtil.addOrDrop(player, extra);

        // Restore off-hand if captured
        if (offHand != null) {
            player.getInventory().setItemInOffHand(offHand.clone());
        }

        player.updateInventory();
    }

    private static ItemStack safeClone(ItemStack stack) {
        return stack == null ? null : stack.clone();
    }
}
