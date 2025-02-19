package xyz.sorridi.farmpvp.modules.player.impl;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PlayersLife
{
    private final PlayerModule.InternalData internals = Serve.of(PlayerModule.InternalData.class);

    private final HashMap<UUID, FPlayer>    playersById;
    private final HashMap<String, FPlayer>  playersByName;

    public PlayersLife()
    {
        playersById = new HashMap<>();
        playersByName = new HashMap<>();
    }

    /**
     * Add a player to the list of loaded players.
     * @param player The player to add.
     */
    public void addPlayer(@NonNull FPlayer player)
    {
        playersById.putIfAbsent(player.getUuid(), player);
        playersByName.putIfAbsent(player.getName(), player);
    }

    /**
     * Remove a player from the list of loaded players.
     * @param player The player to remove.
     */
    public void removePlayer(@NonNull FPlayer player)
    {
        playersById.remove(player.getUuid());
        playersByName.remove(player.getName());
    }

    /**
     * Get a player from the list of loaded players.
     * @param player The player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     */
    public Optional<FPlayer> getPlayer(@NonNull Player player)
    {
        return Optional.ofNullable(playersById.get(player.getUniqueId()));
    }

    /**
     * Get a player from the list of loaded players.
     * @param uuid The uuid of the player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     */
    public Optional<FPlayer> getPlayer(@NonNull UUID uuid)
    {
        return Optional.ofNullable(playersById.get(uuid));
    }

    /**
     * Get a player from the list of loaded players.
     * @param name The name of the player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     */
    public Optional<FPlayer> getPlayer(@NonNull String name)
    {
        return Optional.ofNullable(playersByName.get(name));
    }

    /**
     * Get a player from the list of loaded players.
     * @param entity The entity of the player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     */
    public Optional<FPlayer> getPlayer(@NonNull Entity entity)
    {
        if (entity instanceof Player)
        {
            return getPlayer((Player) entity);
        }

        return Optional.empty();
    }

    /**
     * Get a player from the list of loaded players.
     * @param entity The entity of the player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     * @throws IllegalArgumentException If the entity is not a player.
     */
    public FPlayer getPlayerChecked(@NonNull Entity entity) throws IllegalArgumentException
    {
        return getPlayer(entity).orElseThrow(() -> new IllegalArgumentException("Entity is not a player"));
    }

    /**
     * Get a player from the list of loaded players.
     * @param player The player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     */
    public Optional<FPlayer> getPlayerOrSendError(@NonNull Player player)
    {
        return getPlayer(player).or(() ->
        {
            player.sendMessage(internals.getLoadingData());
            return Optional.empty();
        });
    }

    /**
     * Get a player from the list of loaded players.
     * @param player The player to get.
     * @return The player if it is loaded, otherwise an empty optional.
     * @throws IllegalArgumentException If the player is not loaded.
     */
    public FPlayer getLoadedPlayer(@NonNull Player player) throws IllegalArgumentException
    {
        return getPlayer(player).orElseThrow(() -> new IllegalArgumentException("Player is not loaded"));
    }

}
