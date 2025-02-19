package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import xyz.sorridi.farmpvp.FarmPvP;
import xyz.sorridi.farmpvp.modules.menus.MenuModule;
import xyz.sorridi.farmpvp.modules.menus.impl.MenuLife;
import xyz.sorridi.farmpvp.modules.menus.impl.config.ClickableItem;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

public class FlagMenu extends Gui implements IPlaceHolders
{
    protected final MenuModule module = Serve.of(MenuModule.class);
    protected final MenuLife menuLife = module.getMenuLife();

    protected final MenuModule.Data.Shop shopData = module.getMenuShop();
    protected final MenuModule.Data.Menu menuData = module.getMenuData();

    protected static final MenuScheme FLAG_MENU = new MenuScheme();

    @Getter
    protected final FPlayer fPlayer;

    static
    {
        FLAG_MENU
                .mask("000000000")
                .mask("000000000")
                .mask("000000000")
                .mask("011101110");
    }

    public FlagMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player.getPlayer(), 4, title);
        this.fPlayer = player;
    }

    protected void setItem(@NonNull ClickableItem item)
    {
        setItem(item, true);
    }

    protected void setItem(@NonNull ClickableItem item, boolean defaultSlot)
    {
        int slot = defaultSlot ? item.getDefaultSlot() : item.getSecondarySlot();

        if (slot == -1)
        {
            FarmPvP.severe("SLOT OF " + item.getItemName() + " IS NOT DEFINED!");
            return;
        }

        setItem(slot, menuLife.getItem(item));
    }

    protected void drawStructure(@NonNull ClickableItem position27)
    {
        if (isFirstDraw())
        {
            MenuPopulator menuPopulator = FLAG_MENU.newPopulator(this);

            Item pane = menuLife.getItem(menuData.getPane());

            while (menuPopulator.placeIfSpace(pane));

            setItem(27, menuLife.getItem(position27));
            setItem(menuData.getDraw());
        }

        setItem(31, menuLife.getHeadOf(fPlayer));
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(10, menuData.getUpgradeWeaponMenu().getClickable());
            setItem(12, menuLife.getUpgradeFlagOf(fPlayer));
            setItem(14, menuData.getShopMenu().getClickable());
            setItem(16, menuData.getCosmetics().getClickable());
        }

        drawStructure(menuData.getBin());
    }

    @Override
    public void open()
    {
        super.open();
        fPlayer.setLastOpenMenu(this);
    }

}
