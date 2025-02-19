package xyz.sorridi.farmpvp.modules.bounties.impl;

import lombok.Getter;
import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

@Getter
public class Bounty
{
    private final FPlayer from, target;
    private final int value;
    private final long timing;

    /**
     * Creates a new bounty.
     * @param from The player who placed the bounty.
     * @param target The player who is the target of the bounty.
     * @param value The value of the bounty.
     */
    public Bounty(@NonNull FPlayer from, @NonNull FPlayer target, int value)
    {
        this.from = from;
        this.target = target;
        this.value = value;
        this.timing = System.currentTimeMillis();
    }

    /**
     * Creates a new bounty.
     * @param from The player who placed the bounty.
     * @param target The player who is the target of the bounty.
     * @param value The value of the bounty.
     * @param timing The time when the bounty was placed.
     */
    public Bounty(@NonNull FPlayer from, @NonNull FPlayer target, int value, long timing)
    {
        this.from = from;
        this.target = target;
        this.value = value;
        this.timing = timing;
    }

    @Override
    public String toString()
    {
        return "Bounty{" +
                "from=" + from.getName() +
                ", target=" + target.getName() +
                ", value=" + value +
                ", timing=" + timing +
                '}';
    }

}
