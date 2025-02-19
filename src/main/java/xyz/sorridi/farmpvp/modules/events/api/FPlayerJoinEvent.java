package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

@Getter
public class FPlayerJoinEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final FPlayer fPlayer;
    private final Player player;

    public FPlayerJoinEvent(FPlayer fPlayer)
    {
        this.fPlayer = fPlayer;
        this.player = fPlayer.getPlayer();

        fPlayer.setLastLogin(System.currentTimeMillis());
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
