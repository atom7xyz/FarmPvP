package xyz.sorridi.farmpvp.modules.webs;

import lombok.NonNull;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Material;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.events.api.FBlockPlaceEvent;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.webs.impl.WebsLife;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;

@Service
public class WebModule extends ModulePioneer
{
    @Memo(module = WebModule.class)
    private WebsLife websLife;

    private WebModule.Data data;

    @Override
    public void onEnable()
    {
        websLife = new WebsLife();
        data = Serve.of(WebModule.Data.class);
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public void onReload()
    {
        websLife.clearTimings();
    }

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(FBlockPlaceEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getBlock().getType() == Material.WEB)
                .handler(e ->
                {
                    FPlayer player = e.getFPlayer();

                    if (websLife.add(player))
                    {
                        Schedulers.sync().runLater(() -> e.getBlock().setType(Material.AIR), data.getRemoveAfter());
                    }
                    else
                    {
                        player.reply(Replace.of(data.getCdMessage(), TIME, websLife.getUsableRemaining(player)));
                        e.setBlockPlaceEventCancelled(true);
                    }
                });
    }

    @ConfigName("cobwebs.yml")
    public interface Data extends Config
    {
        @Comment("Il numero di ragnatele piazzabili prima di entrare in cooldown.")
        default int getAmountForCd()
        {
            return 2;
        }

        @Comment("Il cooldown per piazzare altre ragnatele (in millis).")
        default int getCd()
        {
            return 5 * 1000;
        }

        @Comment("Il tempo di vita della ragnatela (in ticks).")
        default int getRemoveAfter()
        {
            return 3 * ONE_CLOCK;
        }

        /**
         * Il messaggio che indica il cooldown rimanente.
         * <ul>
         *     <li>{time} -> Il tempo rimanente.</li>
         * </ul>
         */
        @Comment("Il messaggio che indica il cooldown rimanente.")
        default String getCdMessage()
        {
            return "&fDevi aspettare &c{time} &fprima di poter piazzare altre ragnatele!";
        }
    }

}
