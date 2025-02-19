package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class ArmorsMenu extends FlagMenu
{

    public ArmorsMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            //todo
        }

        drawStructure(menuData.getShopMenu());
    }

}