package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;

@Getter
public class FlagBreakEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final BlockBreakEvent blockBreakEvent;

    @Setter
    private boolean cancelled;

    private final FPlayer victim;
    private final FPlayer destroyer;

    private final Flag flag;
    private final Flag destroyerFlag;

    // TODO
    public FlagBreakEvent(PlayersLife playersLife, BlockBreakEvent event)
    {
        this.victim             = null;
        this.destroyer          = null;
        this.flag               = null;
        this.destroyerFlag      = null;
        this.blockBreakEvent    = event;
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