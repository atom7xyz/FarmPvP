package xyz.sorridi.farmpvp.modules.menus.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jooq.Function3;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.menus.MenuModule;
import xyz.sorridi.farmpvp.modules.menus.guis.*;
import xyz.sorridi.farmpvp.modules.menus.impl.config.ClickableItem;
import xyz.sorridi.farmpvp.modules.menus.impl.config.MenuItem;
import xyz.sorridi.farmpvp.modules.menus.impl.config.PurchasableItem;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.Flag;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.IMemorize;
import xyz.sorridi.stone.annotations.ResourceGatherer;
import xyz.sorridi.stone.utils.Replace;
import xyz.sorridi.stone.utils.bukkit.Serve;
import xyz.sorridi.stone.utils.bukkit.TransferMeta;
import xyz.sorridi.stone.utils.constructor.ConstructorCaller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Getter
public class MenuLife implements IMemorize
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();
    private final PlayerModule.InternalData internals = playerModule.getInternals();
    private final PlayerModule.Data.Flag flagData = playerModule.getFlagData();

    private final MenuModule menuModule = Serve.of(MenuModule.class);
    private final MenuModule.Data.Menu menuData = menuModule.getMenuData();
    private final MenuModule.Data.Shop shopData = menuModule.getMenuShop();

    private final HashMap<FPlayer, HashMap<String, FlagMenu>> menus;
    private final HashMap<MenuItem, Item> itemMap;

    public MenuLife()
    {
        menus = new HashMap<>();
        itemMap = new HashMap<>();

        /*
         * ORRIBILE, MA CONFIGAPI E' COSI, N'CE POI FA NULLA
         */

        Function3<ClickableItem, InventoryClickEvent, FPlayer, Boolean> openGui, buy, nothing, bin, draw, canUpgrade, cannotUpgrade;

        openGui = ((item, event, player) ->
        {
            openGui(player, item.getItemName());
            return true;
        });

        buy = ((item, event, player) -> buyItem(player, (PurchasableItem) item));

        nothing = ((item, event, player) -> true);

        bin = ((item, event, player) ->
        {
            Inventory inv = Bukkit.createInventory(null, 9 * 4, item.getItemName());

            player.closeInventory();
            player.getPlayer().openInventory(inv);
            return true;
        });

        draw = ((item, event, player) ->
        {
            player.closeInventory();
            player.reply("todo");
            return true;
        });

        canUpgrade = ((item, event, player) ->
        {
            Flag flag = player.getFlag();

            if (flag.hasPoints(10))
            {
                flag.removePoints(10);
                flag.addLevels(1);
                player.reply("upgraded todo");

                getGui(player, menuData.getMainMenuName()).ifPresent(Gui::redraw);
            }
            else
            {
                player.reply(internals.getNoPoints());
            }

            return true;
        });

        cannotUpgrade = ((item, event, player) ->
        {
            player.reply(internals.getNoPoints());
            return true;
        });

        setClicked(menuData.getMainMenu(), openGui);
        setClicked(menuData.getShopMenu(), openGui);
        setClicked(menuData.getShopArmorsMenu(), openGui);
        setClicked(menuData.getShopToolsMenu(), openGui);
        setClicked(menuData.getShopBlocksMenu(), openGui);
        setClicked(menuData.getShopFoodsMenu(), openGui);
        setClicked(menuData.getShopOtherMenu(), openGui);
        setClicked(menuData.getUpgradeWeaponMenu(), openGui);
        setClicked(menuData.getUpgradeAvailable(), canUpgrade);
        setClicked(menuData.getUpgradeUnavailable(), cannotUpgrade);
        setClicked(menuData.getDraw(), draw);
        setClicked(menuData.getBin(), bin);
        setClicked(menuData.getCosmetics(), openGui);
        setClicked(menuData.getPane(), nothing);
        setClicked(menuData.getHead(), nothing);

        ResourceGatherer.forEachMethod(shopData, method ->
        {
            try
            {
                setClicked(((PurchasableItem) method.invoke(shopData)).init(), buy);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
        }, method -> method.getReturnType().equals(PurchasableItem.class));
    }

    private
    <T extends ClickableItem, G extends Function3<ClickableItem, InventoryClickEvent, FPlayer, Boolean>>
    void setClicked(T item, G whenClicked)
    {
        if (item.getHeadId() != null && !item.getHeadId().isEmpty())
        {
            setHead(item);
        }

        if (item instanceof PurchasableItem purchasable)
        {
            ItemStack itemStack = purchasable.getItem().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();

            int cost = purchasable.getCost();

            lore = Replace.of(lore, COST, cost);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            item.setTempStack(itemStack);
        }

        item.setWhenClicked(whenClicked);

        put(item.build());
    }

    private <T extends ClickableItem> void put(T item)
    {
        itemMap.put(item, item.getClickable());
    }

    private void setHead(MenuItem menuItem)
    {
        ItemStack item = menuItem.getItem();
        String id = menuItem.getHeadId();

        ItemStack head = FarmPvP.headDatabaseAPI.getItemHead(id);

        TransferMeta.name_v1_8(item, head);
        TransferMeta.lore_v1_8(item, head);

        menuItem.setHead(head);
    }

    public <T extends FlagMenu> Optional<T> getGui(@NonNull FPlayer player, @NonNull String name)
    {
        Optional<T> _gui = Optional.empty();

        if (menus.containsKey(player))
        {
            val map = menus.get(player);

            if (map.containsKey(name))
            {
                _gui = Optional.of((T) map.get(name));
            }
        }

        return _gui;
    }

    public void openGui(@NonNull HumanEntity human, @NonNull String name)
    {
        Player thePlayer = (Player) human;
        val _player = playersLife.getPlayer(thePlayer);

        thePlayer.closeInventory();

        _player.ifPresent(player ->
        {
            val _gui = getGui(player, name);

            _gui.ifPresentOrElse(Gui::open, () -> getGui(player, menuData.getMainMenu().getItemName()));
        });
    }

    public void openGui(@NonNull FPlayer player, @NonNull String name)
    {
        openGui(player.getPlayer(), name);
    }

    public void openGui(@NonNull FPlayer player, @NonNull ItemStack item)
    {
        openGui(player, item.getItemMeta().getDisplayName());
    }

    public void openGui(@NonNull FPlayer player, @NonNull MenuItem menuItem)
    {
        openGui(player, menuItem.getItemName());
    }

    public Item getItem(@NonNull MenuItem menuItem)
    {
        return itemMap.get(menuItem);
    }

    public Item getHeadOf(@NonNull FPlayer player)
    {
        ItemStack tempStack;
        Item tempItem;

        ItemMeta meta;

        List<String> lore;

        tempItem = getItem(menuData.getHead());
        tempStack = tempItem.getItemStack().clone();

        SkullMeta skullMeta = (SkullMeta) tempStack.getItemMeta();
        skullMeta.setOwner(player.getName());
        tempStack.setItemMeta(skullMeta);

        meta = tempStack.getItemMeta();

        lore = meta.getLore();

        lore = Replace.of(lore, LEVELS_POINTS, player.getLevels(), player.getPoints());
        lore = Replace.of(lore, KILLS_DEATHS_KDR, player.getKills(), player.getDeaths(), player.getFormattedKD());

        meta.setLore(lore);
        tempStack.setItemMeta(meta);

        Item.Builder builder = Item.builder(tempStack);
        tempItem.getHandlers().forEach(builder::bind);
        tempItem = builder.build();

        return tempItem;
    }

    public Item getUpgradeFlagOf(@NonNull FPlayer player)
    {
        ItemStack tempStack;

        Item.Builder tempBuilder;
        Item tempItem;

        ItemMeta meta;

        List<String> lore;

        if (player.isEditing()) // todo change
        {
            tempItem = getItem(menuData.getUpgradeAvailable());
        }
        else
        {
            tempItem = getItem(menuData.getUpgradeUnavailable());
        }

        tempStack = tempItem.getItemStack().clone();

        meta = tempStack.getItemMeta();
        lore = meta.getLore();

        lore = Replace.of(lore, NEED_PTS, 500);
        lore = Replace.of(lore, POINTS_NEW_POINTS, player.getPoints(), player.getPoints() - 100);
        lore = Replace.of(lore, LEVELS_NEXT_LEVEL, player.getLevels(), player.getLevels() + 1);
        lore = Replace.of(lore, PPS_NEXT_PPS, 1, 2);

        meta.setLore(lore);
        tempStack.setItemMeta(meta);

        tempBuilder = Item.builder(tempStack);

        tempItem.getHandlers().forEach(tempBuilder::bind);

        return tempBuilder.build();
    }

    public <T extends PurchasableItem> boolean buyItem(FPlayer player, T item)
    {
        Flag flag = player.getFlag();
        int cost = item.getCost();

        boolean result = flag.hasPoints(cost);

        if (result)
        {
            flag.removePoints(cost);
            player.addItem(item.getPurchasedItem());
            player.redrawLastOpenMenu();
        }
        else
        {
            player.reply(internals.getNoPoints());
        }

        return result;
    }

    public <T extends FlagMenu> void createMenuForPlayer(Class<T> classMenu, FPlayer player, String name)
    {
        menus.putIfAbsent(player, new HashMap<>());

        val _menu = ConstructorCaller.call(classMenu, player, name);
        _menu.ifPresent(menu -> menus.get(player).putIfAbsent(name, menu));
    }

    @Override
    public void memorize(@NonNull FPlayer player)
    {
        createMenuForPlayer(FlagMenu.class,         player, menuData.getMainMenuName());
        createMenuForPlayer(ShopMenu.class,         player, menuData.getShopMenuName());
        createMenuForPlayer(WeaponsMenu.class,      player, menuData.getShopWeaponMenuName());
        createMenuForPlayer(BlocksMenu.class,       player, menuData.getShopBlocksMenuName());
        createMenuForPlayer(ArmorsMenu.class,       player, menuData.getShopArmorsMenuName());
        createMenuForPlayer(ToolsMenu.class,        player, menuData.getShopToolsMenuName());
        createMenuForPlayer(OtherMenu.class,        player, menuData.getShopOtherMenuName());
        createMenuForPlayer(CosmeticsMenu.class,    player, menuData.getShopCosmeticsMenuName());
        createMenuForPlayer(FoodsMenu.class,        player, menuData.getShopFoodsMenuName());
    }

    @Override
    public void forget(@NonNull FPlayer player)
    {
        menus.remove(player);
    }

}
