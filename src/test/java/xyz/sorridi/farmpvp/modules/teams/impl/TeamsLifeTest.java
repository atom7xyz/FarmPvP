package xyz.sorridi.farmpvp.modules.teams.impl;

import org.junit.jupiter.api.Test;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TeamsLifeTest
{
    static FPlayer player = new FPlayer("Sorridi", UUID.randomUUID(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);
    static FPlayer target = new FPlayer("GudCheat", UUID.randomUUID(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);

    @Test
    void createTeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));
        assertFalse(teamsLife.createTeam(player));

        assertTrue(teamsLife.isInATeam(player));
    }

    @Test
    void disbandTeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));
        assertTrue(teamsLife.isInATeam(player));

        //assertFalse(teamsLife.disbandTeam(target));
        //assertTrue(teamsLife.disbandTeam(player));

        assertFalse(teamsLife.isInATeam(player));
    }

    @Test
    void getTeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));
        assertTrue(teamsLife.getTeam(player).isPresent());
    }

    @Test
    void acceptInvite()
    {
        TeamsLife teamsLife = new TeamsLife();
        assertTrue(teamsLife.createTeam(player));

        assertTrue(teamsLife.invite(player, target));
        assertFalse(teamsLife.isInATeam(target));

        assertEquals(teamsLife.getTeam(player), teamsLife.acceptInvite(target));
        assertTrue(teamsLife.isInATeam(target));
    }

    @Test
    void denyInvite()
    {
        TeamsLife teamsLife = new TeamsLife();
        assertTrue(teamsLife.createTeam(player));

        assertTrue(teamsLife.invite(player, target));
        assertFalse(teamsLife.isInATeam(target));

        //assertEquals(teamsLife.getTeam(player), teamsLife.denyInvite(target));
        assertFalse(teamsLife.isInATeam(target));
    }

    @Test
    void invite()
    {
        TeamsLife teamsLife = new TeamsLife();
        assertTrue(teamsLife.createTeam(player));

        assertTrue(teamsLife.invite(player, target));
        assertTrue(teamsLife.invite(player, target));

        assertFalse(teamsLife.invite(target, player));
    }

    @Test
    void isInATeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertFalse(teamsLife.isInATeam(player));

        assertTrue(teamsLife.createTeam(player));
        assertTrue(teamsLife.isInATeam(player));

        //assertTrue(teamsLife.disbandTeam(player));
        assertFalse(teamsLife.isInATeam(player));
    }

    @Test
    void isInSameTeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertFalse(teamsLife.isInSameTeam(player, target));

        assertTrue(teamsLife.createTeam(player));
        assertFalse(teamsLife.isInSameTeam(player, target));

        assertTrue(teamsLife.invite(player, target));
        assertEquals(teamsLife.getTeam(player), teamsLife.acceptInvite(target));
        assertTrue(teamsLife.isInSameTeam(player, target));

        //assertTrue(teamsLife.disbandTeam(player));
        assertFalse(teamsLife.isInSameTeam(player, target));
    }

    @Test
    void getInvite()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));
        assertTrue(teamsLife.invite(player, target));
        assertTrue(teamsLife.getInvite(target).isPresent());
    }

    @Test
    void getInvites()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));
        assertTrue(teamsLife.invite(player, target));
        assertTrue(teamsLife.getInvites(teamsLife.getTeam(player).get()).get().contains(target));
    }

    @Test
    void getFormattedTeam()
    {
        TeamsLife teamsLife = new TeamsLife();

        assertTrue(teamsLife.createTeam(player));

        assertTrue(teamsLife.invite(player, target));
        assertEquals(teamsLife.getTeam(player), teamsLife.acceptInvite(target));

        assertTrue(teamsLife.getFormattedTeam(player).isPresent());
    }

}