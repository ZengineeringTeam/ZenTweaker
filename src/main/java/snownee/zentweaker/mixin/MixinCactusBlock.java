package snownee.zentweaker.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import snownee.zentweaker.ZenTweakerCommonConfig;

@Mixin(CactusBlock.class)
public abstract class MixinCactusBlock extends Block {
    public MixinCactusBlock(Properties properties) {
        super(properties);
    }

    private static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;

    @Override
    @Overwrite
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent growing cactus from loading unloaded chunks with block update
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        } else {
            BlockPos blockpos = pos.up();
            if (worldIn.isAirBlock(blockpos)) {
                int i;
                for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {}

                if (i < 3) {
                    int j = state.get(AGE);
                    if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, true)) {
                        if (j >= ZenTweakerCommonConfig.cactusGrowAge) {
                            worldIn.setBlockState(blockpos, this.getDefaultState());
                            BlockState blockstate = state.with(AGE, Integer.valueOf(0));
                            worldIn.setBlockState(pos, blockstate, 4);
                            blockstate.neighborChanged(worldIn, blockpos, this, pos, false);
                        } else {
                            worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
                        }
                        net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                    }
                }
            }
        }
    }

}
