package xyz.sorridi.farmpvp.modules.papi;

import lombok.Getter;
import lombok.val;
import me.clip.placeholderapi.events.ExpansionsLoadedEvent;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.papi.impl.HoldersLife;

import java.util.LinkedHashMap;

@Getter
@Service(priority = 100)
public class HoldersModule extends ModulePioneer
{
    @Memo(module = HoldersModule.class)
    private HoldersLife holdersLife;

    @Override
    public void onEnable()
    {
        holdersLife = new HoldersLife();
        holdersLife.register();
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer)
    {
        Events.subscribe(ExpansionsLoadedEvent.class, EventPriority.MONITOR)
                .filter(e -> !holdersLife.isRegistered())
                .handler(e -> holdersLife.register())
                .bindWith(consumer);
    }

    @ConfigName("placeholders.yml")
    public interface Data extends Config
    {
        default String getLoadingDataMessage()
        {
            return "...";
        }

        default String getUnknownPlaceholder()
        {
            return "???";
        }

        /**
         * Il placeholder per quando la bandiera non è piazzata.
         */
        @Comment("Il placeholder per quando la bandiera non è piazzata.")
        default String getBannerNotPlaced()
        {
            return "&cNon piazzata";
        }

        /**
         * Le coordinate della bandiera con la formattazione.
         * <ul>
         *     <li>{x} -> La coordinata x.</li>
         *     <li>{y} -> La coordinata y.</li>
         *     <li>{z} -> La coordinata z.</li>
         * </ul>
         */
        default String getBannerCoords()
        {
            return "x: {x} | y: {y} | z: {z}";
        }

        /**
         * Il livello con la formattazione.
         * <ul>
         *     <li>{color} -> Il colore del livello.</li>
         *     <li>{level} -> Il livello.</li>
         * </ul>
         */
        default String getLevelsEmoji()
        {
            return "&7{color}{levels}⭐&7";
        }

        /**
         * I colori corrispondenti ai livelli.
         */
        @Comment("Relazione tra livello e colore.")
        default LinkedHashMap<String, String> getLevelsColor()
        {
            val map = new LinkedHashMap<String, String>();

            map.put("0", "&f");
            map.put("50", "&a");
            map.put("100", "&b");
            map.put("150", "&e");
            map.put("200", "&d");

            return map;
        }
    }

}
