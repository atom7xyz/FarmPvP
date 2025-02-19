package xyz.sorridi.farmpvp.modules.teams;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerJoinEvent;
import xyz.sorridi.farmpvp.modules.events.api.FTeamChatEvent;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.teams.commands.TeamCommand;
import xyz.sorridi.farmpvp.modules.teams.commands.tabs.TeamTab;
import xyz.sorridi.farmpvp.modules.teams.impl.TeamsLife;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.data.Array;

import java.util.List;

@Getter
@Service
public class TeamsModule extends ModulePioneer
{
    @Memo(module = TeamsModule.class)
    private TeamsLife teamsLife;

    private TeamsModule.Data data;

    @Override
    public void onEnable()
    {
        teamsLife = new TeamsLife();
        data = Serve.of(TeamsModule.Data.class);
    }

    @Override
    public void onDisable()
    {
        // TODO: Save teams.
    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        val playerModule = Serve.of(PlayerModule.class);
        val playersLife = playerModule.getPlayersLife();

        Commands.create()
                .description("Comando per i team.")
                .assertUsage("[help/of/create/invite/kick/leave/join/deny] [player]")
                .assertPlayer(internals.getNoConsole())
                .tabHandler(new TeamTab())
                .handler(new TeamCommand())
                .registerAndBind(terminableConsumer, "team", "teams");

        Events.subscribe(FTeamChatEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    FPlayer player = e.getFPlayer();
                    val _team = teamsLife.getTeam(player);

                    _team.ifPresent(team ->
                    {
                        val replacement = Array.of(player.getName(), e.getMessage());
                        team.replyAll(Replace.of(data.getChatMessage(), USER_MESSAGE, replacement));
                    });
                })
                .bindWith(terminableConsumer);

        Events.subscribe(FPlayerJoinEvent.class, EventPriority.MONITOR)
                .handler(e ->
                {
                    FPlayer player = e.getFPlayer();
                    val _team = teamsLife.getTeam(player);

                    _team.ifPresent(team -> team.updateEntityId(player));
                }).bindWith(terminableConsumer);

        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    Entity entity   = e.getEntity();
                    Entity damager  = e.getDamager();

                    if (data.getFriendlyFire() || !(entity instanceof Player))
                    {
                        return;
                    }

                    FPlayer fPlayer = playersLife.getPlayerChecked(entity);
                    FPlayer fDamager;

