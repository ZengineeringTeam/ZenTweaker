package snownee.zentweaker;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.Util;

@KiwiModule
@KiwiModule.Subscriber
public class CoreModule extends AbstractModule {

    public CoreModule() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ZenTweakerCommonConfig.spec);
        modEventBus.register(ZenTweakerCommonConfig.class);
        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ZenTweakerClientConfig.spec);
            modEventBus.register(ZenTweakerClientConfig.class);
        }
    }

    @Override
    protected void init(FMLCommonSetupEvent event) {
        ZenTweakerCommonConfig.refresh();

        for (String entry : ZenTweakerCommonConfig.blockResistanceVal.get()) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            ResourceLocation key = Util.RL(parts[0]);
            if (key == null) {
                continue;
            }
            Block block = ForgeRegistries.BLOCKS.getValue(key);
            if (block == null) {
                continue;
            }
            float resistance;
            try {
                resistance = Float.parseFloat(parts[1]);
            } catch (Exception e) {
                continue;
            }
            block.blastResistance = resistance;
        }
    }

    @Override
    protected void clientInit(FMLClientSetupEvent event) {
        ZenTweakerClientConfig.refresh();
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (ZenTweakerCommonConfig.NO_AI_ENTITIES.contains(event.getEntity().getType()) && event.getEntity() instanceof MobEntity) {
            ((MobEntity) event.getEntity()).setNoAI(true);
        }
    }

}
