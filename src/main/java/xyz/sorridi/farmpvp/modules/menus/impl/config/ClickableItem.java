package xyz.sorridi.farmpvp.modules.menus.impl.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import me.lucko.helper.menu.Item;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jooq.Function3;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.io.Serializable;
import java.util.LinkedList;

@Setter
@Getter
public class ClickableItem extends MenuItem implements Serializable
{
    protected boolean executeCommands;
    protected LinkedList<String> commandsWhenClicked;

    protected transient Function3<ClickableItem, InventoryClickEvent, FPlayer, Boolean> whenClicked;
    protected transient Item clickable;
    protected transient ItemStack tempStack;

    public ClickableItem() { }

    public ClickableItem(@NonNull ItemStack item, int slot)
    {
        super(item, slot);
        necessaryInit();
    }

    public ClickableItem(@NonNull ItemStack item, int slot, @NonNull String headId)
    {
        super(item, slot, headId);
        necessaryInit();
    }

    public ClickableItem(ItemStack defaultItem, int i, int i1, String number)
    {
        super(defaultItem, i, i1, number);
        necessaryInit();
    }

    public ClickableItem executeCommands(boolean executeCommands)
    {
        this.executeCommands = executeCommands;
        return this;
    }

    public ClickableItem addCommand(@NonNull String commandWhenClicked)
    {
        this.commandsWhenClicked.add(commandWhenClicked);
        return this;
    }

    public ClickableItem whenClicked(@NonNull Function3<ClickableItem, InventoryClickEvent, FPlayer, Boolean> whenClicked)
    {
        this.whenClicked = whenClicked;
        return this;
    }

    public ClickableItem build()
    {
        ItemStack item = getItem();

        if (headId != null && !headId.isEmpty())
        {
            item = head;
        }

        if (tempStack != null)
        {
            item = tempStack;
        }

        if (whenClicked == null)
        {
            clickable = Item.builder(item).build();
            return this;
        }

        clickable = Item.builder(item)
                .bind((event) ->
                {
                    if (whenClicked != null)
                    {
                        val playersLife = Serve.of(PlayerModule.class).getPlayersLife();
                        FPlayer player = playersLife.getPlayerChecked(event.getWhoClicked());

                        boolean result = whenClicked.apply(this, event, player);

                        if (result)
                        {
                            executeCommands();
                        }
                    }
                }, ClickType.RIGHT, ClickType.LEFT)
                .build();

        return this;
    }

    private void necessaryInit()
    {
        commandsWhenClicked = new LinkedList<>();
        commandsWhenClicked.add("say Hello World!");
    }

    public void executeCommands()
    {
        if (executeCommands)
        {
            commandsWhenClicked.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        }
    }

}
