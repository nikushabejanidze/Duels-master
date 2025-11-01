package com.meteordevelopments.duels.data;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.kit.KitImpl;
import com.meteordevelopments.duels.queue.Queue;
import com.meteordevelopments.duels.queue.sign.QueueSignImpl;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class QueueSignData {

    private LocationData location;
    private String queueName; // kept from old code
    private String kit;
    private int bet;
    private int teamSize; // kept from old code

    private QueueSignData() {
    }

    public QueueSignData(final QueueSignImpl sign) {
        this.location = LocationData.fromLocation(sign.getLocation());

        final Queue queue = sign.getQueue();
        this.queueName = queue.getName(); // preserve name-based queues
        this.kit = queue.getKit() != null ? queue.getKit().getName() : null;
        this.bet = queue.getBet();
        this.teamSize = queue.getTeamSize();
    }

    public QueueSignImpl toQueueSign(final DuelsPlugin plugin) {
        final Location location = this.location.toLocation();

        if (location.getWorld() == null) {
            return null;
        }

        final Block block = location.getBlock();
        if (!(block.getState() instanceof Sign)) {
            return null;
        }

        // Prefer name-based lookup (old behavior)
        Queue queue = plugin.getQueueManager().getByName(queueName);

        if (queue == null) {
            // Fallback: kit + bet (new code behavior)
            final KitImpl kitObj = this.kit != null ? plugin.getKitManager().get(this.kit) : null;
            queue = plugin.getQueueManager().get(kitObj, bet);

            if (queue == null) {
                // Create queue if still missing; keep old fields (name, team size) for backward compat
                plugin.getQueueManager().create(
                        null,
                        queueName != null ? queueName : "Unnamed",
                        kitObj,
                        bet,
                        teamSize <= 0 ? 1 : teamSize
                );
                queue = plugin.getQueueManager().getByName(queueName);
            }
        }

        // Add emoji support (from new code) — include "emoji" placeholder in message
        final String text = plugin.getLang().getMessage(
                "SIGN.format",
                "name", queueName != null ? queueName : "Unnamed",
                "kit", this.kit != null ? this.kit : plugin.getLang().getMessage("GENERAL.none"),
                "bet_amount", bet,
                "emoji", getEmoji()
        );

        // Keep old constructor signature that passes Lang
        return new QueueSignImpl(location, text, queue, plugin.getLang());
    }

    private String getEmoji() {
        if (this.kit == null) {
            return ChatColor.YELLOW + "\u270e"; // pencil
        }
        if (this.kit.equals("Sword")) {
            return ChatColor.BLUE + "\ud83d\udde1"; // dagger
        }
        if (this.kit.equals("Diamond Pot")) {
            return ChatColor.DARK_RED + "\u2697"; // alembic
        }
        if (this.kit.equals("Netherite Pot")) {
            return ChatColor.RED + "\u26e8"; // black droplet
        }
        if (this.kit.equals("Axe")) {
            return ChatColor.BLUE + "\ud83e\ude93"; // axe
        }
        if (this.kit.equals("ClassicUHC")) {
            return ChatColor.DARK_RED + "\u2764"; // heart
        }
        if (this.kit.equals("CrazyUHC")) {
            return ChatColor.GOLD + "\ud83d\udc51"; // crown
        }
        if (this.kit.equals("SMP")) {
            return ChatColor.DARK_GREEN + "\u26cf"; // pickaxe
        }
        return "";
    }
}
