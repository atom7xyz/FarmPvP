package xyz.sorridi.farmpvp.modules;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.command.CommandSender;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.utils.IModule;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.data.Array;

import java.util.List;

@Getter
public abstract class ModulePioneer implements IModule, TerminableModule, IPlaceHolders
{
    protected InternalData internals;
    protected boolean enabled;

    @Override
    public void enable()
    {
        enabled = true;
        internals = Serve.of(InternalData.class);
        onEnable();
    }

    @Override
    public void disable()
    {
        enabled = false;
        onDisable();
    }

    @Override
    public void reload()
    {
        onReload();
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void onReload();

    @ConfigName("internals.yml")
    public interface InternalData extends Config
    {
        /**
         * Il permesso per eseguire i comandi da amministratore della modalità.
         */
        @Comment("Il permesso per eseguire i comandi da amministratore della modalità.")
        default String getAdminPermission()
        {
            return "farmpvp.edit";
        }

        /**
         * Il messaggio di errore quando un player non esiste.
         */
        default String getUnknownPlayer()
        {
            return "&cQuesto player non esiste!";
        }

        /**
         * Il messaggio di errore quando il player non ha il permesso per eseguire il comando.
         * <ul>
         *     <li><b>{perm}</b> -> Il permesso mancante.</li>
         * </ul>
         */
        default List<String> getNoPerms()
        {
            return List.of(
                    "&cNon hai il permesso per eseguire questo comando!",
                    "&7  ➥ &o({perm})"
            );
        }

        /**
         * Il messaggio di errore quando il comando viene eseguito da console.
         */
        default String getNoConsole()
        {
            return "&cQuesto comando non può essere eseguito da console!";
        }

        /**
         * Il messaggio di errore quando il comando viene eseguito da un player.
         */
        default String getNoPlayer()
        {
            return "&cQuesto comando non può essere eseguito dai giocatori!";
        }

        /**
         * Il messaggio di errore quando il player non ha abbastanza punti.
         */
        default String getNoPoints()
        {
            return "&cNon hai abbastanza punti per eseguire questa azione!";
        }

        /**
         * Il messaggio di errore quando il player non ha abbastanza coins.
         */
        default String getNoCoins()
        {
            return "&cNon hai abbastanza coins per eseguire questa azione!";
        }

        /**
         * Il messaggio di errore quando il player non ha abbastanza livelli.
         */
        default String getNoLevels()
        {
            return "&cNon hai abbastanza livelli per eseguire questa azione!";
        }

        /**
         * Il messaggio di errore quando il player è ancora in cooldown.
         * <ul>
         *     <li><b>{time}</b> -> Il tempo rimanente.</li>
         * </ul>
         */
        default String getCdMessage()
        {
            return "&cDevi aspettare {time} per eseguire questa azione!";
        }

        /**
         * Il messaggio di errore quando i dati del player sono in caricamento.
         */
        default String getLoadingData()
        {
            return "&cSto recuperando i dati del tuo profilo...";
        }

        /**
         * Il messaggio di errore quando il player non è online.
         * <ul>
         *     <li><b>{target}</b> -> Il nome del player che non è online.</li>
         * </ul>
         */
        default String getOfflineMessage()
        {
            return "&c{target} non è online!";
        }

        /**
         * Il messaggio di errore quando non sono presenti dati utili di un player.
         * <ul>
         *     <li><b>{target}</b> -> Il nome del player di cui non sono presenti dati utili.</li>
         * </ul>
         */
        default List<String> getNoRecentData()
        {
            return List.of(
                    "&cNon sono presenti dati utili di {target}.",
                    "&7  ➥ &o(I dati relativi ad esso erano trascurabili e sono stati rimossi.)"
            );
        }

        /**
         * Il messaggio di errori fatali.
         * <ul>
         *     <li><b>{at}</b> -> In che classe e linea si è verificato l'errore.</li>
         * </ul>
         */
        default List<String> getFatalError()
        {
            return List.of(
                    "&cSi è verificato un errore fatale, prova a rientrare nel server o contatta lo staff! :(",
                    "&7  ➥ &o(debug: {at})"
            );
        }

        /**
         * Il permesso che da accesso alle notifiche di debug.
         */
        @Comment("Il permesso che da accesso alle notifiche di debug.")
        default String getDebugPermission()
        {
            return "farmpvp.debug";
        }
    }

    /**
     * Replies the no recent data error to the player.
     * @param player The player.
     * @param target The target which data are not available.
     */
    public void replyNoRecentData(@NonNull FPlayer player, String target)
    {
        player.reply(Replace.of(internals.getNoRecentData(), TARGET, target));
    }

    /**
     * Replies the fatal error to the player.
     * @param player The player.
     * @param e The error.
     */
    public void replyFatalError(@NonNull FPlayer player, Throwable e)
    {
        String at = e.getStackTrace()[0].toString();
        player.reply(Replace.of(internals.getFatalError(), AT, at));

        e.printStackTrace();
    }

    /**
     * Replies the no permission error to the player.
     * @param player The player.
     * @param perm The permission.
     */
    public void replyNoPermission(@NonNull FPlayer player, String perm)
    {
        player.reply(Replace.of(internals.getNoPerms(), PERM, perm));
    }

    /**
     * Replies the offline error to the player.
     * @param player The player.
     * @param target The target.
     */
    public void replyOffline(@NonNull FPlayer player, String target)
    {
        player.reply(Replace.of(internals.getOfflineMessage(), TARGET, target));
    }

    /**
     * Replies the no console error to the player.
     * @param player The target.
     * @param messages The help messages.
     */
    public void replyHelp(@NonNull FPlayer player, List<String> messages)
    {
        player.reply(messages);
    }

    /**
     * Replies the no console error to the player.
     * @param c The command context.
     * @param messages The help messages.
     */
    public void replyHelp(@NonNull CommandContext<CommandSender> c, List<String> messages)
    {
        c.reply(Array.of(messages, String[].class));
    }

}
