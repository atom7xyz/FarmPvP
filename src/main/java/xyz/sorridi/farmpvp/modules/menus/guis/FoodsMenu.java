package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class FoodsMenu extends FlagMenu
{

    public FoodsMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(shopData.getFish());
            setItem(shopData.getPotato());
            setItem(shopData.getPork());
            setItem(shopData.getGoldenApple());
            setItem(shopData.getRegen());
            setItem(shopData.getSpeed());
            setItem(shopData.getFireResistance());
        }

        drawStructure(menuData.getShopMenu());
    }

}