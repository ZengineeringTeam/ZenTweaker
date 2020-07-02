//package snownee.zentweaker.mixin;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//import it.unimi.dsi.fastutil.objects.Object2IntFunction;
//import net.minecraft.entity.EntityClassification;
//import net.minecraft.world.server.ServerChunkProvider;
//
//@Mixin(ServerChunkProvider.class)
//public class MixinServerChunkProvider {
//
//    @Redirect(
//            at = @At(
//                    value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Lnet/minecraft/entity/EntityClassification;)I"
//            ), method = "tickChunks"
//    )
//    private int zenRedirect_getInt(Object2IntFunction map, EntityClassification entityclassification) {
//        System.out.println(entityclassification);
//        System.out.println(map);
//        return map.getInt(entityclassification);
//    }
//
//}
