package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class OtherMenu extends FlagMenu
{

    public OtherMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(shopData.getTNT());
            setItem(shopData.getFireCharge());
            setItem(shopData.getFeather());
            setItem(shopData.getBow());
            setItem(shopData.getArrows());
        }

        drawStructure(menuData.getShopMenu());
    }

}