                    if (damager instanceof Arrow arrow)
                    {
                        Entity shooter = (Entity) arrow.getShooter();

                        if (shooter instanceof Player)
                        {
                            fDamager = playersLife.getPlayerChecked(shooter);

                            if (teamsLife.isInSameTeam(fPlayer, fDamager))
                            {
                                e.setCancelled(true);
                            }
                        }
                    }
                    else if (damager instanceof Player)
                    {
                        fDamager = playersLife.getPlayerChecked(damager);

                        if (teamsLife.isInSameTeam(fPlayer, fDamager))
                        {
                            e.setCancelled(true);
                        }
                    }
                })
                .bindWith(terminableConsumer);
    }

    @ConfigName("teams.yml")
    public interface Data extends Config
    {
        /**
         * Lista di argomenti per args.length == 1.
         * @return List.of("help", "of", "create", "join", "deny", "chat", "leave", "invite", "kick");
         */
        default List<String> getTabArgs1()
        {
            return List.of("help", "of", "create", "join", "deny", "chat", "leave", "invite", "kick");
        }

        /**
         * Il messaggio dello /team help
         */
        default List<String> getHelpMessage()
        {
            return List.of(
                    " ",
                    "&a/team help &7- &fMostra questo menù.",
                    "&a/team &7- &fMostra il tuo team.",
                    "&a/team of <player> &7- &fMostra il team di &o<player>&f.",
                    "&a/team create &7- &fCrea il tuo team.",
                    "&a/team invite <player> &7- &fInvita &o<player>&f nel tuo team.",
                    "&a/team join &7- &fAccetta l'ultimo invito ricevuto.",
                    "&a/team deny &7- &fRifiuta l'ultimo invito ricevuto.",
                    "&a/team chat &7- &fAttiva/Disattiva la chat del team.",
                    "&a/team kick <player> &7- &fEspelli &o<player>&f dal tuo team.",
                    "&a/team leave &7- &fEsci dal team.",
                    " "
            );
        }

        /**
         * Mostra le informazioni del team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         *     <li>{size} -> La dimensione del team.</li>
         *     <li>{members} -> I membri del team.</li>
         * </ul>
         */
        default List<String> getTeamInfo()
        {
            return List.of(
                    " ",
                    "&7&m-----------------",
                    "&a&lTEAM &7di &f{owner}:",
                    "&7&m-----------------",
                    "&7Membri ({size}):",
                    "&f{members}",
                    "&7&m-----------------",
                    " "
            );
        }

        /**
         * Messaggio nella chat del team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         *     <li>{message} -> Il messaggio.</li>
         * </ul>
         */
        default String getChatMessage()
        {
            return "&a&lTEAM &7&l| &f{user}: &7{message}";
        }

        /**
         * Messaggio personale per quando la chat del team è attivata.
         */
        default String getChatEnabled()
        {
            return "&aChat del team attivata!";
        }

        /**
         * Messaggio personale per quando la chat del team è disattivata.
         */
        default String getChatDisabled()
        {
            return "&cChat del team disattivata!";
        }

        /**
         * Messaggio personale per quando il player è entrato nel team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getJoin()
        {
            return "&aSei entrato nel team di {owner}!";
        }

        /**
         * Messaggio che annuncia l'entrata di un player nel team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getJoinAnnounce()
        {
            return "&a{user} &7è entrato nel team!";
        }

        /**
         * Messaggio personale per quando il player rifiuta l'invito al team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getDeny()
        {
            return "&cHai rifiutato l'invito al team di {owner}!";
        }

        /**
         * Messaggio che annuncia il rifiuto di un player all'invito al team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getDenyAnnounce()
        {
            return "&c{user} &7ha rifiutato l'invito al team!";
        }

        /**
         * Messaggio personale per quando il player esce dal team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getLeave()
        {
            return "&cSei uscito dal team di {owner}!";
        }

        /**
         * Messaggio che annuncia l' uscita di un player dal team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getLeaveAnnounce()
        {
            return "&c{user} &7è uscito dal team!";
        }

        /**
         * Messaggio personale per quando il player viene espulso dal team.
         */
        default String getKick()
        {
            return "&cSei stato espulso dal team!";
        }
        
        /**
         * Messaggio di espulsione di un player dal team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getKicked()
        {
            return "&cHai espulso {user} dal team!";
        }

        /**
         * Messaggio che annuncia l'espulsione di un player dal team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getKickAnnounce()
        {
            return "&c{user} &7è stato espulso dal team!";
        }

        /**
         * Messaggio personale per quando il player non ha inviti.
         */
        default String getNoInvites()
        {
            return "&cNon hai inviti ad alcun team.";
        }

        /**
         * Messaggio personale per quando sia ha già invitato il player in team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getAlreadyInvited()
        {
            return "&cHai già invitato {user} nel team.";
        }

        /**
         * Messaggio personale per quando il player non fa parte di nessun team.
         */
        default String getNoTeam()
        {
            return "&cNon fai parte di nessun team.";
        }

        /**
         * Messaggio personale che indica che il target non fa parte di nessun team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getPlayerNoTeam()
        {
            return "&c{user} non fa parte di nessun team.";
        }

        /**
         * Messaggio personale che indica che il target non fa parte del proprio team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getPlayerNotInYourTeam()
        {
            return "&c{user} non fa parte del tuo team.";
        }

        /**
         * Messaggio di errore per quando il team è pieno.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getTeamFull()
        {
            return "&cIl team di {owner} è pieno.";
        }

        /**
         * Messaggio di creazione del team.
         */
        default String getTeamCreated()
        {
            return "&aHai creato il tuo team!";
        }

        /**
         * Messaggio di errore per quando il player è già in un team.
         */
        default String getAlreadyInATeam()
        {
            return "&cSei già in un team.";
        }

        /**
         * Messaggio di errore quando si è già in un team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getAlreadyInTeamOf()
        {
            return "&cSei già nel team di {owner}.";
        }

        /**
         * Messaggio di errore quando il player è già in un team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getPlayerAlreadyInATeam()
        {
            return "&c{user} è già nel team di {owner}.";
        }

        /**
         * Messaggio di errore quando è necessario essere il proprietario del team.
         */
        default String getMustBeOwner()
        {
            return "&cDevi essere il proprietario del team per poterlo fare.";
        }

        /**
         * Messaggio di disband del team.
         */
        default String getDisband()
        {
            return "&cHai sciolto il tuo team!";
        }

        /**
         * Messaggio che annuncia il disband del team a tutti i player del team.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default String getDisbandAnnounce()
        {
            return "&c{owner} ha sciolto il suo team!";
        }

        /**
         * Messaggio di ricezione di un invito.
         * <ul>
         *     <li>{owner} -> Il nome del proprietario del team.</li>
         * </ul>
         */
        default List<String> getInvite()
        {
            return List.of(
                    "&a{owner} ti ha invitato nel suo team!",
                    "&7  ➥ &o(Accetta l'invito usando &a&o/team join&7&o)"
            );
        }

        /**
         * Messaggio d'invito di un player nel team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getInvited()
        {
            return "&aHai invitato {user} nel tuo team!";
        }

        /**
         * Messaggio che annuncia l'invito di un player nel team.
         * <ul>
         *     <li>{user} -> Il nome del player.</li>
         * </ul>
         */
        default String getInvitedAnnounce()
        {
            return "&a{user} è stato invitato nel team!";
        }

        /**
         * Messaggio di errore per quando il player tenta di espellersi da solo.
         */
        default List<String> getKickNotPossible()
        {
            return List.of(
                    "&cNon puoi espellerti da solo.",
                    "&7  ➥ &o(Puoi uscire dal team usando &a&o/team leave&7&o)"
                    );
        }

        /**
         * Il permesso per editare i teams.
         * @return "farmpvp.teams.edit".
         */
        default String getEditPermission()
        {
            return "farmpvp.teams.edit";
        }

        /**
         * La massima dimensione del team.
         */
        default int getMaxTeamSize()
        {
            return 3;
        }

        @Comment("Se il fuoco amico è abilitato o meno.")
        default boolean getFriendlyFire()
        {
            return true;
        }
    }

}
