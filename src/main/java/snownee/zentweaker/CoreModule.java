package snownee.zentweaker;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;

@KiwiModule
@KiwiModule.Subscriber
public class CoreModule extends AbstractModule {

    public CoreModule() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ZenTweakerCommonConfig.spec);
        modEventBus.register(ZenTweakerCommonConfig.class);
        // if (FMLEnvironment.dist.isClient()) {
        //     ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModNameClientConfig.spec);
        //     modEventBus.register(ModNameClientConfig.class);
        // }
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        ZenTweakerCommonConfig.refresh();
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (ZenTweakerCommonConfig.NO_AI_ENTITIES.contains(event.getEntity().getType()) && event.getEntity() instanceof MobEntity) {
            ((MobEntity) event.getEntity()).setNoAI(true);
        }
    }

}
