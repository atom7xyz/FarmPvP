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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlockPlaced;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlocksLife;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;

@Getter
public class FBlockBreakEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private FPlayer fPlayer;
    private final Player player;
    private final BlockBreakEvent blockBreakEvent;
    private BlockPlaced blockPlaced;

    public FBlockBreakEvent(PlayersLife playersLife, BlocksLife blocksLife, BlockBreakEvent event)
    {
        this.player = event.getPlayer();

        Block block = event.getBlock();

        val _player = playersLife.getPlayer(player);
        val _blockPlaced = blocksLife.getBlockPlaced(block);

        this.blockBreakEvent = event;

        _blockPlaced.ifPresent(blockPlaced -> this.blockPlaced = blockPlaced);

        if (_player.isEmpty())
        {
            setBlockBreakEventCancelled(true);
            setCancelled(true);
        }
        else
        {
            this.fPlayer = _player.get();

            if (_blockPlaced.isPresent())
            {
                if (fPlayer.isEditing())
                {
                    blocksLife.removePlaced(blockPlaced);
                }
                else
                {
                    blocksLife.progressStage(block);
                    setBlockBreakEventCancelled(true);
                }
            }
            else
            {
                if (fPlayer.isEditing())
                {
                    block.setType(Material.AIR);
                }
                else
                {
                    setBlockBreakEventCancelled(true);
                    setCancelled(true);
                }
            }
        }

        blockBreakEvent.setExpToDrop(0);
    }

    public void setBlockBreakEventCancelled(boolean cancelled)
    {
        blockBreakEvent.setCancelled(cancelled);
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