package xyz.sorridi.farmpvp.modules.player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.events.api.FChatEvent;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerJoinEvent;
import xyz.sorridi.farmpvp.modules.events.api.FPlayerQuitEvent;
import xyz.sorridi.farmpvp.modules.events.api.FTeamChatEvent;
import xyz.sorridi.farmpvp.modules.player.commands.FarmPvPCommand;
import xyz.sorridi.farmpvp.modules.player.commands.IgnorePingsCommand;
import xyz.sorridi.farmpvp.modules.player.economy.commands.CoinsCommand;
import xyz.sorridi.farmpvp.modules.player.economy.commands.LevelsCommand;
import xyz.sorridi.farmpvp.modules.player.economy.commands.PointsCommand;
import xyz.sorridi.farmpvp.modules.player.economy.commands.tabs.LevelsTab;
import xyz.sorridi.farmpvp.modules.player.economy.commands.tabs.PointsTab;
import xyz.sorridi.farmpvp.modules.player.essentials.commands.EditCommand;
import xyz.sorridi.farmpvp.modules.player.essentials.commands.GameModeCommand;
import xyz.sorridi.farmpvp.modules.player.essentials.commands.SetSpawnCommand;
import xyz.sorridi.farmpvp.modules.player.essentials.commands.SpawnCommand;
import xyz.sorridi.farmpvp.modules.player.essentials.commands.tabs.GameModeTab;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.ICDFormatter;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.PlaySound;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.string.StringConverter;

import java.util.*;

@Getter
@Service(priority = -1)
public class PlayerModule extends ModulePioneer implements IPlaceHolders, ICDFormatter<FPlayer>
{
    private PlayersLife playersLife;

    private PlayerModule.Data.Chat  chatData;
    private PlayerModule.Data.Spawn spawnData;
    private PlayerModule.Data.Flag  flagData;

    @Override
    public void onEnable()
    {
        playersLife = new PlayersLife();

        chatData    = Serve.of(PlayerModule.Data.Chat.class);
        spawnData   = Serve.of(PlayerModule.Data.Spawn.class);
        flagData    = Serve.of(PlayerModule.Data.Flag.class);

        FarmPvP.spawnPoint = spawnData.getSpawn();
    }

    @Override
    public void onDisable()
    {
        playersLife
                .getPlayersById()
                .values()
                .forEach(player -> player.setEditing(false));
    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Commands.create()
                .description("Comando per la versione del farmpvp.")
                .handler(new FarmPvPCommand())
                .registerAndBind(terminableConsumer, "farmpvp");

        Commands.create()
                .description("Comando per la game-mode.")
                .assertUsage("[mode] [player]")
                .tabHandler(new GameModeTab())
                .handler(new GameModeCommand())
                .registerAndBind(terminableConsumer, "gamemode", "gm");

        Commands.create()
                .description("Comando per la edit-mode.")
                .assertPlayer(internals.getNoConsole())
                .assertPermission(internals.getAdminPermission())
                .handler(new EditCommand())
                .registerAndBind(terminableConsumer, "edit");

        Commands.create()
                .description("Comando per i punti.")
                .assertUsage("[help/of/remove/reset/add] [player] [points]")
                .tabHandler(new PointsTab())
                .handler(new PointsCommand())
                .registerAndBind(terminableConsumer, "point", "points", "punti", "punto");

        Commands.create()
                .description("Comando per i livelli.")
                .assertUsage("[help/of/remove/reset/add] [player] [points]")
                .tabHandler(new LevelsTab())
                .handler(new LevelsCommand())
                .registerAndBind(terminableConsumer, "lvl", "lvls", "level", "levels", "livelli", "livello");

        Commands.create()
                .description("Comando per lo spawn.")
                .assertPlayer(internals.getNoConsole())
                .handler(new SpawnCommand())
                .registerAndBind(terminableConsumer, "spawn", "spawnpoint");

        Commands.create()
                .description("Comando per il set-spawn.")
                .assertPlayer(internals.getNoConsole())
                .assertPermission(internals.getAdminPermission())
                .handler(new SetSpawnCommand())
                .registerAndBind(terminableConsumer, "setspawn", "setspawnpoint");

        Commands.create()
                .description("Comando per le menzioni in chat.")
                .assertPlayer(internals.getNoConsole())
                .handler(new IgnorePingsCommand())
                .registerAndBind(terminableConsumer, "menzioni", "mentions", "ignore", "ignorepings");

        Commands.create()
                .description("Comando per i coins.")
                .assertUsage("[help/of/remove/reset/add] [player] [coins]")
                .tabHandler(new PointsTab())
                .handler(new CoinsCommand())
                .registerAndBind(terminableConsumer, "coin", "coins");

        playersLife.addPlayer(
                new FPlayer("rambo", UUID.randomUUID(),
                        0, 0, 0, 0,0,0,0 ,
                        0,0,0,0, false)
        );

        Events.subscribe(PlayerJoinEvent.class, EventPriority.MONITOR)
                .handler(e ->
                {
                    Player thePlayer = e.getPlayer();

                    val _player = playersLife.getPlayer(thePlayer);
                    FPlayer player;

                    if (_player.isEmpty())
                    {
                        player = new FPlayer(thePlayer);
                        playersLife.addPlayer(player);
                    }
                    else
                    {
                        player = _player.get();
                    }

                    player.setPlayer(thePlayer);
                    FarmPvP.callEvent(new FPlayerJoinEvent(player));
                })
                .bindWith(terminableConsumer);

        Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR)
                .handler(e ->
                {
                    val _player = playersLife.getPlayer(e.getPlayer());

                    _player.ifPresent(player -> FarmPvP.callEvent(new FPlayerQuitEvent(player)));
                })
                .bindWith(terminableConsumer);

        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    val _player = playersLife.getPlayer(e.getPlayer());

