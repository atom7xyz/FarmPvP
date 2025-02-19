package xyz.sorridi.farmpvp.modules.events.api;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import me.lucko.helper.Services;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.sorridi.farmpvp.modules.papi.HoldersModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;

@Getter
public class LevelChangeEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();

    @Setter
    private boolean cancelled;

    private final FPlayer fPlayer;
    private final Flag flag;

    private final int from, to;

    public LevelChangeEvent(FPlayer fPlayer, int level, Operation operation)
    {
        this.fPlayer    = fPlayer;
        this.flag       = fPlayer.getFlag();
        this.from       = flag.getLevels();

        switch (operation)
        {
            case ADD    -> this.to = from + level;
            case REMOVE -> this.to = from - level;
            case SET    -> this.to = level;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        }

        flag.setLevels(to);

        val _holdersModule = Services.get(HoldersModule.class);
        _holdersModule.ifPresent(module -> module.getHoldersLife().assignColor(flag));
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

    public enum Operation
    {
        ADD,
        REMOVE,
        SET,
    }

}
