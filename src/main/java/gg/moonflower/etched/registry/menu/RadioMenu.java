package gg.moonflower.etched.registry.menu;

import gg.moonflower.etched.registry.block.entity.RadioBlockEntity;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ocelot
 */
public class RadioMenu extends AbstractContainerMenu implements UrlMenu {

    private final ContainerLevelAccess access;
    private String initialUrl;

    public RadioMenu(int i, ContainerLevelAccess access) {
        super(EtchedMenuTypes.RADIO_MENU, i);
        this.access = access;
    }
    public RadioMenu(int i, Inventory ignored, String url) {
        this(i, url);
    }
    public RadioMenu(int i, String url) {
        this(i, ContainerLevelAccess.NULL);
        this.initialUrl = url;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, EtchedBlocks.RADIO);
    }

    @Override
    public void setUrl(String url) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity radio) {
                radio.setUrl(url);
            }
        });
    }

    /**
     * @return The original URL inside the radio before opening the menu
     */
    public String getInitialUrl() {
        return this.initialUrl;
    }
}