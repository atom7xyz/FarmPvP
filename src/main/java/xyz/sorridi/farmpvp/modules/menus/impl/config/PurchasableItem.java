package xyz.sorridi.farmpvp.modules.menus.impl.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;

@Setter
@Getter
public class PurchasableItem extends ClickableItem implements Serializable
{
    protected int cost;

    protected transient ItemStack purchasedItem;

    public PurchasableItem() { }

    public PurchasableItem(@NonNull ItemStack item, int slot)
    {
        super(item, slot);
    }

    public PurchasableItem cost(int cost)
    {
        this.cost = cost;
        return this;
    }

    public PurchasableItem init()
    {
        this.purchasedItem = item.clone();

        ItemMeta meta = purchasedItem.getItemMeta();
        meta.setLore(null);
        purchasedItem.setItemMeta(meta);

        return this;
    }

}
