package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlockPlaced;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

@Getter
public class FlagPlaceEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final FPlayer fPlayer;
    private final Player player;
    private final BlockPlaced blockPlaced;
    private final BlockPlaceEvent blockPlaceEvent;

    public FlagPlaceEvent(FPlayer fPlayer, BlockPlaceEvent event)
    {
        this.fPlayer            = fPlayer;
        this.player             = event.getPlayer();
        this.blockPlaced        = new BlockPlaced(event.getBlock(), event.getPlayer());
        this.blockPlaceEvent    = event;
    }

    public Block getBlock()
    {
        return blockPlaced.getBlock();
    }

    public Material getMaterial()
    {
        return blockPlaced.getMaterial();
    }

    public BlockState getState()
    {
        return getBlock().getState();
    }

    public MaterialData getMaterialData()
    {
        return getState().getData();
    }

    public Location getLocation()
    {
        return blockPlaced.getLocation();
    }

    public Location getMiddleLocation()
    {
        return blockPlaced.getMiddleLocation();
    }

    public World getWorld()
    {
        return getBlock().getWorld();
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

}