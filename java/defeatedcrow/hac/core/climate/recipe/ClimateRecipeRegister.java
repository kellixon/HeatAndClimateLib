package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IClimateRecipe;
import defeatedcrow.hac.api.recipe.IClimateRecipeRegister;
import defeatedcrow.hac.api.recipe.RecipeAPI;

public class ClimateRecipeRegister implements IClimateRecipeRegister {

	public ClimateRecipeRegister() {
		this.absList = new ArrayList<ClimateRecipe>();
		this.coldList = new ArrayList<ClimateRecipe>();
		this.normalList = new ArrayList<ClimateRecipe>();
		this.hotList = new ArrayList<ClimateRecipe>();
		this.ovenList = new ArrayList<ClimateRecipe>();
		this.kilnList = new ArrayList<ClimateRecipe>();
		this.smeltList = new ArrayList<ClimateRecipe>();
		this.uhtList = new ArrayList<ClimateRecipe>();
	}

	public IClimateRecipeRegister instance() {
		return RecipeAPI.registerRecipes;
	}

	/*
	 * RecipeListは温度ごとに別になっている。
	 */
	private static List<ClimateRecipe> absList;
	private static List<ClimateRecipe> coldList;
	private static List<ClimateRecipe> normalList;
	private static List<ClimateRecipe> hotList;
	private static List<ClimateRecipe> ovenList;
	private static List<ClimateRecipe> kilnList;
	private static List<ClimateRecipe> smeltList;
	private static List<ClimateRecipe> uhtList;

	@Override
	public List<? extends ClimateRecipe> getRecipeList(DCHeatTier tier) {
		switch (tier) {
		case ABSOLUTE:
			return absList;
		case COLD:
			return coldList;
		case NORMAL:
			return normalList;
		case HOT:
			return hotList;
		case OVEN:
			return ovenList;
		case KILN:
			return kilnList;
		case SMELTING:
			return smeltList;
		case UHT:
			return uhtList;
		default:
			return ovenList;
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, DCHeatTier heat,
			DCHumidity hum, DCAirflow air, Object... input) {
		List<IClimateRecipe> list = (List<IClimateRecipe>) getRecipeList(heat);
		if (input != null && output != null && heat != null) {
			list.add(new ClimateRecipe(output, secondary, heat, hum, air, secondaryChance, input));
		}
	}

	@Override
	public void addRecipe(ItemStack output, ItemStack secondary, float secondaryChance, DCHeatTier heat,
			Object... input) {
		this.addRecipe(output, secondary, secondaryChance, heat, null, null, input);
	}

	@Override
	public void addRecipe(ItemStack output, DCHeatTier heat, Object... input) {
		this.addRecipe(output, null, heat, null, null, input);
	}

	@Override
	public void addRecipe(IClimateRecipe recipe, DCHeatTier heat) {
		List<IClimateRecipe> list = (List<IClimateRecipe>) getRecipeList(heat);
		list.add(recipe);
	}

	@Override
	public IClimateRecipe getRecipe(IClimate clm, List<ItemStack> items) {
		List<IClimateRecipe> list = (List<IClimateRecipe>) getRecipeList(clm.getHeat());
		if (list.isEmpty()) {
			return null;
		} else {
			for (IClimateRecipe recipe : list) {
				if (recipe.matches(items) && recipe.matchClimate(clm)) {
					return recipe;
				}
			}
			return null;
		}
	}

	@Override
	public IClimateRecipe getRecipe(int code, List<ItemStack> items) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		List<IClimateRecipe> list = (List<IClimateRecipe>) getRecipeList(clm.getHeat());
		if (list.isEmpty()) {
			return null;
		} else {
			for (IClimateRecipe recipe : list) {
				if (recipe.matches(items) && recipe.matchClimate(clm)) {
					return recipe;
				}
			}
			return null;
		}
	}

}
