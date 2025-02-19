package xyz.sorridi.farmpvp.modules.papi.impl;

import lombok.NonNull;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucko.helper.text3.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.bounties.BountyModule;
import xyz.sorridi.farmpvp.modules.bounties.impl.BountiesLife;
import xyz.sorridi.farmpvp.modules.papi.HoldersModule;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public class HoldersLife extends PlaceholderExpansion implements IPlaceHolders, IMemorize
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final BountyModule bountyModule = Serve.of(BountyModule.class);
    private final BountiesLife bountiesLife = bountyModule.getBountiesLife();

    private final HoldersModule.Data data = Serve.of(HoldersModule.Data.class);

    private final HashMap<String, Function<FPlayer, Object>> placeholders;

    public HoldersLife()
    {
        placeholders = new HashMap<>();

        add("player_kills", FPlayer::getKills);
        add("mob_kills",    FPlayer::getMobKills);
        add("deaths",       FPlayer::getDeaths);
        add("kd",           FPlayer::getFormattedKD);

        add("points",       FPlayer::getFormattedPoints);
        add("levels",       FPlayer::getLevels);
        add("coins",        FPlayer::getCoins);

        add("levels_emoji", player ->
        {
            Flag flag = player.getFlag();
            String result = Replace.of(data.getLevelsEmoji(), COLOR_LEVELS, flag.getLevelsColor(), flag.getLevels());

            return Text.colorize(result);
        });

        add("banners_destroyed", FPlayer::getBannersDestroyed);

        add("flag_location", player ->
        {
            val _location = player.getFlag().getLocation();

            if (_location.isEmpty())
            {
                return data.getBannerNotPlaced();
            }

            Location location = _location.get();

            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            return Replace.of(data.getBannerCoords(), XYZ, x, y, z);
        });

        add("bounty_value", player ->
        {
            try
            {
                return bountiesLife.getBountiesValue(player).orElse(0);
            }
            catch (NoSuchElementException e)
            {
                return 0;
            }
        });
    }

    @Override
    public @NotNull String getIdentifier()
    {
        return "farmpvp";
    }

    @Override
    public @NotNull String getAuthor()
    {
        return "Sorridi";
    }

    @Override
    public @NotNull String getVersion()
    {
        return "1.0";
    }

    public void add(String name, Function<FPlayer, Object> function)
    {
        placeholders.put(name, function);
    }

    public void remove(String name)
    {
        placeholders.remove(name);
    }

    public Object get(String name, FPlayer fPlayer)
    {
        return placeholders.get(name).apply(fPlayer);
    }

    public Optional<Function<FPlayer, Object>> getPlaceholder(String name)
    {
        return Optional.ofNullable(placeholders.get(name));
    }

    public boolean contains(String name)
    {
        return placeholders.containsKey(name);
    }

    public void assignColor(Flag flag)
    {
        int counter = 0;
        int key;

        for (val entry : data.getLevelsColor().entrySet())
        {
            counter++;

            try
            {
                key = Integer.parseInt(entry.getKey());

                if (key > flag.getLevels())
                {
                    break;
                }

                flag.setLevelsColor(entry.getValue());

                /*
                if (counter <= 15)
                {
                    MaterialData materialData = flag.getItem().getData();
                    materialData.setData((byte) counter);

                    flag.getItem().setData(materialData);
                }
                */
            }
            catch (NumberFormatException e)
            {
                FarmPvP.severe("Error while parsing levels color key: " + entry.getKey());
            }
        }
    }

    @Override
    public String onPlaceholderRequest(Player thePlayer, @NotNull String id)
    {
        String result = data.getLoadingDataMessage();

        if (thePlayer == null)
        {
            return result;
        }

        val _action = getPlaceholder(id);

        if (_action.isEmpty())
        {
            return data.getUnknownPlaceholder();
        }

        val _player = playersLife.getPlayer(thePlayer);

        if (_player.isEmpty())
        {
            return result;
        }

        Function<FPlayer, Object> action = _action.get();
        FPlayer player = _player.get();

        result = String.valueOf(action.apply(player));

        return result;
    }

    @Override
    public void memorize(@NonNull FPlayer player)
    {
        assignColor(player.getFlag());
    }

    @Override
    public void forget(@NonNull FPlayer player)
    {

    }

}
