package defeatedcrow.hac.core.client.base;

import defeatedcrow.hac.core.base.FoodEntityBase;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class DCFoodModelBase extends ModelBase {

	private final boolean isBaked;

	public DCFoodModelBase(boolean baked) {
		super();
		isBaked = baked;
	}

	public abstract void render(float scale, FoodEntityBase entity);

	public boolean isBaked() {
		return isBaked;
	}

}
