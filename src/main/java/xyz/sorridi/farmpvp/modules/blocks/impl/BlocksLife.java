package xyz.sorridi.farmpvp.modules.blocks.impl;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;
import xyz.sorridi.farmpvp.modules.blocks.BlockModule;
import xyz.sorridi.farmpvp.utils.Wire;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.bukkit.location.LocationEvaluate;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.texture.BlockTexture;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class BlocksLife implements IPlaceHolders
{
    private final BlockModule.Data data = Serve.of(BlockModule.Data.class);

    private final HashMap<Block, BlockPlaced> blocksPlaced;

    private final HashMap<MaterialData, MaterialData> stages;
    private final HashMap<Material, Boolean> instantBreak;

    private final long BREAKING_1_10TH;

    private final ParticleBuilder BLOCK_CRACK, FLAME;

    public BlocksLife()
    {
        blocksPlaced    = new HashMap<>();
        stages          = new HashMap<>();
        instantBreak    = new HashMap<>();

        processBreakableBlocks();

        BREAKING_1_10TH = ONE_CLOCK * ((60L * data.getBlockDuration()) / 10);

        BLOCK_CRACK = new ParticleBuilder(ParticleEffect.BLOCK_CRACK).setAmount(25);
        FLAME = new ParticleBuilder(ParticleEffect.FLAME).setAmount(25);

        Schedulers.sync()
                .runRepeating(() ->
                {
                    blocksPlaced.values().forEach(blockPlaced ->
                    {
                        byte stage = blockPlaced.getBreakStage();
                        Wire.sendBlockBreakAnimationToAll(blockPlaced, stage);
                    });
                }, 0, 200L);
    }

    public void processBreakableBlocks()
    {
        stages.clear();
        instantBreak.clear();

        data.getBreakableBlocks().forEach((whole) ->
        {
            String[] tempBlock  = whole.split(" ");
            Material material   = Material.valueOf(tempBlock[0]);;

            if (tempBlock.length == 3 && tempBlock[2].equals("INSTANT"))
            {
                addInstantBreak(material);
            }

            tempBlock = tempBlock[1].split(":");
            int sum = 0;

            MaterialData targetMeta;
            MaterialData forData;

            for (int i = 0; i < tempBlock.length; i++)
            {
                targetMeta = new MaterialData(material, Byte.parseByte(tempBlock[i]));
                forData = null;

                if (++sum < tempBlock.length)
                {
                    forData = new MaterialData(material, Byte.parseByte(tempBlock[i + 1]));
                }

                addStage(targetMeta, forData);
            }
        });
    }

    /**
     * Gets the corresponding BlockPlaced from a Block.
     * @param block Corresponding block.
     * @return The BlockPlaced object if it exists, otherwise an empty optional.
     */
    public Optional<BlockPlaced> getBlockPlaced(@NonNull Block block)
    {
        return Optional.ofNullable(blocksPlaced.get(block));
    }

    /**
     * Adds a block that has been placed to the list of blocks placed.
     * @param blockPlaced BlockPlaced object corresponding to the block placed.
     */
    public void addPlaced(@NonNull BlockPlaced blockPlaced)
    {
        Block block = blockPlaced.getBlock();
        World world = block.getWorld();

        Location blockLocation = LocationEvaluate.getMiddleLocationFull(block.getLocation(), false);

        blocksPlaced.putIfAbsent(block, blockPlaced);

        Function<Task, Boolean> closeIfNotPlaced = task ->
        {
            boolean isNotPlaced = !isPlaced(block);

            if (isNotPlaced)
            {
                task.stop();
            }

            return isNotPlaced;
        };

        Schedulers.sync()
                .runRepeating(task ->
                {
                    if (closeIfNotPlaced.apply(task))
                    {
                        return;
                    }

                    if (blockPlaced.getAndIncrementBreakStage() == 10)
                    {
                        removePlaced(blockPlaced);

                        FLAME.setLocation(blockLocation);
                        FLAME.display();

                        task.stop();
                    }
                    else
                    {
                        BLOCK_CRACK.setParticleData(new BlockTexture(block.getType()));
                        BLOCK_CRACK.setLocation(blockLocation);
                        BLOCK_CRACK.display();
                    }
                }, BREAKING_1_10TH, BREAKING_1_10TH);
    }

    /**
     * Adds a new stage of break to the target.
     * @param target The target MaterialData.
     * @param data The next stage MaterialData.
     */
    public void addStage(@NonNull MaterialData target, @Nullable MaterialData data)
    {
        stages.put(target, data);
    }

    /**
     * Adds a material to the instant breaking blocks.
     * @param material The material to add.
     */
    public void addInstantBreak(@NonNull Material material)
    {
        instantBreak.put(material, true);
    }

    /**
     * Removes a block from the list of blocks placed.
     * @param blockPlaced Block to remove.
     * @return If the block has been removed.
     */
    public void removePlaced(@NonNull BlockPlaced blockPlaced)
    {
        Block block = blockPlaced.getBlock();

        block.setType(Material.AIR);
        blocksPlaced.remove(block);
    }

    /**
     * Removes all blocks from the list of blocks placed.
     */
    public void removeAllPlaced()
    {
        blocksPlaced
                .values()
                .forEach(block -> block.setMaterial(Material.AIR));

        blocksPlaced.clear();
    }

    /**
     * Checks if a block has been placed.
     * @param block Block to check.
     * @return If the block has been placed.
     */
    public boolean isPlaced(@NonNull Block block)
    {
        if (!blocksPlaced.containsKey(block))
        {
            return false;
        }

        BlockPlaced placed = blocksPlaced.get(block);

        return placed.getBreakStage() != 10 && !placed.isMaterial(Material.AIR);
    }

    /**
     * Gets the next stage of the BlockStage.
     * @param materialData The MaterialData to get the next stage of.
     * @return The next stage of the BlockStage.
     */
    public Optional<MaterialData> getNextStage(@NonNull MaterialData materialData)
    {
        return Optional.ofNullable(stages.get(materialData));
    }
    
    public void progressStage(@NonNull Block block)
    {
        Optional<BlockPlaced> _blockPlaced = getBlockPlaced(block);

        if (_blockPlaced.isEmpty())
        {
            return;
        }
        
        if (isInstant(block.getType()))
        {
            block.setType(Material.AIR);
            return;
        }
        
        BlockState blockState = block.getState();
        Optional<MaterialData> _stage = getNextStage(blockState.getData());

        if (_stage.isEmpty())
        {
            block.setType(Material.AIR);
            removePlaced(_blockPlaced.get());
            return;
        }

        blockState.setData(_stage.get());
        blockState.update();
    }

    /**
     * Checks if the block is breakable instantly.
     * @param material The material to check.
     * @return If the block is breakable instantly.
     */
    public boolean isInstant(@NonNull Material material)
    {
        return instantBreak.containsKey(material) && instantBreak.get(material);
    }

    /**
     * Checks if the BlockStage contains the MaterialData.
     * @param materialData The MaterialData to check.
     * @return If the BlockStage contains the MaterialData.
     */
    public boolean stageExists(@NonNull MaterialData materialData)
    {
        return stages.containsKey(materialData);
    }

}
