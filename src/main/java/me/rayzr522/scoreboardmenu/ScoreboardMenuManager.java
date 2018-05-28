package me.rayzr522.scoreboardmenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The manager for opening and closing {@link ScoreboardMenu ScoreboardMenus}, as well as handling user input.
 *
 * @author Rayzr522
 */
public class ScoreboardMenuManager implements Listener {
    private Map<UUID, ScoreboardMenu> openMenus = new HashMap<>();

    ScoreboardMenuManager(ScoreboardMenuPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void closeAll() {
        openMenus.keySet().iterator().forEachRemaining(this::closeIfOpen);
    }

    /**
     * Opens a menu for a player.
     *
     * @param player The player to open the menu for.
     * @param menu   The menu to open.
     * @param <T>    The type of the option.
     */
    public <T> void openMenu(Player player, ScoreboardMenu<T> menu) {
        closeIfOpen(player.getUniqueId());
        openMenus.put(player.getUniqueId(), menu);
        menu.render(player);
    }

    /**
     * Checks to see if the player is currently in a menu.
     *
     * @param player The player to check.
     * @return Whether or not the player is currently in a menu.
     */
    public boolean hasMenu(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }

    /**
     * Closes an open menu.
     *
     * @param player The player who should have their menu closed.
     */
    public void closeMenu(Player player) {
        closeIfOpen(player.getUniqueId());
    }

    private void closeIfOpen(UUID id) {
        if (openMenus.containsKey(id)) {
            openMenus.remove(id);

            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        closeIfOpen(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!openMenus.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }

        e.setCancelled(true);
        Player player = e.getPlayer();
        ScoreboardMenu menu = openMenus.get(player.getUniqueId());

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                menu.execute();
                closeIfOpen(player.getUniqueId());
            } else {
                menu.nextEntry();
                menu.render(player);
            }
        } else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getHand() != EquipmentSlot.HAND) {
                return;
            }

            menu.previousEntry();
            menu.render(player);
        }
    }
}
