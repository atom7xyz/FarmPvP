package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

@Getter
public class FChatEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final FPlayer fPlayer;
    private final Player player;
    private final AsyncPlayerChatEvent asyncChatEvent;

    private final String message;

    public FChatEvent(AsyncPlayerChatEvent asyncChatEvent, FPlayer fPlayer)
    {
        this.fPlayer        = fPlayer;
        this.asyncChatEvent = asyncChatEvent;
        this.message        = asyncChatEvent.getMessage();
        this.player         = fPlayer.getPlayer();
    }

    public void setAsyncChatEventCancelled(boolean cancelled)
    {
        asyncChatEvent.setCancelled(cancelled);
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
