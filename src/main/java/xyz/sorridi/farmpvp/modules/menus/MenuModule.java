package xyz.sorridi.farmpvp.modules.menus;

import lombok.Getter;
import me.lucko.helper.Commands;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import xyz.sorridi.farmpvp.annotations.memorizables.Memo;
import xyz.sorridi.farmpvp.annotations.services.Service;
import xyz.sorridi.farmpvp.modules.ModulePioneer;
import xyz.sorridi.farmpvp.modules.menus.impl.MenuLife;
import xyz.sorridi.farmpvp.modules.menus.impl.config.ClickableItem;
import xyz.sorridi.farmpvp.modules.menus.impl.config.PurchasableItem;
import xyz.sorridi.farmpvp.modules.player.economy.commands.ShopCommand;
import xyz.sorridi.stone.utils.bukkit.Serve;

@Getter
@Service(priority = 5)
public class MenuModule extends ModulePioneer
{
    @Memo(module = MenuModule.class)
    private MenuLife menuLife;

    private Data.Menu menuData;
    private Data.Shop menuShop;

    @Override
    public void onEnable()
    {
        menuData = Serve.of(Data.Menu.class);
        menuShop = Serve.of(Data.Shop.class);

        menuLife = new MenuLife();
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public void onReload()
    {

    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer)
    {
        Commands.create()
                .description("Shop command.")
                .assertPlayer(internals.getNoConsole())
                .handler(new ShopCommand())
                .registerAndBind(consumer, "shop");
    }

    public static class Data
    {
        @ConfigName("menus.yml")
        public interface Menu extends Config
        {
            default String getMainMenuName()
            {
                return "&4Bandiera";
            }

            default String getShopMenuName()
            {
                return "&eNegozio $";
            }

            default String getShopBlocksMenuName()
            {
                return "&2• Blocchi •";
            }

            default String getShopArmorsMenuName()
            {
                return "&3• Armature •";
            }

            default String getShopToolsMenuName()
            {
                return "&e• Attrezzi •";
            }

            default String getShopOtherMenuName()
            {
                return "&b• Utilità •";
            }

            default String getShopWeaponMenuName()
            {
                return "&6Potenzia arma ✪";
            }

            default String getShopCosmeticsMenuName()
            {
                return "&eCosmetici";
            }

            default String getShopFoodsMenuName()
            {
                return "&c• Cibi e bevande •";
            }

            @Comment("La testa del player.")
            default ClickableItem getHead()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .name("&bProfilo ✦")
                        .lore(" ")
                        .lore("&bLivello: &f{levels}")
                        .lore("&bPunti: &f{points}")
                        .lore(" ")
                        .lore("&eKills: &f{player_kills}")
                        .lore("&eDeaths: &f{deaths}")
                        .lore("&eKDR: &f{kd}")
                        .data(3)
                        .build();

                return new ClickableItem(defaultItem, 31);
            }

            @Comment("Il cestino.")
            default ClickableItem getBin()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .name("&cCestino ☣")
                        .lore(" ")
                        .lore("&7Clicca per aprire")
                        .lore("&7il cestino e buttare")
                        .lore("&7gli oggetti inutili.")
                        .build();

                return new ClickableItem(defaultItem, 27, "25114");
            }

            @Comment("Il raccogli bandiera.")
            default ClickableItem getDraw()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.HOPPER)
                        .name("&cRaccogli la bandiera ⚐")
                        .lore(" ")
                        .lore("&7Clicca per ritirare")
                        .lore("&7la tua bandiera da terra.")
                        .lore(" ")
                        .lore("&7Tempo di raccolta: &e{draw_in} secondi")
                        .build();

