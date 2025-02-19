package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.teams.impl.Team;

@Getter
public class TeamChatEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final FPlayer fPlayer;
    private final Team team;

    private final String message;

    public TeamChatEvent(FPlayer fPlayer, Team team, String message)
    {
        this.fPlayer    = fPlayer;
        this.team       = team;
        this.message    = message;
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
