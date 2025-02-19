package xyz.sorridi.farmpvp.modules.player.impl;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.text3.Text;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.menus.guis.FlagMenu;
import xyz.sorridi.stone.utils.PlaySound;
import xyz.sorridi.stone.utils.nums.NumberOps;
import xyz.sorridi.stone.utils.string.StringFormatter;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class FPlayer
{
    private String name;
    private UUID uuid;

    private Player player;
    private Flag flag;

    private int coins;
    private int playTime;
    private int kills;
    private int mobKills;
    private int deaths;
    private int bannersDestroyed;

    private int teleportIn = -1;
    private int respawnIn = -1;

    private long firstSeen;
    private long lastLogin;
    private long lastLogout;

    private boolean ignorePings;
    private boolean teamChatEnabled;
    private boolean dead;
    private boolean editing;

    private ItemStack[] editingInv;
    private ItemStack[] preEditingInv;
    private ItemStack[] preEditingArmor;

    private ItemStack[] preStaffModeInv;
    private ItemStack[] preStaffModeArmor;

    @Nullable
    private FlagMenu lastOpenMenu;

    /**
     * Creates a new FPlayer.
     * @param player The bukkit player.
     */
    public FPlayer(Player player)
    {
        initDefaultScores();

        this.player = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    /**
     * Creates a new FPlayer.
     * @param name The name of the player.
     * @param uuid The uuid of the player.
     * @param levels The flag-levels of the player.
     * @param points The flag-points of the player.
     * @param coins The coins of the player.
     * @param playTime The play time of the player.
     * @param kills The human-kills of the player.
     * @param mobKills The mob-kills of the player.
     * @param deaths The deaths of the player.
     * @param bannersDestroyed The banners destroyed of the player.
     * @param firstSeen The first seen of the player, in millis.
     * @param lastLogin The last login of the player, in millis.
     * @param lastLogout The last logout of the player, in millis.
     * @param ignorePings If the player ignores pings.
     */
    public FPlayer(
            String name,
            UUID uuid,
            int levels,
            double points,
            int coins,
            int playTime,
            int kills,
            int mobKills,
            int deaths,
            int bannersDestroyed,
            long firstSeen,
            long lastLogin,
            long lastLogout,
            boolean ignorePings
    ) {
        initDefaultScores();

        this.name = name;
        this.uuid = uuid;
        this.flag = new Flag(this, levels, points);
        this.coins = coins;
        this.playTime = playTime;
        this.kills = kills;
        this.mobKills = mobKills;
        this.deaths = deaths;
        this.bannersDestroyed = bannersDestroyed;
        this.firstSeen = firstSeen;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
        this.ignorePings = ignorePings;
    }

    /**
     * Initializes the default scores.
     */
    private void initDefaultScores()
    {
        this.flag = new Flag(this, 0, 0);
        this.coins = 0;
        this.playTime = 0;
        this.kills = 0;
        this.deaths = 0;
        this.bannersDestroyed = 0;
        this.mobKills = 0;

        this.firstSeen = System.currentTimeMillis();
        this.lastLogin = 0;
        this.lastLogout = 0;
    }

    /**
     * Adds points to the player.
     * @param points Points to add.
     */
    public void addPoints(int points)
    {
        flag.addPoints(points);
    }

    /**
     * Adds points to the player and plays a Sound.
     * @param points Points to add.
     * @param sound Sound to play.
     */
    public void addPoints(int points, boolean playSound, Sound sound)
    {
        flag.addPoints(points);

        if (playSound && isOnline())
        {
            PlaySound.play(player, sound);
        }
    }

    /**
     * Adds levels to the player.
     * @param levels Levels to add.
     */
    public void addLevels(int levels)
    {
        flag.addLevels(levels);
    }

    /**
     * Adds coins to the player.
     * @return The coins of the player.
     */
    public double getPoints()
    {
        return flag.getPoints();
    }

    /**
     * Gets the levels of the player.
     * @return The levels of the player.
     */
    public int getLevels()
    {
        return flag.getLevels();
    }

    /**
     * Adds coins to the player.
     * @param coins Coins to add.
     */
    public void addCoins(int coins)
    {
        this.coins += Math.max(0, coins);
    }

    /**
     * Removes coins to the player.
     * @param coins Coins to remove.
     */
    public void removeCoins(int coins)
    {
        this.coins = Math.max(0, this.coins - coins);
    }

    /**
     * Resets the coins of the player.
     */
    public void resetCoins()
    {
        this.coins = 0;
    }

    /**
     * Teleports the player to the flag, if present.
     * @return If the player was teleported.
     */
    public boolean teleportToFlag()
    {
        Optional<Location> _location = flag.getLocation();;

        if (isOnline())
        {
            _location.ifPresent(location -> PaperLib.teleportAsync(player.getPlayer(), location));
        }

        return isOnline() && _location.isPresent();
    }

    /**
     * Teleports the player to his current spawn point.
     */
    public void teleportToSpawnPoint()
    {
        if (!teleportToFlag())
        {
            PaperLib.teleportAsync(player.getPlayer(), FarmPvP.spawnPoint);
        }
    }

    /**
     * Checks if the flag has enough coins.
     * @param coins Coins to check.
     * @return If the flag has enough coins.
     */
    public boolean hasCoins(int coins)
    {
        return this.coins >= coins;
    }

    public void setEditing(boolean editing)
    {
        if (this.editing == editing)
        {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        if (editing)
        {
            preEditingInv = inventory.getContents();
            preEditingArmor = inventory.getArmorContents();

            inventory.setArmorContents(null);

            if (editingInv == null)
            {
                inventory.clear();
            }
            else
            {
                inventory.setContents(editingInv);
            }
        }
        else
        {
            editingInv = inventory.getContents();

            inventory.setContents(preEditingInv);
            inventory.setArmorContents(preEditingArmor);
        }

        player.setGameMode(editing ? GameMode.CREATIVE : GameMode.SURVIVAL);

        this.editing = editing;
    }

    public void decrementTeleportIn()
    {
        --teleportIn;
    }

    public void decrementRespawnIn()
    {
        --respawnIn;
    }

    /**
     * Sends a message to the player.
     * @param message Message to send.
     */
    public void reply(String... message)
    {
        if (isOnline())
        {
            for (String s : message)
            {
                player.sendMessage(Text.colorize(s));
            }
        }
    }

    /**
     * Sends messages to the player.
     * @param messages Messages to send.
     */
    public <C extends Collection<String>> void reply(C messages)
    {
        if (isOnline())
        {
            for (String s : messages)
            {
                player.sendMessage(Text.colorize(s));
            }
        }
    }

    /**
     * Checks if the player is online.
     * @return If the player is online.
     */
    public boolean isOnline()
    {
        return player != null && player.isOnline();
    }

    /**
     * Checks if the player has a permission.
     * @param permission Permission to check.
     * @return If the player has the permission.
     */
    public boolean hasPermission(String permission)
    {
        return player != null && player.hasPermission(permission);
    }

    /**
     * Checks if the player has not a permission.
     * @param permission Permission to check.
     * @return If the player has not the permission.
     */
    public boolean hasNotPermission(String permission)
    {
        return !hasPermission(permission);
    }

    /**
     * Gets the player K/D ratio.
     * @return The player K/D ratio.
     */
    public double getKD()
    {
        return NumberOps.safeDiv(kills, deaths);
    }

    /**
     * Gets the player K/D ratio formatted.
     * @return The player K/D ratio formatted.
     */
    public String getFormattedKD()
    {
        return StringFormatter.formatDouble(getKD());
    }

    /**
     * Gets the player points formatted.
     * @return The player points formatted.
     */
    public String getFormattedPoints()
    {
        double points = getPoints();
        return points == 0 ? "0" : NumberOps.shorten(getPoints());
    }

    public void addItem(ItemStack ...items)
    {
        getInventory().addItem(items);
    }

    public PlayerInventory getInventory()
    {
        return player.getInventory();
    }

    public void closeInventory()
    {
        player.closeInventory();
    }

    public void redrawLastOpenMenu()
    {
        if (lastOpenMenu != null)
        {
            lastOpenMenu.redraw();
        }
    }

    @Override
    public String toString()
    {
        return "FPlayer{" +
                "name=" + name +
                ", uuid=" + uuid +
                ", player=" + player +
                ", entityId=" + player.getEntityId() +
                ", flag=" + flag +
                ", coins=" + coins +
                ", playTime=" + playTime +
                ", kills=" + kills +
                ", mobKills=" + mobKills +
                ", deaths=" + deaths +
                ", kd=" + getKD() +
                ", bannersDestroyed=" + bannersDestroyed +
                ", respawnIn=" + respawnIn +
                ", firstSeen=" + firstSeen +
                ", lastLogin=" + lastLogin +
                ", lastLogout=" + lastLogout +
                ", ignorePings=" + ignorePings +
                ", teamChatEnabled=" + teamChatEnabled +
                ", dead=" + dead +
                '}';
    }

}
