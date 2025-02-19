package xyz.sorridi.farmpvp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.SneakyThrows;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.lucko.helper.Events;
import me.lucko.helper.maven.MavenLibrary;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import me.lucko.helper.text3.Text;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLoadOrder;
import xyz.sorridi.farmpvp.annotations.configs.ConfigProcessor;
import xyz.sorridi.farmpvp.annotations.events.EventsProcessor;
import xyz.sorridi.farmpvp.annotations.memorizables.MemoProcessor;
import xyz.sorridi.farmpvp.annotations.services.ServicesProcessor;
import xyz.sorridi.stone.builders.average.AverageBuilder;

import java.util.HashMap;

@Plugin(
        name = "FarmPvP",
        version = "1.0-SNAPSHOT",
        description = "FarmPvP original game-mode.",
        authors = "Sorridi",
        website = "https://sorridi.xyz/plugins/farmpvp",
        load = PluginLoadOrder.STARTUP,
        depends = {
                @PluginDependency("helper"),
                @PluginDependency("Stone"),
                @PluginDependency("PlaceholderAPI"),
                @PluginDependency("ActionBarAPI"),
                @PluginDependency("ProtocolLib"),
                @PluginDependency("HeadDatabase")
        }
)

@MavenLibrary(groupId = "xyz.xenondevs", artifactId = "particle", version = "1.8.4")

@Getter
public class FarmPvP extends ExtendedJavaPlugin
{
    public static FarmPvP instance;
    private static boolean enabled;

    private ConfigProcessor     configProcessor;
    private ServicesProcessor   servicesProcessor;
    private EventsProcessor     eventsProcessor;
    private MemoProcessor       memoProcessor;

    public static Location spawnPoint;
    public static ProtocolManager protocolManager;
    public static HeadDatabaseAPI headDatabaseAPI;

    public static HashMap<String, ItemStack> playerHeads;

    @SneakyThrows
    @Override
    public void enable()
    {
        instance = this;

        Runnable startup = () ->
        {
            AverageBuilder averageBuilder = new AverageBuilder(1);
            averageBuilder.setStart();

            headDatabaseAPI = new HeadDatabaseAPI();
            playerHeads     = new HashMap<>();
            protocolManager = ProtocolLibrary.getProtocolManager();

            configProcessor     = new ConfigProcessor(this);
            servicesProcessor   = new ServicesProcessor(this);
            eventsProcessor     = new EventsProcessor(this);
            memoProcessor       = new MemoProcessor(this);

            configProcessor.process();
            servicesProcessor.process();
            eventsProcessor.process();
            memoProcessor.process();

            enabled = true;

            averageBuilder.setEnd();

            info("FarmPvP enabled in " + averageBuilder.get() + "ms! '\\(^-^)/'");
        };

        Events.subscribe(DatabaseLoadEvent.class)
                .expireIf(p -> enabled)
                .handler(e -> startup.run())
                .bindWith(this);

        String message = "&7Il server è in fase di avvio potrai entrare tra qualche secondo...";

        Events.subscribe(AsyncPlayerPreLoginEvent.class)
                .expireIf(p -> enabled)
                .handler(e -> e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Text.colorize(message)))
                .bindWith(this);
    }

    @Override
    public void disable()
    {
        servicesProcessor.shutdownModules();
    }

    public static void callEvent(Event event)
    {
        info("Calling event " + event.getClass().getSimpleName() + "...");
        instance.getServer().getPluginManager().callEvent(event);
    }

    public static void warn(String message)
    {
        instance.getLogger().warning(message);
    }

    public static void info(String message)
    {
        instance.getLogger().info(message);
    }

    public static void severe(String message)
    {
        instance.getLogger().severe(message);
    }

}