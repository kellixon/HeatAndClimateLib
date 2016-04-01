package defeatedcrow.hac.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;

/**
 * Climateを条件に進む、1:1交換のレシピ。<br>
 * 材料は辞書対応。
 */
public interface IClimateSmelting {

	/**
	 * Input登録内容
	 */
	Object getInput();

	ItemStack getOutput();

	ItemStack getSecondary();

	float getSecondaryChance();

	/**
	 * Inputのコンテナアイテムのリスト
	 */
	ItemStack getContainerItem(ItemStack item);

	/**
	 * macth条件判定用、鉱石辞書変換後のInputリスト
	 */
	List<ItemStack> getProcessedInput();

	/**
	 * Input条件判定
	 */
	boolean matcheInput(ItemStack item);

	boolean matchClimate(int code);

	/**
	 * Climate条件判定
	 */
	boolean matchClimate(IClimate climate);

	/**
	 * 追加条件
	 * trueで条件クリア
	 */
	boolean additionalRequire(World world, BlockPos pos);

	/**
	 * 0: 設置不可, 1: Block, 2: Entity
	 */
	int hasPlaceableOutput();

	/**
	 * おもにレシピ条件表示機能用
	 */
	List<DCHeatTier> requiredHeat();

	List<DCHumidity> requiredHum();

	List<DCAirflow> requiredAir();
}
