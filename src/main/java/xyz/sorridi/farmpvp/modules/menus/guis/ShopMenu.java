package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class ShopMenu extends FlagMenu
{

    public ShopMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(menuData.getShopArmorsMenu());
            setItem(menuData.getShopToolsMenu());
            setItem(menuData.getShopBlocksMenu());
            setItem(menuData.getShopFoodsMenu());
            setItem(menuData.getShopOtherMenu());
        }

        drawStructure(menuData.getMainMenu());
    }

}