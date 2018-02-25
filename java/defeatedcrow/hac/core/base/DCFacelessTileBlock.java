package defeatedcrow.hac.core.base;

import java.util.List;
import java.util.Random;

import defeatedcrow.hac.api.blockstate.DCState;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateObject;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TESR持ちブロックのベース
public abstract class DCFacelessTileBlock extends BlockContainerDC implements IClimateObject, INameSuffix {

	protected Random rand = new Random();

	// Type上限
	public final int maxMeta;
	public final boolean forceUpdate;

	public DCFacelessTileBlock(Material m, String s, int max, boolean force) {
		super(m, s);
		this.setHardness(0.5F);
		this.setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCState.TYPE16, 0));
		this.maxMeta = max;
		forceUpdate = force;
		this.fullBlock = false;
		this.lightOpacity = 0;
	}

	public int getMaxMeta() {
		return maxMeta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = super.getSubItemList();
		for (int i = 0; i < maxMeta + 1; i++) {
			list.add(new ItemStack(this, 1, i));
		}
		return list;
	}

	// 設置・破壊処理
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		DCTileEntity te = null;
		if (tile != null && tile instanceof DCTileEntity) {
			te = (DCTileEntity) tile;
		}

		if (te != null) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				te.setNBT(tag);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		DCTileEntity te = null;
		if (tile instanceof DCTileEntity) {
			te = (DCTileEntity) tile;
		}
		int i = this.damageDropped(state);
		if (te != null) {
			ItemStack drop = new ItemStack(this, 1, i);
			NBTTagCompound tag = new NBTTagCompound();
			tag = te.getNBT(tag);
			if (tag != null)
				drop.setTagCompound(tag);
			EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
					drop);
			float f3 = 0.05F;
			entityitem.motionX = (float) this.rand.nextGaussian() * f3;
			entityitem.motionY = (float) this.rand.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) this.rand.nextGaussian() * f3;
			world.spawnEntityInWorld(entityitem);
		}
		world.updateComparatorOutputLevel(pos, state.getBlock());
	}

	@Override
	public int damageDropped(IBlockState state) {
		int i = state.getValue(DCState.TYPE16);
		if (i > maxMeta)
			i = maxMeta;
		return i;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int i = meta & 15;
		if (i > maxMeta)
			i = maxMeta;
		IBlockState state = this.getDefaultState().withProperty(DCState.TYPE16, i);
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		i = state.getValue(DCState.TYPE16);
		if (i > maxMeta)
			i = maxMeta;

		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCState.TYPE16
		});
	}

	/* climate */
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (!worldIn.isRemote && state != null && state.getBlock() != null) {
			IClimate clm = this.onUpdateClimate(worldIn, pos, state);
			if (!this.onClimateChange(worldIn, pos, state, clm) && this.isForcedTickUpdate()) {
				worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(20));
			}
		}
	}

	@Override
	public IClimate onUpdateClimate(World world, BlockPos pos, IBlockState state) {
		DCHeatTier heat = ClimateAPI.calculator.getAverageTemp(world, pos, checkingRange()[0], false);
		DCHumidity hum = ClimateAPI.calculator.getHumidity(world, pos, checkingRange()[1], false);
		DCAirflow air = ClimateAPI.calculator.getAirflow(world, pos, checkingRange()[2], false);
		IClimate c = ClimateAPI.register.getClimateFromParam(heat, hum, air);
		return c;
	}

	@Override
	public boolean onClimateChange(World world, BlockPos pos, IBlockState state, IClimate clm) {
		if (clm != null) {
			DCHeatTier heat = clm.getHeat();
			DCHumidity hum = clm.getHumidity();
			DCAirflow air = clm.getAirflow();
			int meta = this.getMetaFromState(state);
			ItemStack check = new ItemStack(this, 1, meta);
			IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, check);
			if (recipe != null && recipe.additionalRequire(world, pos)) {
				ItemStack output = recipe.getOutput();
				if (!DCUtil.isEmpty(output) && output.getItem() instanceof ItemBlock) {
					Block ret = ((ItemBlock) output.getItem()).getBlock();
					IBlockState retS = ret.getStateFromMeta(output.getMetadata());
					if (world.setBlockState(pos, retS, 2)) {
						world.notifyBlockOfStateChange(pos, ret);

						// 効果音
						if (playSEOnChanging(meta)) {
							world.playSound(null, pos, getSE(meta), SoundCategory.BLOCKS, 0.8F, 2.0F);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public SoundEvent getSE(int meta) {
		return SoundEvents.BLOCK_LAVA_EXTINGUISH;
	}

	@Override
	public boolean playSEOnChanging(int meta) {
		return true;
	}

	@Override
	public String[] getNameSuffix() {
		return null;
	}

	@Override
	public boolean isForcedTickUpdate() {
		return forceUpdate;
	}

	@Override
	public int[] checkingRange() {
		return CoreConfigDC.ranges;
	}

}
