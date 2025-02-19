package xyz.sorridi.farmpvp.modules.player.economy.commands;

import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.sorridi.farmpvp.modules.menus.MenuModule;
import xyz.sorridi.farmpvp.modules.menus.impl.MenuLife;
import xyz.sorridi.farmpvp.modules.player.PlayerModule;
import xyz.sorridi.farmpvp.modules.player.impl.FPlayer;
import xyz.sorridi.farmpvp.modules.player.impl.PlayersLife;
import xyz.sorridi.farmpvp.utils.placeholders.IPlaceHolders;
import xyz.sorridi.stone.utils.bukkit.Serve;

import java.util.Optional;

public class ShopCommand implements FunctionalCommandHandler<Player>, IPlaceHolders
{
    private final PlayerModule playerModule = Serve.of(PlayerModule.class);
    private final PlayersLife playersLife = playerModule.getPlayersLife();

    private final MenuModule menuModule = Serve.of(MenuModule.class);
    private final MenuLife menuLife = menuModule.getMenuLife();

    private final MenuModule.Data.Menu data = Serve.of(MenuModule.Data.Menu.class);

    @Override
    public void handle(@NotNull CommandContext<Player> c)
    {
        Player thePlayer = c.sender();
        Optional<FPlayer> _player = playersLife.getPlayerOrSendError(thePlayer);

        if (_player.isEmpty())
        {
            return;
        }

        FPlayer player = _player.get();

        menuLife.getGui(player, data.getShopMenuName()).ifPresent(Gui::open);
    }

}
