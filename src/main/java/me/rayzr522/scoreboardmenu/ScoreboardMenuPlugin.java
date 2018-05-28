package me.rayzr522.scoreboardmenu;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A simple plugin for creating menus via {@link org.bukkit.scoreboard.Scoreboard Scoreboards}.
 *
 * @author Rayzr522
 * @see ScoreboardMenu
 */
public class ScoreboardMenuPlugin extends JavaPlugin implements Listener {
    private static ScoreboardMenuPlugin instance;

    private ScoreboardMenuManager menuManager;

    /**
     * Gets the currently running instance of {@link ScoreboardMenuPlugin}.
     *
     * @return The current instance.
     */
    public static ScoreboardMenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        menuManager = new ScoreboardMenuManager(this);
    }

    @Override
    public void onDisable() {
        menuManager.closeAll();
        instance = null;
    }

    /**
     * The current {@link ScoreboardMenuManager} instance.
     *
     * @return The current menu manager.
     */
    public ScoreboardMenuManager getMenuManager() {
        return menuManager;
    }
}
