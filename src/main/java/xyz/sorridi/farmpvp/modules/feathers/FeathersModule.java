package xyz.sorridi.farmpvp.modules.feathers;

import lombok.NonNull;
import lombok.val;
import me.lucko.helper.Events;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.feathers.impl.FeathersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.Reply;
import xyz.sorridi.stone.utils.bukkit.Serve;

@Service
public class FeathersModule extends ModulePioneer implements IPlaceHolders
{
    @Memo(module = FeathersModule.class)
    private FeathersLife feathersLife;

    private FeathersModule.Data data;

    @Override
    public void onEnable()
    {
        feathersLife = new FeathersLife();
        data = Serve.of(FeathersModule.Data.class);
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
    public void setup(@NonNull TerminableConsumer terminableConsumer)
    {
        Events.subscribe(EntityDamageEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .handler(e ->
                {
                    Player player = (Player) e.getEntity();
                    double damage = e.getDamage();
                    double div = player.isSneaking() ? data.getFallDamageDivOnSneak() : data.getFallDamageDiv();

                    e.setDamage(damage / Math.max(0, div));
                });

        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> e.getItem() != null)
                .filter(e -> e.getItem().getType() == Material.FEATHER)
                .handler(e ->
                {
                    val coolDowns = feathersLife.getCoolDown();

                    Player player   = e.getPlayer();
                    ItemStack item  = e.getItem();
                    int amount      = item.getAmount();

                    if (coolDowns.isUsable(player))
                    {
                        if (amount > 1)
                        {
                            item.setAmount(amount - 1);
                        }
                        else
                        {
                            if (player.getGameMode() != GameMode.CREATIVE)
                            {
                                player.getInventory().remove(item);
                            }
                        }

                        coolDowns.renew(player);
                        player.setVelocity(player.getLocation().getDirection().multiply(data.getVelocity()));
                    }
                    else
                    {
                        String cd = feathersLife.getUsableRemaining(player);
                        Reply.to(player, Replace.of(data.getCdMessage(), TIME, cd));
                    }

                })
                .bindWith(terminableConsumer);
    }

    @ConfigName("feathers.yml")
    public interface Data extends Config
    {
        @Comment("Il moltiplicatore di velocità per il boost della piuma.")
        default double getVelocity()
        {
            return 2.0;
        }

        @Comment("Il cooldown prima di utilizzare nuovamente la piuma, in secondi.")
        default int getCd()
        {
            return 6;
        }

        @Comment("Il messaggio che indica il cooldown rimanente.")
        default String getCdMessage()
        {
            return "&cDevi aspettare {time} per utilizzare la piuma!";
        }

        @Comment("Divisore di danno da caduta.")
        default int getFallDamageDiv()
        {
            return 2;
        }

        @Comment("Divisore di danno da caduta quando si è in sneak.")
        default int getFallDamageDivOnSneak()
        {
            return 4;
        }
    }

}
