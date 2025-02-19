package xyz.sorridi.farmpvp.modules.bounties;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.bounties.commands.BountyCommand;
import xyz.sorridi.farmpvp.modules.bounties.commands.tabs.BountyTab;
import xyz.sorridi.farmpvp.modules.bounties.impl.BountiesLife;

import java.util.List;

@Getter
@Service
public class BountyModule extends ModulePioneer
{
    @Memo(module = BountyModule.class)
    private BountiesLife bountiesLife;

    @Override
    public void onEnable()
    {
        bountiesLife = new BountiesLife();
    }

    @Override
    public void onDisable()
    {
        // TODO: Save bounties.
    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Commands.create()
                .description("Comando per le taglie.")
                .assertUsage("[help/of/remove/reset/add] [player] [points]")
                .assertPlayer(internals.getNoConsole())
                .tabHandler(new BountyTab())
                .handler(new BountyCommand())
                .registerAndBind(terminableConsumer, "bounty", "bounties", "taglia", "taglie");

        Events.subscribe(PlayerDeathEvent.class)
                .handler(e -> {})
                .bindWith(terminableConsumer);
    }

    @ConfigName("bounty.yml")
    public interface Data extends Config
    {
        /**
         * Lista di argomenti per args.length == 1.
         * @return List.of("help", "of", "add", "remove", "reset")
         */
        default List<String> getTabArgs1()
        {
            return List.of("help", "of", "add", "remove", "reset");
        }

        /**
         * Lista di argomenti per args.length == 3.
         * @return List.of("100", "1000", "10000", "250", "2500", "25000", "500", "5000", "50000")
         */
        default List<String> getTabArgs3()
        {
            return List.of("100", "1000", "10000", "250", "2500", "25000", "500", "5000", "50000");
        }

        /**
         * Il cooldown in minuti per assegnare una nuova bounty, in minuti.
         * @return 60
         */
        @Comment("Il cooldown in minuti per assegnare una nuova bounty.")
        default int getCd()
        {
            return 60;
        }

        /**
         * Il valore minimo di una bounty.
         * @return 100
         */
        default int getMinValue()
        {
            return 100;
        }

        /**
         * Il valore massimo di una bounty.
         * @return 50000
         */
        default int getMaxValue()
        {
            return 50000;
        }

        /**
         * Il messaggio dello /bounty help
         */
        @Comment("Messaggio dello /bounty help")
        default List<String> getHelpMessage()
        {
            return List.of(
                    " ",
                    "&a/bounty help &7- &fMostra questo menù.",
                    "&a/bounty &7- &fMostra la propria taglia.",
                    "&a/bounty of <player> &7- &fMostra la taglia di &o<player>&f.",
                    "&a/bounty add <player> <amount> &7- &fAggiungi una taglia a &o<player>&f di &o<amount>&f punti.",
                    "&a/bounty remove &7- &fRimuovi la taglia che hai assegnato.",
                    "&c/bounty reset <player> &7- &fResetta la taglia di &o<player>&f.",
                    " "
            );
        }

        /**
         * Il permesso per editare le taglie.
         * @return "farmpvp.edit.bounty"
         */
        default String getEditPermission()
        {
            return "farmpvp.edit.bounty";
        }

        /**
         * Il valore totale delle taglie sulla testa del player.
         * <ul>
         *     <li>{tot} -> Il valore totale delle taglie sulla testa del player.</li>
         * </ul>
         */
        default String getSeeMessage()
        {
            return "&aHai una taglia di {tot} punti.";
        }

        /**
         * Il valore totale delle taglie sulla testa di un altro player.
         * <ul>
         *     <li>{target} -> Il nome del player che si ha controllato.</li>
         *     <li>{tot} -> Il valore totale delle taglie sulla testa del player.</li>
         * </ul>
         */
        default String getSeeOtherMessage()
        {
            return "&a{target} ha una taglia di {tot} punti.";
        }

        /**
         * Il messaggio che indica che il player non ha una taglia sulla testa.
         */
        default String getOwnBountyZeroMessage()
        {
            return "&aNon c'è alcuna taglia sulla tua testa! Che fortuna!";
        }

        /**
         * Il messaggio che indica che il player non ha una taglia sulla testa.
         * <ul>
         *     <li>{target} -> Il nome del player che si ha controllato.</li>
         * </ul>
         */
        default String getOtherBountyZeroMessage()
        {
            return "&a{target} non ha alcuna taglia sulla sua testa.";
        }

        /**
         * Il messaggio di aggiunta di una taglia.
         * <ul>
         *     <li><b>{target}</b> -> Il nome del player che ha ricevuto la taglia.</li>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         * </ul>
         */
        default String getAddMessage()
        {
            return "&aHai aggiunto una taglia a {target} di {tot} punti.";
        }

