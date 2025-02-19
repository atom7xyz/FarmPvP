package xyz.sorridi.farmpvp.modules.blocks;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlocksLife;
import xyz.sorridi.farmpvp.modules.events.api.FBlockBreakEvent;
import xyz.sorridi.farmpvp.modules.events.api.FBlockPlaceEvent;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerJoinEvent;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.Wire;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.List;

@Service
public class BlockModule extends ModulePioneer
{
    @Getter
    private BlocksLife blocksLife;
    private PlayersLife playersLife;

    @Override
    public void onEnable()
    {
        blocksLife = new BlocksLife();
        playersLife = Serve.of(PlayerModule.class).getPlayersLife();
    }

    @Override
    public void onDisable()
    {
        blocksLife.removeAllPlaced();
    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(EntityExplodeEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    for (Block block : e.blockList())
                    {
                        blocksLife.progressStage(block);
                    }
                    e.blockList().clear();
                })
                .bindWith(terminableConsumer);

        Events.subscribe(BlockPlaceEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e -> FarmPvP.callEvent(new FBlockPlaceEvent(playersLife, blocksLife, e)))
                .bindWith(terminableConsumer);

        Events.subscribe(BlockBreakEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e -> FarmPvP.callEvent(new FBlockBreakEvent(playersLife, blocksLife, e)))
                .bindWith(terminableConsumer);

        Events.subscribe(FPlayerJoinEvent.class)
                .handler(e ->
                {
                    Player player = e.getPlayer();

                    blocksLife
                            .getBlocksPlaced()
                            .values()
                            .forEach(block -> Wire.sendBlockBreakAnimation(player, block));
                })
                .bindWith(terminableConsumer);
    }

    @ConfigName("blocks.yml")
    public interface Data extends Config
    {
        /**
         * La lista dei blocchi che possono essere rotti.
         */
        @Comment("Per saperne di più: https://github.com/MetaMC-it/FarmPVP/issues/7")
        default List<String> getBreakableBlocks()
        {
            return List.of(
                    "WOOL 0 INSTANT",
                    "STAINED_CLAY 7:12:8:0",
                    "GLASS 0",
                    "WEB 0"
            );
        }

        @Comment("Durata del blocco nella mappa. (in minuti)")
        default int getBlockDuration()
        {
            return 120;
        }
    }

}
