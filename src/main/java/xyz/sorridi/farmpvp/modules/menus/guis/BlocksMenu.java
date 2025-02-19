package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class BlocksMenu extends FlagMenu
{

    public BlocksMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(shopData.getGlass());
            setItem(shopData.getWool());
            setItem(shopData.getSandstone());
            setItem(shopData.getClay1());
            setItem(shopData.getClay2());
            setItem(shopData.getClay3());
        }

        drawStructure(menuData.getShopMenu());
    }

}