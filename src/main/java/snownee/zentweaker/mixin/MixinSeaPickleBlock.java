package snownee.zentweaker.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import snownee.zentweaker.ZenTweakerCommonConfig;

@Mixin(SeaPickleBlock.class)
public abstract class MixinSeaPickleBlock extends BushBlock {
    private static final IntegerProperty PICKLES = BlockStateProperties.PICKLES_1_4;

    public MixinSeaPickleBlock(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "grow", cancellable = true)
    public void zentweaker_grow(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_, CallbackInfo info) {
        info.cancel();
        if (!this.isInBadEnvironment(p_225535_4_) && p_225535_1_.getBlockState(p_225535_3_.down()).isIn(BlockTags.CORAL_BLOCKS)) {
            int j = 1;
            int l = 0;
            int i1 = p_225535_3_.getX() - 2;
            int j1 = 0;

            for (int k1 = 0; k1 < 5; ++k1) {
                for (int l1 = 0; l1 < j; ++l1) {
                    int i2 = 2 + p_225535_3_.getY() - 1;

                    for (int j2 = i2 - 2; j2 < i2; ++j2) {
                        BlockPos blockpos = new BlockPos(i1 + k1, j2, p_225535_3_.getZ() - j1 + l1);
                        if (blockpos != p_225535_3_ && p_225535_2_.nextInt(6) == 0 && p_225535_1_.getBlockState(blockpos).getBlock() == Blocks.WATER) {
                            BlockState blockstate = p_225535_1_.getBlockState(blockpos.down());
                            if (blockstate.isIn(BlockTags.CORAL_BLOCKS)) {
                                if (ZenTweakerCommonConfig.nerfSeaPickle) {
                                    p_225535_1_.setBlockState(blockpos, Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, 1), 3);
                                } else {
                                    p_225535_1_.setBlockState(blockpos, Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, Integer.valueOf(p_225535_2_.nextInt(4) + 1)), 3);
                                }
                            }
                        }
                    }
                }

                if (l < 2) {
                    j += 2;
                    ++j1;
                } else {
                    j -= 2;
                    --j1;
                }

                ++l;
            }

            if (!ZenTweakerCommonConfig.nerfSeaPickle) {
                p_225535_1_.setBlockState(p_225535_3_, p_225535_4_.with(PICKLES, Integer.valueOf(4)), 2);
            }
        }
    }

    private boolean isInBadEnvironment(BlockState p_204901_1_) {
        return !p_204901_1_.get(BlockStateProperties.WATERLOGGED);
    }

}
