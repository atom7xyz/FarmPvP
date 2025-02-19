package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlockPlaced;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlocksLife;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;

@Getter
public class FBlockPlaceEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private FPlayer fPlayer;
    private final Block block;
    private final Player player;
    private final BlockPlaceEvent blockPlaceEvent;
    private final BlockPlaced blockPlaced;

    public FBlockPlaceEvent(PlayersLife playersLife, BlocksLife blocksLife, BlockPlaceEvent event)
    {
        this.player = event.getPlayer();
        this.block = event.getBlock();
        this.blockPlaceEvent = event;
        this.blockPlaced = new BlockPlaced(block, player);

        val _player = playersLife.getPlayer(player);

        if (_player.isEmpty())
        {
            setBlockPlaceEventCancelled(true);
            setCancelled(true);
        }
        else
        {
            this.fPlayer = _player.get();

            if (!fPlayer.isEditing())
            {
                blocksLife.addPlaced(blockPlaced);
            }
        }
    }

    public void setBlockPlaceEventCancelled(boolean cancelled)
    {
        blockPlaceEvent.setCancelled(cancelled);
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
