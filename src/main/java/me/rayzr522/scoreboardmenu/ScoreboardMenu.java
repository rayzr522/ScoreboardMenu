package me.rayzr522.scoreboardmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A Scoreboard menu.
 *
 * @param <T> The type of the option.
 * @author Rayzr522
 */
public class ScoreboardMenu<T> {
    private final List<T> options = new ArrayList<>();
    private int current = 0;

    private Consumer<T> callback = null;
    private Function<T, String> renderTransformer = Objects::toString;
    private String selectedPrefix = "" + ChatColor.GREEN;
    private String otherPrefix = "" + ChatColor.WHITE;
    private String title = "Select";

    /**
     * Gets the current menu callback.
     *
     * @return The callback that is called when an item is selected.
     */
    public Consumer<T> getCallback() {
        return callback;
    }

    /**
     * Sets the callback of the menu.
     *
     * @param callback The new callback to use.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> setCallback(Consumer<T> callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Gets the current render transformer for display options. This is what is used to determine the name to use for the fake players in the {@link Scoreboard}.
     *
     * @return The current render transformer.
     */
    public Function<T, String> getRenderTransformer() {
        return renderTransformer;
    }

    /**
     * Sets the current render transformer for display options.
     *
     * @param renderTransformer The new render transformer to use.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> setRenderTransformer(Function<T, String> renderTransformer) {
        this.renderTransformer = renderTransformer;
        return this;
    }

    /**
     * Gets the prefix for the currently selected option.
     *
     * @return The selected prefix.
     */
    public String getSelectedPrefix() {
        return selectedPrefix;
    }

    /**
     * Sets the prefix for the currently selected option.
     *
     * @param selectedPrefix The new prefix to use.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> setSelectedPrefix(String selectedPrefix) {
        this.selectedPrefix = selectedPrefix;
        return this;
    }

    /**
     * Gets the prefix for options other than the currently selected one.
     *
     * @return The other prefix.
     */
    public String getOtherPrefix() {
        return otherPrefix;
    }

    /**
     * Sets the prefix for options other than the currently selected one.
     *
     * @param otherPrefix The new prefix to use.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> setOtherPrefix(String otherPrefix) {
        this.otherPrefix = otherPrefix;
        return this;
    }

    /**
     * Gets the title of the scoreboard.
     *
     * @return The title of the scoreboard.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the scoreboard.
     *
     * @param title The title to set.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Adds a single option to the menu.
     *
     * @param option The option to add.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> addOption(T option) {
        options.add(option);
        return this;
    }

    /**
     * Adds a list of options to the menu.
     *
     * @param options The options to add.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> addAll(T... options) {
        this.options.addAll(Arrays.asList(options));
        return this;
    }

    /**
     * Gets all the current options.
     *
     * @return The list of all menu options.
     */
    public List<T> getOptions() {
        return options;
    }

    /**
     * Opens the menu for a player.
     *
     * @param player The player to open the menu for.
     * @return This {@link ScoreboardMenu} instance.
     */
    public ScoreboardMenu<T> openFor(Player player) {
        ScoreboardMenuPlugin.getInstance().getMenuManager().openMenu(player, this);
        return this;
    }

    /**
     * Cycles to the next entry.
     */
    public void nextEntry() {
        current = (current + 1) % options.size();
    }

    /**
     * Cycles to the previous entry.
     */
    public void previousEntry() {
        current = (current > 0 ? current : options.size()) - 1;
    }

    /**
     * Renders the Scoreboard for a specific player.
     *
     * @param player The {@link Player} to render the menu for.
     */
    public void render(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("selection", "dummy");

        int length = options.size();
        int displayLength = Math.min(length, 10);

        for (int i = 0; i < displayLength; i++) {
            T next = options.get((i + current) % length);
            String display = renderTransformer.apply(next);
            if (i == 0) {
                display = selectedPrefix + display;
            } else {
                display = otherPrefix + display;
            }
            objective.getScore(display).setScore(displayLength - i);
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);

        player.setScoreboard(scoreboard);
    }

    /**
     * Executes the callback with the currently selected option.
     */
    public void execute() {
        if (callback != null) {
            callback.accept(options.get(current));
        }
    }
}
