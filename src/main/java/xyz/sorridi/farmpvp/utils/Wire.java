package xyz.sorridi.farmpvp.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.blocks.impl.BlockPlaced;
import xyz.sorridi.stone.utils.bukkit.location.LocationEvaluate;

import java.util.concurrent.Callable;

public class Wire
{
    private static final ProtocolManager protocolManager = FarmPvP.protocolManager;

    @SneakyThrows
    public static boolean ifNearSendPacket(@NonNull Callable<PacketContainer> containerBuilder,
                                           @NonNull Player player,
                                           @NonNull Location location,
                                           int radius
    ) {
        Location playerLocation = player.getLocation();
        boolean result = LocationEvaluate.isNear(playerLocation, location, radius, false, true, true);

        if (result)
        {
            protocolManager.sendServerPacket(player, containerBuilder.call());
        }

        return result;
    }

    public static void ifNearSendPacketToAll(@NonNull Callable<PacketContainer> containerBuilder,
                                             @NonNull Location location,
                                             int radius
    ) {
        Bukkit.getOnlinePlayers().forEach(player -> ifNearSendPacket(containerBuilder, player, location, radius));
    }

    /**
     * Builds the block break animation packet.
     * @param block The block to build the packet on.
     * @return The packet container.
     */
    public static PacketContainer buildBlockBreakAnimation(@NonNull BlockPlaced block)
    {
        return buildBlockBreakAnimation(block, block.getBreakStage());
    }

    /**
     * Builds the block break animation packet.
     * @param block The block to build the packet on.
     * @param stage The stage of the animation.
     * @return The packet container.
     */
    public static PacketContainer buildBlockBreakAnimation(@NonNull BlockPlaced block, int stage)
    {
        PacketContainer container = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        BlockPosition position = new BlockPosition(block.getVector());

        container
                .getIntegers()
                .write(0, block.hashCode());

        container
                .getBlockPositionModifier()
                .write(0, position);

        container
                .getIntegers()
                .write(1, stage);

        return container;
    }

    /**
     * Sends the block break animation packet to the player.
     * @param player The player to send the packet to.
     * @param block The block to build the packet on.
     */
    public static void sendBlockBreakAnimation(@NonNull Player player, @NonNull BlockPlaced block)
    {
        protocolManager.sendServerPacket(player, buildBlockBreakAnimation(block));
    }

    /**
     * Sends the block break animation packet to the player.
     * @param player The player to send the packet to.
     * @param block The block to build the packet on.
     * @param stage The stage of the animation.
     */
    public static void sendBlockBreakAnimation(@NonNull Player player, @NonNull BlockPlaced block, int stage)
    {
        protocolManager.sendServerPacket(player, buildBlockBreakAnimation(block, stage));
    }

    /**
     * Sends the block break animation packet to all players.
     * @param block The block to build the packet on.
     */
    public static void sendBlockBreakAnimationToAll(@NonNull BlockPlaced block)
    {
        protocolManager.broadcastServerPacket(buildBlockBreakAnimation(block));
    }

    /**
     * Sends the block break animation packet to all players.
     * @param block The block to build the packet on.
     * @param stage The stage of the animation.
     */
    public static void sendBlockBreakAnimationToAll(@NonNull BlockPlaced block, int stage)
    {
        protocolManager.broadcastServerPacket(buildBlockBreakAnimation(block, stage));
    }

}