        /**
         * Il messaggio di aggiunta di una taglia che viene mandato a tutti i giocatori online.
         * <ul>
         *     <li><b>{from}</b> -> Il nome del player che ha aggiunto la taglia.</li>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         *     <li><b>{target}</b> -> Il nome del player che ha ricevuto la taglia.</li>
         *     <li><b>{tot_bounties}</b> -> Il valore totale delle taglie sulla testa del player.</li>
         * </ul>
         */
        default String getAddBroadcastMessage()
        {
            return "&c{from} ha aggiunto una taglia di {tot} punti per la testa di {target}! &7&o(Totale: {tot_bounties})";
        }

        /**
         * Il messaggio di rimozione di una taglia.
         * <ul>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         *     <li><b>{target}</b> -> Il nome del player dal quale è stata rimossa la taglia.</li>
         * </ul>
         */
        default String getRemoveMessage()
        {
            return "&aHai rimosso la taglia di {tot} punti su {target}.";
        }

        /**
         * Il messaggio di rimozione di una taglia che viene mandato a tutti i giocatori online.
         * <ul>
         *     <li><b>{user}</b> -> Il nome del player che ha rimosso la taglia.</li>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         *     <li><b>{target}</b> -> Il nome del player dal quale è stata rimossa la taglia.</li>
         *     <li><b>{tot_bounties}</b> -> Il valore totale delle taglie sulla testa del player.</li>
         * </ul>
         */
        default String getRemovedBroadcastMessage()
        {
            return "&c{user} ha rimosso la taglia di {tot} punti per la testa di {target}! &7&o(Totale: {tot_bounties})";
        }

        /**
         * Il messaggio di errore quando il valore della taglia è minore del valore minimo.
         * <ul>
         *     <li><b>{tot}</b> -> Il valore minimo della taglia.</li>
         * </ul>
         */
        default String getMinValueMessage()
        {
            return "&cLa taglia deve essere di minimo {tot} punti!";
        }

        /**
         * Il messaggio di errore quando il valore della taglia è maggiore del valore massimo.
         * <ul>
         *     <li><b>{tot}</b> -> Il valore massimo della taglia.</li>
         * </ul>
         */
        default String getMaxValueMessage()
        {
            return "&cLa taglia non può superare i {tot} punti!";
        }

        /**
         * Il messaggio di errore quando il player vuole mettere una taglia su se stesso.
         */
        default String getSetSelfMessage()
        {
            return "&cNon puoi mettere una taglia su te stesso!";
        }

        /**
         * Il messaggio di riscatto della taglia che viene mandato a tutti i giocatori online.
         * <ul>
         *     <li><b>{killer}</b> -> Il nome del player che ha riscattato la taglia.</li>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         *     <li><b>{victim}</b> -> Il nome del player dal quale è stata riscattata la taglia.</li>
         * </ul>
         */
        default String getTakeBroadcastMessage()
        {
            return "&c{killer} ha riscattato la taglia di {tot} punti per la testa di {victim}!";
        }

        /**
         * Il messaggio di errore quando si ha già una taglia su un player.
         * <ul>
         *     <li><b>{target}</b> -> Il nome del player su cui si ha già una taglia.</li>
         *     <li><b>{tot}</b> -> Il valore della taglia.</li>
         * </ul>
         */
        default String getBountyAlreadySetMessage()
        {
            return "&cHai già messo una taglia su {target} di {tot} punti!";
        }

        /**
         * Il messaggio di errore quando non si ha una taglia su alcun player.
         */
        default String getNoBountyYetMessage()
        {
            return "&cNon hai ancora messo una taglia.";
        }

        /**
         * Il messaggio di reset delle taglie su un player.
         * <ul>
         *     <li><b>{target}</b> -> Il nome del player dal quale sono state rimosse le taglie.</li>
         * </ul>
         */
        default String getResetMessage()
        {
            return "&aHai rimosso tutte le taglie su {target}.";
        }

        /**
         * Il messaggio di reset delle taglie su un player che viene mandato a tutti i giocatori online.
         * <ul>
         *     <li><b>{staffer}</b> -> Il nome del player che ha resettato le taglie.</li>
         *     <li><b>{target}</b> -> Il nome del player dal quale sono state rimosse le taglie.</li>
         * </ul>
         */
        default String getResetStafferMessage()
        {
            return "&a{staffer} ha rimosso le taglie che sono state messe su {target}! &7&o(I punti sono stati restituiti).";
        }
    }

}
