package defeatedcrow.hac.core.plugin.baubles;

import baubles.api.BaublesApi;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelAmulet;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DCPluginBaubles {

	public static ItemStack getBaublesAmulet(EntityPlayer player) {
		ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(0);
		if (!DCUtil.isEmpty(item) && item.getItem() instanceof IJewelAmulet) {
			return item;
		}
		return ItemStack.EMPTY;
	}

	public static boolean hasBaublesAmulet(EntityPlayer player, ItemStack item) {
		ItemStack item2 = BaublesApi.getBaublesHandler(player).getStackInSlot(0);
		if (!DCUtil.isEmpty(item2) && !DCUtil.isEmpty(item)) {
			return item2.getItem() == item.getItem() && item2.getItemDamage() == item.getItemDamage();
		}
		return false;
	}

	public static void setBaublesAmuletEmpty(EntityPlayer player) {
		BaublesApi.getBaublesHandler(player).setStackInSlot(0, ItemStack.EMPTY);
		player.inventory.markDirty();
	}

	public static ItemStack getBaublesCharm(EntityPlayer player, CharmType type) {
		ItemStack item = BaublesApi.getBaublesHandler(player).getStackInSlot(6);
		if (!DCUtil.isEmpty(item) && item.getItem() instanceof IJewelCharm) {
			IJewelCharm charm = (IJewelCharm) item.getItem();
			if (type == null || charm.getType(item.getItemDamage()) == type) {
				return item;
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean hasBaublesCharm(EntityPlayer player, ItemStack item) {
		ItemStack item2 = BaublesApi.getBaublesHandler(player).getStackInSlot(6);
		if (!DCUtil.isEmpty(item2) && !DCUtil.isEmpty(item)) {
			return item2.getItem() == item.getItem() && item2.getItemDamage() == item.getItemDamage();
		}
		return false;
	}

	public static void setBaublesCharmEmpty(EntityPlayer player) {
		BaublesApi.getBaublesHandler(player).setStackInSlot(6, ItemStack.EMPTY);
		player.inventory.markDirty();
	}

}
