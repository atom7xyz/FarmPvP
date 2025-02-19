package xyz.sorridi.farmpvp.modules.blocks.impl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.sorridi.stone.utils.bukkit.location.LocationEvaluate;

@Getter
public class BlockPlaced
{
    private final Block block;
    private final Player player;
    private final long timing;

    private byte breakStage = -1;

    /**
     * Creates a new BlockPlaced.
     * @param block The block placed.
     */
    public BlockPlaced(Block block, Player player)
    {
        this.block  = block;
        this.player = player;
        this.timing = System.currentTimeMillis();
    }

    /**
     * Sets the material of the block.
     * @param material The material to set.
     */
    public void setMaterial(Material material)
    {
        block.setType(material);
    }

    public boolean isMaterial(Material material)
    {
        return block.getType() == material;
    }

    /**
     * Gets the material of the block.
     * @return The material of the block.
     */
    public Material getMaterial()
    {
        return block.getType();
    }

    /**
     * Gets the location of the block.
     * @return The location of the block.
     */
    public Location getLocation()
    {
        return block.getLocation();
    }

    public Vector getVector()
    {
        return getLocation().toVector();
    }

    /**
     * Gets the middle location of the block.
     * @return The middle location of the block.
     */
    public Location getMiddleLocation()
    {
        return LocationEvaluate.getMiddleLocation(getLocation(), false);
    }

    /**
     * Gets the X coordinate of the block.
     * @return The X coordinate of the block.
     */
    public int getX()
    {
        return getLocation().getBlockX();
    }

    /**
     * Gets the Y coordinate of the block.
     * @return The Y coordinate of the block.
     */
    public int getY()
    {
        return getLocation().getBlockY();
    }

    /**
     * Gets the Z coordinate of the block.
     * @return The Z coordinate of the block.
     */
    public int getZ()
    {
        return getLocation().getBlockZ();
    }

    public int getAndIncrementBreakStage()
    {
        return ++breakStage;
    }

}