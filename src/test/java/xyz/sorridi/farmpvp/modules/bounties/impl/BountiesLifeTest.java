package xyz.sorridi.farmpvp.modules.bounties.impl;

import org.junit.jupiter.api.Test;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BountiesLifeTest
{
    static FPlayer player = new FPlayer("Sorridi", UUID.randomUUID(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);
    static FPlayer target = new FPlayer("GudCheat", UUID.randomUUID(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);

    @Test
    void addBounty()
    {
        BountiesLife bountiesLife = new BountiesLife();
        Optional<Bounty> bounty;
        Optional<List<Bounty>> bounties;

        bountiesLife.memorize(player);
        bountiesLife.memorize(target);

        /*
         * Prima bounty.
         */
        assertTrue(bountiesLife.addBounty(new Bounty(player, target, 100)));

        bounty = bountiesLife.getAssignedBounty(player);

        assertTrue(bounty.isPresent());

        assertEquals(100, bounty.get().getValue());
        assertEquals(target, bounty.get().getTarget());
        assertEquals(player, bounty.get().getFrom());

        bounties = bountiesLife.getBountiesAssignedTo(target);

        assertTrue(bounties.isPresent());

        assertEquals(100, bounties.get().get(0).getValue());
        assertEquals(target, bounties.get().get(0).getTarget());
        assertEquals(player, bounties.get().get(0).getFrom());

        /*
         * Seconda bounty.
         */
        assertFalse(bountiesLife.addBounty(new Bounty(player, target, 200)));

        bounty = bountiesLife.getAssignedBounty(player);

        assertTrue(bounty.isPresent());

        assertEquals(100, bounty.get().getValue());
        assertEquals(target, bounty.get().getTarget());
        assertEquals(player, bounty.get().getFrom());

        bounties = bountiesLife.getBountiesAssignedTo(target);

        assertTrue(bounties.isPresent());

        assertEquals(100, bounties.get().get(0).getValue());
        assertEquals(target, bounties.get().get(0).getTarget());
        assertEquals(player, bounties.get().get(0).getFrom());

        assertEquals(1, bounties.get().size());
    }

    @Test
    void removeBounty()
    {
        BountiesLife bountiesLife = new BountiesLife();

        bountiesLife.memorize(player);
        bountiesLife.memorize(target);

        assertEquals(Optional.empty(), bountiesLife.removeBounty(player));

        Bounty bounty = new Bounty(player, target, 100);

        assertTrue(bountiesLife.addBounty(bounty));
        assertEquals(Optional.of(bounty), bountiesLife.removeBounty(player));

        assertEquals(Optional.empty(), bountiesLife.getAssignedBounty(player));
        assertEquals(Optional.empty(), bountiesLife.getBountiesAssignedTo(target));
    }

    @Test
    void takeBounty()
    {
        BountiesLife bountiesLife = new BountiesLife();

        bountiesLife.memorize(player);
        bountiesLife.memorize(target);

        bountiesLife.addBounty(new Bounty(player, target, 100));

        bountiesLife.takeBounties(player, target);
        assertEquals(100, player.getPoints());

        assertEquals(Optional.empty(), bountiesLife.getAssignedBounty(player));
        assertEquals(Optional.empty(), bountiesLife.getBountiesAssignedTo(target));

        assertEquals(Optional.empty(), bountiesLife.takeBounties(player, target));
        assertEquals(100, player.getPoints());
    }

}