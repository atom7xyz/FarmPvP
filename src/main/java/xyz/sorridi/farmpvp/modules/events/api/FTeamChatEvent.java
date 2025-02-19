package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

@Getter
public class FTeamChatEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final FPlayer fPlayer;
    private final Player player;
    private final FChatEvent fChatEvent;

    private final String message;

    public FTeamChatEvent(FChatEvent fChatEvent, FPlayer fPlayer)
    {
        this.fPlayer    = fPlayer;
        this.fChatEvent = fChatEvent;
        this.player     = fPlayer.getPlayer();
        this.message    = fChatEvent.getMessage();

        fChatEvent.setAsyncChatEventCancelled(true);
    }

    public void setFChatEventCancelled(boolean cancelled)
    {
        fChatEvent.setCancelled(cancelled);
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

}