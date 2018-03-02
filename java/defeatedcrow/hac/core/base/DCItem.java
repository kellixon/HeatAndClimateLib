package defeatedcrow.hac.core.base;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import defeatedcrow.hac.core.ClimateCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DCItem extends Item implements ITexturePath {

	public DCItem() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		return 1.0F;
	}

	@Override
	public int getMetadata(int damage) {
		return Math.min(damage, getMaxMeta());
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int j = Math.min(stack.getMetadata(), getMaxMeta());
		return getNameSuffix() != null && j < getNameSuffix().length
				? super.getUnlocalizedName() + "_" + getNameSuffix()[j] : super.getUnlocalizedName();
	}

	public int getMaxMeta() {
		return 3;
	}

	public abstract String[] getNameSuffix();

	/**
	 * 移植補助
	 */

	@Override
	public EnumActionResult onItemUse(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		return this.onItemUse2(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	public EnumActionResult onItemUse2(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		return super.onItemUse(player == null ? null : player.getHeldItem(hand), player, world, pos, hand, facing, hitX,
				hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack item, World world, EntityPlayer player, EnumHand hand) {
		return this.onItemRightClick2(world, player, hand);
	}

	public ActionResult<ItemStack> onItemRightClick2(World world, EntityPlayer player, EnumHand hand) {
		return super.onItemRightClick(player == null ? null : player.getHeldItem(hand), world, player, hand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
		List<ItemStack> itms = getSubItemList();
		subItems.addAll(itms);
	}

	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		for (int i = 0; i < getMaxMeta() + 1; i++) {
			list.add(new ItemStack(this, 1, i));
		}
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		this.addInformation2(stack, player == null ? null : player.worldObj, tooltip);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {
		super.addInformation(stack, ClimateCore.proxy.getPlayer(), tooltip, false);
	}
}
