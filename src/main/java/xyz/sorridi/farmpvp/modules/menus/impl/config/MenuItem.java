package xyz.sorridi.farmpvp.modules.menus.impl.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class MenuItem implements Serializable
{
    protected String headId;

    protected ItemStack item;
    protected transient ItemStack head;

    protected int defaultSlot;
    protected int secondarySlot;

    public MenuItem() { }

    public MenuItem(@NonNull ItemStack item, int defaultSlot)
    {
        this.item = item;
        this.defaultSlot = defaultSlot;
    }

    public MenuItem(@NonNull ItemStack item, int defaultSlot, int secondarySlot)
    {
        this.item = item;
        this.defaultSlot = defaultSlot;
        this.secondarySlot = secondarySlot;
    }

    public MenuItem(@NonNull ItemStack item, int defaultSlot, @Nullable String headId)
    {
        this.item = item;
        this.defaultSlot = defaultSlot;
        this.headId = headId;
    }

    public MenuItem(@NonNull ItemStack item, int defaultSlot, int secondarySlot, @Nullable String headId)
    {
        this.item = item;
        this.defaultSlot = defaultSlot;
        this.secondarySlot = secondarySlot;
        this.headId = headId;
    }

    public MenuItem item(@NonNull ItemStack item)
    {
        this.item = item;
        return this;
    }

    public MenuItem slot(int slot)
    {
        this.defaultSlot = slot;
        return this;
    }

    public String getItemName()
    {
        return item.getItemMeta().getDisplayName();
    }

    public List<String> getItemLore()
    {
        return item.getItemMeta().getLore();
    }

    public ItemStack getItemNoLore()
    {
        ItemStack item = this.item.clone();

        ItemMeta meta = item.getItemMeta();
        meta.setLore(null);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public int hashCode()
    {
        return item.hashCode();
    }

}
