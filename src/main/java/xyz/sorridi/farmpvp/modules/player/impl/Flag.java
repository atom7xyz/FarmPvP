package xyz.sorridi.farmpvp.modules.player.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
public class Flag
{
    @Setter
    private FPlayer owner;

    @Setter
    private ItemStack item;

    @Setter
    private Block block;

    private double points;
    private int levels;

    @Setter
    private String levelsColor;

    /**
     * Creates a new flag with default levels and points and with no owner.
     */
    public Flag()
    {
        initDefaultScores();
    }

    /**
     * Creates a new flag with the specified levels and points and with an owner.
     * @param fPlayer Owner of the flag.
     * @param levels levels of the flag.
     * @param points Points of the flag.
     */
    public Flag(FPlayer fPlayer, int levels, double points)
    {
        initDefaultScores();

        this.owner = fPlayer;
        this.levels = levels;
        this.points = points;
    }

    private void initDefaultScores()
    {
        this.item = new ItemStack(Material.STANDING_BANNER);
        this.points = 0;
        this.levels = 0;
    }

    /**
     * Adds levels to the flag.
     * @param levels Levels to add.
     */
    public void addLevels(int levels)
    {
        this.levels += Math.max(0, levels);
    }

    /**
     * Adds points to the flag.
     * @param points Points to add.
     */
    public void addPoints(double points)
    {
        this.points += Math.max(0, points);
    }

    /**
     * Sets the levels of the flag.
     * @param levels levels to set.
     */
    public void setLevels(int levels)
    {
        this.levels = Math.max(0, levels);
    }

    /**
     * Sets the points of the flag.
     * @param points Points to set.
     */
    public void setPoints(double points)
    {
        this.points = Math.max(0, points);
    }

    /**
     * Removes levels from the flag.
     * @param levels Levels to remove.
     */
    public void removeLevels(int levels)
    {
        this.levels = Math.max(0, this.levels - levels);
    }

    /**
     * Removes points from the flag.
     * @param points Points to remove.
     */
    public void removePoints(double points)
    {
        this.points = Math.max(0, this.points - points);
    }

    /**
     * Checks if the flag has enough points.
     * @param points Points to check.
     * @return If the flag has enough points.
     */
    public boolean hasPoints(double points)
    {
        return this.points >= points;
    }

    /**
     * Checks if the flag has enough levels.
     * @param levels Levels to check.
     * @return If the flag has enough levels.
     */
    public boolean hasLevels(int levels)
    {
        return this.levels >= levels;
    }

    /**
     * Resets the points of the flag.
     */
    public void resetPoints()
    {
        this.points = 0;
    }

    /**
     * Resets the levels of the flag.
     */
    public void resetLevels()
    {
        this.levels = 0;
    }

    /**
     * Checks if the flag is placed.
     * @return If the flag is placed.
     */
    public boolean isPlaced()
    {
        return block != null;
    }

    /**
     * Forces the flag to break.
     */
    public void forceBreak()
    {
        if (isPlaced())
        {
            block.setType(Material.AIR);
            block = null;
        }
    }

    /**
     * Gets the location of the flag.
     * @return Location of the flag.
     */
    public Optional<Location> getLocation()
    {
        return Optional.ofNullable(block).map(Block::getLocation);
    }

    @Override
    public String toString()
    {
        return "Flag{" +
                "owner=" + owner.getName() +
                ", item=" + item +
                ", block=" + block +
                ", points=" + points +
                ", levels=" + levels +
                ", levelsColor=" + levelsColor +
                '}';
    }

}
