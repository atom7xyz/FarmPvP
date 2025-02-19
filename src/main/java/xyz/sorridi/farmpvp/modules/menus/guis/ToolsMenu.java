package xyz.sorridi.farmpvp.modules.menus.guis;

import lombok.NonNull;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;

public class ToolsMenu extends FlagMenu
{

    public ToolsMenu(@NonNull FPlayer player, @NonNull String title)
    {
        super(player, title);
    }

    @Override
    public void redraw()
    {
        if (isFirstDraw())
        {
            setItem(shopData.getWoodPickaxe());
            setItem(shopData.getStonePickaxe());
            setItem(shopData.getIronPickaxe());
            setItem(shopData.getDiamondPickaxe());
            setItem(shopData.getShears());
        }

        drawStructure(menuData.getShopMenu());
    }

}