                    _player.ifPresentOrElse(player ->
                            FarmPvP.callEvent(new FChatEvent(e, player)), () -> e.setCancelled(true));
                })
                .bindWith(terminableConsumer);

        Events.subscribe(FChatEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    FPlayer player = e.getFPlayer();

                    if (player.isTeamChatEnabled())
                    {
                        FarmPvP.callEvent(new FTeamChatEvent(e, player));
                        return;
                    }

                    String message = e.getMessage();
                    String iterName;
                    String format;

                    val iterator = Arrays.stream(message.split(" ")).iterator();
                    HashSet<String> pinged = null;

                    Optional<FPlayer> _iterPlayer;
                    FPlayer iterPlayer;

                    while (iterator.hasNext())
                    {
                        _iterPlayer = playersLife.getPlayer(iterator.next());

                        if (_iterPlayer.isEmpty())
                        {
                            continue;
                        }
                        
                        if (pinged == null)
                        {
                            pinged = new HashSet<>();
                        }

                        iterPlayer = _iterPlayer.get();
                        iterName = iterPlayer.getName();

                        if (pinged.contains(iterName) || iterPlayer.isIgnorePings() || iterPlayer.equals(player))
                        {
                            continue;
                        }

                        format = iterPlayer.isOnline() ? chatData.getPingFormatOnline() : chatData.getPingFormatOffline();
                        format = Replace.of(format, USER, iterName);

                        message = Replace.of(message, iterName, format);
                        e.getAsyncChatEvent().setMessage(message);

                        if (iterPlayer.isOnline())
                        {
                            PlaySound.play(iterPlayer.getPlayer(), Sound.CHICKEN_EGG_POP);
                        }

                        pinged.add(iterName);
                    }
                })
                .bindWith(terminableConsumer);

        Events.subscribe(PlayerCommandPreprocessEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .handler(e ->
                {
                    Optional<FPlayer> _player = playersLife.getPlayer(e.getPlayer());

                    if (_player.isEmpty())
                    {
                        return;
                    }

                    FPlayer player = _player.get();

                    if (player.isDead())
                    {
                        player.reply(spawnData.getCantCommandWhileDead());
                        e.setCancelled(true);
                    }
                }).bindWith(terminableConsumer);

        val blindness = new PotionEffect(PotionEffectType.BLINDNESS, spawnData.getTeleportDelay() * ONE_CLOCK, 1);

        Events.subscribe(PlayerDeathEvent.class)
                .handler(e ->
                {
                    Player thePlayer = e.getEntity();
                    Optional<FPlayer> _player = playersLife.getPlayer(thePlayer);

                    if (_player.isEmpty())
                    {
                        return;
                    }

                    FPlayer player = _player.get();
                    int delay = spawnData.getTeleportDelay();

                    player.setDead(true);
                    player.setRespawnIn(delay);

                    Schedulers.sync()
                            .runRepeating(task ->
                            {
                                if (!player.isDead())
                                {
                                    ActionBarAPI.sendActionBar(thePlayer, EMPTY_STRING);
                                    task.stop();
                                    return;
                                }

                                String message = Replace.of(spawnData.getTeleportArena(), TIME, getUsableRemaining(player));

                                PlaySound.play(thePlayer, Sound.CHICKEN_EGG_POP);
                                ActionBarAPI.sendActionBar(thePlayer, message);

                                player.decrementRespawnIn();
                            }, 0, ONE_CLOCK);

                    Promise.start()
                            .thenRunDelayedSync(() ->
                            {
                                thePlayer.spigot().respawn();
                                thePlayer.setGameMode(GameMode.SPECTATOR);
                                thePlayer.addPotionEffect(blindness);
                            }, QUARTER_CLOCK)
                            .thenRunDelayedSync(player::teleportToSpawnPoint, QUARTER_CLOCK)
                            .thenRunDelayedSync(() ->
                            {
                                player.teleportToSpawnPoint();
                                player.setDead(false);

                                if (!player.isEditing())
                                {
                                    thePlayer.setGameMode(GameMode.SURVIVAL);
                                }
                            }, (long) ONE_CLOCK * delay - (HALF_CLOCK));
                })
                .bindWith(terminableConsumer);
    }

    @Override
    public String getUsableRemaining(@NonNull FPlayer target)
    {
        long remaining = target.getRespawnIn() * 1000L;
        return StringConverter.fromMillisToHuman(TIME_FULL_ITA_PLU, TIME_FULL_ITA_SIN, remaining);
    }

    public static class Data
    {
        @ConfigName("flag.yml")
        public interface Flag extends Config
        {
            @Comment("Il tempo di raccolta della bandiera, in secondi.")
            default int getPickupTime()
            {
                return 10;
            }
        }

        @ConfigName("edit.yml")
        public interface Edit extends Config
        {
            default String getEnabledEdit()
            {
                return "&aModalità di editing abilitata.";
            }

            default String getDisabledEdit()
            {
                return "&cModalità di editing disabilitata.";
            }
        }

        @ConfigName("spawn.yml")
        public interface Spawn extends Config
        {
            /**
             * La posizione dello spawn.
             */
            default Location getSpawn()
            {
                return new Location(Bukkit.getWorld("world"), 0, 96.5, 0, 0, 0);
            }

            void setSpawn(Location location);

            default String getTeleportInit()
            {
                return "&aTeletrasporto iniziato, non muoverti!";
            }

            default String getTeleport()
            {
                return "&aSei stato teletrasportato allo spawn.";
            }

            default String getTeleportCancelled()
            {
                return "&cIl teletrasporto è stato cancellato.";
            }

            default String getMustNotHaveBanner()
            {
                return "&cNon puoi teletrasportarti con una bandiera piazzata!";
            }

            /**
             * Messaggio di teletrasporto in corso.
             * <ul>
             *     <li>{time} -> Il tempo rimanente.</li>
             * </ul>
             */
            default String getTeleportingBar()
            {
                return "&aSarai allo spawn tra {time}...";
            }

            default String getAlreadyTeleporting()
            {
                return "&cTi stai già teletrasportando!";
            }

            /**
             * Messaggio nell'actionbar per il respawn.
             * <ul>
             *     <li>{time} -> Il tempo rimanente.</li>
             * </ul>
             */
            default String getTeleportArena()
            {
                return "&cRientrerai nell'arena tra {time}...";
            }

            default String getCantCommandWhileDead()
            {
                return "&cNon puoi usare comandi mentre sei un fantasma!";
            }

            @Comment("Il delay per il teletrasporto, in secondi.")
            default int getTeleportDelay()
            {
                return 5;
            }
        }

        @ConfigName("chat.yml")
        public interface Chat extends Config
        {
            /**
             * Il formato del ping quando il player è online.
             * <ul>
             *     <li>{user} -> Il nome del player.</li>
             * </ul>
             */
            default String getPingFormatOnline()
            {
                return "&b@{user}&r";
            }

            /**
             * Il formato del ping quando il player è offline.
             * <ul>
             *     <li>{user} -> Il nome del player.</li>
             * </ul>
             */
            default String getPingFormatOffline()
            {
                return "&c@{user}&r";
            }

            default String getEnabledPings()
            {
                return "&aHai abilitato le menzioni in chat.";
            }

            default String getDisabledPings()
            {
                return "&cHai disabilitato le menzioni in chat.";
            }
        }

        @ConfigName("gamemode.yml")
        public interface GameMode extends Config
        {
            /**
             * Lista di argomenti per args.length == 1.
             * @return List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator", "s", "c", "a", "sp")
             */
            default List<String> getTabArgs1()
            {
                return List.of(
                        "0", "1", "2", "3",
                        "survival", "creative", "adventure", "spectator",
                        "s", "c", "a", "sp"
                );
            }

            /**
             * Il messaggio dello /team help
             */
            @Comment("Messaggio dello /gm help")
            default List<String> getHelpMessage()
            {
                return List.of(
                        " ",
                        "&b/gm help &7» &fMostra questo menù.",
                        "&a/gm <mode> &7- &fCambia la tua game-mode.",
                        "&a/gm <mode> <player> &7- &fCambia la game-mode di <player>.",
                        " "
                );
            }

            /**
             * Il permesso per poter usare la game-mode.
             * @return "farmpvp.edit.levels"
             */
            default String getGameModePermission()
            {
                return "farmpvp.game-mode";
            }

            /**
             * Il messaggio di cambio game-mode.
             * <ul>
             *     <li>{gamemode} -> La nuova game-mode.</li>
             * </ul>
             */
            default String getChangeMessage()
            {
                return "&7Sei ora in modalità &e{gamemode}&7!";
            }

            /**
             * Il messaggio di cambio game-mode di altri giocatori.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{gamemode} -> La nuova game-mode.</li>
             * </ul>
             */
            default String getChangeOtherMessage()
            {
                return "&7{target} è ora in modalità &e{gamemode}&7!";
            }
        }

        @ConfigName("levels.yml")
        public interface Levels extends Config
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
                return List.of("10", "100", "1000", "25", "250", "2500", "50", "500", "5000");
            }

            /**
             * Il permesso per editare i livelli.
             * @return "farmpvp.edit.levels"
             */
            default String getEditPermission()
            {
                return "farmpvp.edit.levels";
            }

            /**
             * Il messaggio dello /levels help
             */
            @Comment("Messaggio dello /levels help")
            default List<String> getHelpMessage()
            {
                return List.of(
                        " ",
                        "&a/livello help &7- &fMostra questo menù.",
                        "&a/livello &7- &fMostra i tuoi livelli.",
                        "&a/livello of <player> &7- &fMostra i livelli di <player>.",
                        "&c/livello add <player> <amount> &7- &fAggiungi <amount> livelli a <player>.",
                        "&c/livello remove <player> <amount> &7- &fRimuovi <amount> livelli a <player>.",
                        "&c/livello set <player> <amount> &7- &fImposta i livelli di <player> ad <amount>.",
                        "&c/livello reset <player> &7- &fResetta i livelli di <player>.",
                        " "
                );
            }

            /**
             * Mostra i livelli.
             * <ul>
             *     <li>{levels} -> I livelli.</li>
             * </ul>
             */
            default String getSeeMessage()
            {
                return "&aHai {levels} livelli.";
            }

            /**
             * Mostra i livelli di altri giocatori.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{levels} -> I livelli.</li>
             * </ul>
             */
            default String getSeeOtherMessage()
            {
                return "&a{target} ha {levels} livelli.";
            }

            /**
             * Il messaggio di aggiunta livelli.
             * <ul>
             *     <li>{tot} -> I livelli aggiunti.</li>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{levels} -> I livelli totali.</li>
             * </ul>
             */
            default String getAddMessage()
            {
                return "&aHai aggiunto {tot} livelli a {target}. &7&o(livelli totali: {levels})";
            }

            /**
             * Il messaggio di aggiunta livelli da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I livelli aggiunti.</li>
             *     <li>{levels} -> I livelli totali.</li>
             * </ul>
             */
            default String getAddStaffMessage()
            {
                return "&a{staffer} ti ha aggiunto {tot} livelli. &7&o(livelli totali: {levels})";
            }

            /**
             * Il messaggio di rimozione livelli.
             * <ul>
             *     <li>{tot} -> I livelli rimossi.</li>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{levels} -> I livelli totali.</li>
             * </ul>
             */
            default String getRemoveMessage()
            {
                return "&aHai rimosso {tot} livelli a {target}. &7&o(livelli totali: {levels})";
            }

            /**
             * Il messaggio di rimozione livelli da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I livelli rimossi.</li>
             *     <li>{levels} -> I livelli totali.</li>
             * </ul>
             */
            default String getRemoveStaffMessage()
            {
                return "&a{staffer} ti ha rimosso {tot} livelli. &7&o(livelli totali: {levels})";
            }

            /**
             * Il messaggio di reset livelli.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             * </ul>
             */
            default String getResetMessage()
            {
                return "&aHai resettato i livelli di {target}.";
            }

            /**
             * Il messaggio di reset livelli da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             * </ul>
             */
            default String getResetStaffMessage()
            {
                return "&a{staffer} ti ha resettato i livelli.";
            }

            /**
             * Il messaggio di set livelli.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I livelli impostati.</li>
             * </ul>
             */
            default String getSetMessage()
            {
                return "&aHai impostato i livelli di {target} a {tot}.";
            }

            /**
             * Il messaggio di set livelli da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I livelli impostati.</li>
             * </ul>
             */
            default String getSetStaffMessage()
            {
                return "&a{staffer} ti ha impostato i livelli a {tot}.";
            }

            /**
             * Il messaggio di errore quando i livelli non sono compresi tra i valori indicati.
             * <ul>
             *     <li>{min} -> Il livello minimo.</li>
             *     <li>{max} -> Il livello massimo.</li>
             * </ul>
             */
            default String getMinMaxEditMessage()
            {
                return "&cIl valore dei livelli deve essere compreso tra {min} e {max} (esclusi).";
            }
        }

        @ConfigName("points.yml")
        public interface Points extends Config
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
             * Il permesso per editare i punti.
             * @return "farmpvp.edit.points"
             */
            default String getEditPermission()
            {
                return "farmpvp.edit.points";
            }

            /**
             * Il messaggio dello /points help
             */
            @Comment("Messaggio dello /points help")
            default List<String> getHelpMessage()
            {
                return List.of(
                        " ",
                        "&a/punti help &7- &fMostra questo menù.",
                        "&a/punti &7- &fMostra i tuoi punti.",
                        "&a/punti of <player> &7- &fMostra i punti di <player>.",
                        "&c/punti add <player> <amount> &7- &fAggiungi <amount> punti a <player>.",
                        "&c/punti remove <player> <amount> &7- &fRimuovi <amount> punti a <player>.",
                        "&c/punti set <player> <amount> &7- &fImposta i punti di <player> ad <amount>.",
                        "&c/punti reset <player> &7- &fResetta i punti di <player>.",
                        " "
                );
            }

            /**
             * Mostra i punti.
             * <ul>
             *     <li>{points} -> I punti.</li>
             * </ul>
             */
            default String getSeeMessage()
            {
                return "&aHai {points} punti.";
            }

            /**
             * Mostra i punti di un player.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{points} -> I punti.</li>
             * </ul>
             */
            default String getSeeOtherMessage()
            {
                return "&a{target} ha {points} punti.";
            }

            /**
             * Il messaggio di aggiunta punti.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I punti aggiunti.</li>
             *     <li>{points} -> I punti totali.</li>
             * </ul>
             */
            default String getAddMessage()
            {
                return "&aHai aggiunto {tot} punti a {target}. &7&o(punti totali: {points})";
            }

            /**
             * Il messaggio di aggiunta punti da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I punti aggiunti.</li>
             *     <li>{points} -> I punti totali.</li>
             * </ul>
             */
            default String getAddStaffMessage()
            {
                return "&a{staffer} ti ha aggiunto {tot} punti. &7&o(punti totali: {points})";
            }

            /**
             * Il messaggio di rimozione punti.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I punti rimossi.</li>
             *     <li>{points} -> I punti totali.</li>
             * </ul>
             */
            default String getRemoveMessage()
            {
                return "&aHai rimosso {tot} punti a {target}. &7&o(punti totali: {points})";
            }

            /**
             * Il messaggio di rimozione punti da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I punti rimossi.</li>
             *     <li>{points} -> I punti totali.</li>
             * </ul>
             */
            default String getRemoveStaffMessage()
            {
                return "&a{staffer} ti ha rimosso {tot} punti. &7&o(punti totali: {points})";
            }

            /**
             * Il messaggio di reset punti.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             * </ul>
             */
            default String getResetMessage()
            {
                return "&aHai resettato i punti di {target}.";
            }

            /**
             * Il messaggio di reset punti da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             * </ul>
             */
            default String getResetStaffMessage()
            {
                return "&a{staffer} ti ha resettato i punti.";
            }

            /**
             * Il messaggio di set punti.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I punti settati.</li>
             * </ul>
             */
            default String getSetMessage()
            {
                return "&aHai impostato i punti di {target} a {tot}.";
            }

            /**
             * Il messaggio di set punti da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I punti settati.</li>
             * </ul>
             */
            default String getSetStaffMessage()
            {
                return "&a{staffer} ti ha impostato i punti a {tot}.";
            }

            /**
             * Il messaggi di errore quando i punti non sono compresi tra i valori indicati.
             * <ul>
             *     <li>{min} -> Il valore minimo.</li>
             *     <li>{max} -> Il valore massimo.</li>
             * </ul>
             */
            default String getMinMaxEditMessage()
            {
                return "&cIl valore dei punti deve essere compreso tra {min} e {max} (esclusi).";
            }
        }

        @ConfigName("coins.yml")
        public interface Coins extends Config
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
             * Il permesso per editare i coins.
             * @return "farmpvp.edit.coins"
             */
            default String getEditPermission()
            {
                return "farmpvp.edit.coins";
            }

            /**
             * Il messaggio dello /coins help
             */
            @Comment("Messaggio dello /coins help")
            default List<String> getHelpMessage()
            {
                return List.of(
                        " ",
                        "&a/coins help &7- &fMostra questo menù.",
                        "&a/coins &7- &fMostra i tuoi coins.",
                        "&a/coins of <player> &7- &fMostra i coins di <player>.",
                        "&c/coins add <player> <amount> &7- &fAggiungi <amount> coins a <player>.",
                        "&c/coins remove <player> <amount> &7- &fRimuovi <amount> coins a <player>.",
                        "&c/coins set <player> <amount> &7- &fImposta i coins di <player> ad <amount>.",
                        "&c/coins reset <player> &7- &fResetta i coins di <player>.",
                        " "
                );
            }

            /**
             * Mostra i coins.
             * <ul>
             *     <li>{coins} -> I coins.</li>
             * </ul>
             */
            default String getSeeMessage()
            {
                return "&aHai {coins} coins.";
            }

            /**
             * Mostra i coins di un player.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{coins} -> I coins.</li>
             * </ul>
             */
            default String getSeeOtherMessage()
            {
                return "&a{target} ha {coins} coins.";
            }

            /**
             * Il messaggio di aggiunta coins.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I coins aggiunti.</li>
             *     <li>{coins} -> I coins totali.</li>
             * </ul>
             */
            default String getAddMessage()
            {
                return "&aHai aggiunto {tot} coins a {target}. &7&o(coins totali: {coins})";
            }

            /**
             * Il messaggio di aggiunta coins da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I coins aggiunti.</li>
             *     <li>{coins} -> I coins totali.</li>
             * </ul>
             */
            default String getAddStaffMessage()
            {
                return "&a{staffer} ti ha aggiunto {tot} coins. &7&o(coins totali: {coins})";
            }

            /**
             * Il messaggio di rimozione coins.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I coins rimossi.</li>
             *     <li>{coins} -> I coins totali.</li>
             * </ul>
             */
            default String getRemoveMessage()
            {
                return "&aHai rimosso {tot} coins a {target}. &7&o(coins totali: {coins})";
            }

            /**
             * Il messaggio di rimozione coins da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I coins rimossi.</li>
             *     <li>{coins} -> I coins totali.</li>
             * </ul>
             */
            default String getRemoveStaffMessage()
            {
                return "&a{staffer} ti ha rimosso {tot} coins. &7&o(coins totali: {coins})";
            }

            /**
             * Il messaggio di reset coins.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             * </ul>
             */
            default String getResetMessage()
            {
                return "&aHai resettato i coins di {target}.";
            }

            /**
             * Il messaggio di reset coins da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             * </ul>
             */
            default String getResetStaffMessage()
            {
                return "&a{staffer} ti ha resettato i coins.";
            }

            /**
             * Il messaggio di set coins.
             * <ul>
             *     <li>{target} -> Il nome del player.</li>
             *     <li>{tot} -> I coins settati.</li>
             * </ul>
             */
            default String getSetMessage()
            {
                return "&aHai impostato i coins di {target} a {tot}.";
            }

            /**
             * Il messaggio di set coins da uno staffer.
             * <ul>
             *     <li>{staffer} -> Il nome dello staffer.</li>
             *     <li>{tot} -> I coins settati.</li>
             * </ul>
             */
            default String getSetStaffMessage()
            {
                return "&a{staffer} ti ha impostato i coins a {tot}.";
            }

            /**
             * Il messaggi di errore quando i coins non sono compresi tra i valori indicati.
             * <ul>
             *     <li>{min} -> Il valore minimo.</li>
             *     <li>{max} -> Il valore massimo.</li>
             * </ul>
             */
            default String getMinMaxEditMessage()
            {
                return "&cIl valore dei coins deve essere compreso tra {min} e {max} (esclusi).";
            }
        }
    }

}