                return new ClickableItem(defaultItem, 35);
            }

            @Comment("Il negozio.")
            default ClickableItem getShopMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .name("&eNegozio $")
                        .lore(" ")
                        .lore("&7Clicca per aprire")
                        .lore("&7il menù del negozio")
                        .lore("&7in cui acquistare")
                        .lore("&7i potenziamenti.")
                        .build();

                return new ClickableItem(defaultItem, 14, 27, "50238");
            }

            @Comment("Il potenziamento delle armi.")
            default ClickableItem getUpgradeWeaponMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.IRON_SWORD)
                        .name("&6Potenzia arma ✪")
                        .lore(" ")
                        .lore("&7Clicca per aprire")
                        .lore("&7il menù dei potenziamenti")
                        .lore("&7della tua arma.")
                        .build();

                return new ClickableItem(defaultItem, 10);
            }

            @Comment("Il potenziamento della bandiera non disponibile.")
            default ClickableItem getUpgradeUnavailable()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .name("&6Potenzia bandiera ✗")
                        .lore(" ")
                        .lore("&7Non puoi ancora")
                        .lore("&7salire di livello.")
                        .lore(" ")
                        .lore("&7Necessiti di altri")
                        .lore("&6{need_points} punti.")
                        .lore(" ")
                        .lore("&bLivello: &f{levels} &7-> &a{next_level}")
                        .lore("&ePunti/s: &f{pps} &7-> &a{next_pps}")
                        .build();

                return new ClickableItem(defaultItem, 12, "9328");
            }

            @Comment("Il potenziamento della bandiera disponibile.")
            default ClickableItem getUpgradeAvailable()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .name("&6Potenzia bandiera")
                        .lore(" ")
                        .lore("&7Clicca per aumentare")
                        .lore("&7di livello.")
                        .lore(" ")
                        .lore("&7Punti attuali: &f{points} &7-> &c{new_points}")
                        .lore( " ")
                        .lore("&bLivello: &f{levels} &7-> &a{next_level}")
                        .lore("&ePunti/s: &f{pps} &7-> &a{next_pps}")
                        .build();

                return new ClickableItem(defaultItem, 12, "48792");
            }

            @Comment("I cosmetici.")
            default ClickableItem getCosmetics()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.BANNER)
                        .name("&eCosmetici")
                        .lore(" ")
                        .lore("&7Clicca per aprire")
                        .lore("&7il menù dei cosmetici")
                        .lore("&7per personalizzare")
                        .lore("&7l'aspetto della tua bandiera.")
                        .build();

                return new ClickableItem(defaultItem, 16);
            }

            @Comment("I vetri di separazione.")
            default ClickableItem getPane()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                        .name("&7")
                        .data(15)
                        .build();

                return new ClickableItem(defaultItem, -1);
            }

            @Comment("Il negozio di armature.")
            default ClickableItem getShopArmorsMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.DIAMOND_HELMET)
                        .name("&3• Armature •")
                        .build();

                return new ClickableItem(defaultItem, 10);
            }

            @Comment("Il negozio degli attrezzi.")
            default ClickableItem getShopToolsMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.DIAMOND_PICKAXE)
                        .name("&e• Attrezzi •")
                        .build();

                return new ClickableItem(defaultItem, 11);
            }

            @Comment("Il negozio dei blocchi.")
            default ClickableItem getShopBlocksMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STAINED_CLAY)
                        .name("&2• Blocchi •")
                        .data(8)
                        .build();

                return new ClickableItem(defaultItem, 13);
            }

            @Comment("Il negozio di cibi e bevande.")
            default ClickableItem getShopFoodsMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.GRILLED_PORK)
                        .name("&c• Cibi e bevande •")
                        .build();

                return new ClickableItem(defaultItem, 15);
            }

            @Comment("Il negozio degli oggetti vari.")
            default ClickableItem getShopOtherMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.FEATHER)
                        .name("&b• Utilità •")
                        .build();

                return new ClickableItem(defaultItem, 16);
            }

            @Comment("Il menù principale.")
            default ClickableItem getMainMenu()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SKULL_ITEM)
                        .data(3)
                        .name("&4Bandiera")
                        .lore(" ")
                        .lore("&7Clicca per tornare")
                        .lore("&7al menù della bandiera.")
                        .build();

                return new ClickableItem(defaultItem, 27, "8790");
            }
        }

        @ConfigName("shops.yml")
        public interface Shop extends Config
        {
            default PurchasableItem getGlass()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.GLASS)
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 10).cost(1);
            }

            default PurchasableItem getWool()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.WOOL)
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 11).cost(1);
            }

            default PurchasableItem getSandstone()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SANDSTONE)
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 12).cost(1);
            }

            default PurchasableItem getClay1()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STAINED_CLAY)
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 14).cost(1);
            }

            default PurchasableItem getClay2()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STAINED_CLAY)
                        .amount(16)
                        .data(8)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 15).cost(1);
            }

            default PurchasableItem getClay3()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STAINED_CLAY)
                        .amount(16)
                        .data(12)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 16).cost(1);
            }

            default PurchasableItem getTNT()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.TNT)
                        .name("&cTritolo istantaneo")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 9).cost(1);
            }

            default PurchasableItem getFireCharge()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.FIREBALL)
                        .name("&cPalla di fuoco")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 11).cost(1);
            }

            default PurchasableItem getFeather()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.FEATHER)
                        .name("&bSpinta")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 13).cost(1);
            }

            default PurchasableItem getBow()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.BOW)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 15).cost(1);
            }

            default PurchasableItem getArrows()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.ARROW)
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 17).cost(1);
            }

            default PurchasableItem getWoodPickaxe()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.WOOD_PICKAXE)
                        .name("&7Piccone del principiante")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 9).cost(1);
            }

            default PurchasableItem getStonePickaxe()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.STONE_PICKAXE)
                        .name("&7Piccone dell'apprendista")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 11).cost(1);
            }

            default PurchasableItem getIronPickaxe()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.IRON_PICKAXE)
                        .name("&7Piccone del minatore")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 13).cost(1);
            }

            default PurchasableItem getDiamondPickaxe()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.DIAMOND_PICKAXE)
                        .name("&7Piccone dell'esperto")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 15).cost(1);
            }

            default PurchasableItem getShears()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.SHEARS)
                        .name("&7Tosa pecore")
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 17).cost(1);
            }

            default PurchasableItem getFish()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.COOKED_FISH)
                        .name("&7Pesce")
                        .amount(16)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 9).cost(1);
            }

            default PurchasableItem getPotato()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.BAKED_POTATO)
                        .name("&7Patata")
                        .amount(8)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 10).cost(1);
            }

            default PurchasableItem getPork()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.GRILLED_PORK)
                        .name("&7Cotoletta")
                        .amount(8)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 11).cost(1);
            }

            default PurchasableItem getGoldenApple()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.GOLDEN_APPLE)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 13).cost(1);
            }

            default PurchasableItem getRegen()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.POTION)
                        .name("&7Pozione di &drigenerazione")
                        .data(8193)
                        .amount(1)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 15).cost(1);
            }

            default PurchasableItem getSpeed()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.POTION)
                        .name("&7Pozione di &bvelocità")
                        .data(8194)
                        .amount(1)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 16).cost(1);
            }

            default PurchasableItem getFireResistance()
            {
                ItemStack defaultItem = ItemStackBuilder.of(Material.POTION)
                        .name("&7Pozione di &cresistenza al fuoco")
                        .data(8227)
                        .amount(1)
                        .lore(" ")
                        .lore("&7➥ &aPrezzo: &e{cost} punti")
                        .lore(" ")
                        .lore("&bClicca per acquistare.")
                        .build();

                return new PurchasableItem(defaultItem, 17).cost(1);
            }
        }
    }

}
