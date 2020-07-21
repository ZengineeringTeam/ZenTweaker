package snownee.zentweaker.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import snownee.zentweaker.ZenTweakerCommonConfig;

@Mixin(WitherSkeletonSkullBlock.class)
public class MixinWitherSkeletonSkullBlock {

    @Shadow
    @Nullable
    private static BlockPattern witherPatternFull;
    @Shadow
    @Nullable
    private static BlockPattern witherPatternBase;

    @Inject(at = @At("HEAD"), method = "checkWitherSpawn", cancellable = true)
    private static void zentweaker_checkWitherSpawn(World worldIn, BlockPos pos, SkullTileEntity p_196298_2_, CallbackInfo info) {
        if (!ZenTweakerCommonConfig.peacefulNetherStar) {
            return;
        }
        info.cancel();
        if (!worldIn.isRemote) {
            Block block = p_196298_2_.getBlockState().getBlock();
            boolean flag = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
            if (flag && pos.getY() >= 2) {
                BlockPattern blockpattern = getOrCreateWitherFull();
                BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(worldIn, pos);
                if (blockpattern$patternhelper != null) {
                    for (int i = 0; i < blockpattern.getPalmLength(); ++i) {
                        for (int j = 0; j < blockpattern.getThumbLength(); ++j) {
                            CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, 0);
                            worldIn.setBlockState(cachedblockinfo.getPos(), Blocks.AIR.getDefaultState(), 2);
                            worldIn.playEvent(2001, cachedblockinfo.getPos(), Block.getStateId(cachedblockinfo.getBlockState()));
                        }
                    }

                    BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();

                    if (worldIn.getDifficulty() == Difficulty.PEACEFUL) {
                        double d0 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        double d1 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        double d2 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        ItemEntity itementity = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(Items.NETHER_STAR));
                        itementity.setDefaultPickupDelay();
                        worldIn.addEntity(itementity);
                        return;
                    }

                    WitherEntity witherentity = EntityType.WITHER.create(worldIn);
                    witherentity.setLocationAndAngles(blockpos.getX() + 0.5D, blockpos.getY() + 0.55D, blockpos.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                    witherentity.renderYawOffset = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                    witherentity.ignite();

                    for (ServerPlayerEntity serverplayerentity : worldIn.getEntitiesWithinAABB(ServerPlayerEntity.class, witherentity.getBoundingBox().grow(50.0D))) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, witherentity);
                    }

                    worldIn.addEntity(witherentity);

                    for (int k = 0; k < blockpattern.getPalmLength(); ++k) {
                        for (int l = 0; l < blockpattern.getThumbLength(); ++l) {
                            worldIn.notifyNeighbors(blockpattern$patternhelper.translateOffset(k, l, 0).getPos(), Blocks.AIR);
                        }
                    }

                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "canSpawnMob", cancellable = true)
    private static void zentweaker_canSpawnMob(World p_196299_0_, BlockPos p_196299_1_, ItemStack p_196299_2_, CallbackInfoReturnable<Boolean> info) {
        if (p_196299_2_.getItem() == Items.WITHER_SKELETON_SKULL && p_196299_1_.getY() >= 2 && !p_196299_0_.isRemote) {
            info.setReturnValue(getOrCreateWitherBase().match(p_196299_0_, p_196299_1_) != null);
        } else {
            info.setReturnValue(false);
        }
    }

    private static BlockPattern getOrCreateWitherFull() {
        if (witherPatternFull == null) {
            witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return witherPatternFull;
    }

    private static BlockPattern getOrCreateWitherBase() {
        if (witherPatternBase == null) {
            witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return witherPatternBase;
    }

}
