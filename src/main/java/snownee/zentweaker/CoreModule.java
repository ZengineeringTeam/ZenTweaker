package snownee.zentweaker;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
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

        for (String entry : ZenTweakerCommonConfig.itemContainerVal.get()) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            ResourceLocation key = Util.RL(parts[0]);
            if (key == null) {
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(key);
            if (item == null) {
                continue;
            }
            key = Util.RL(parts[1]);
            if (key == null) {
                continue;
            }
            Item container = ForgeRegistries.ITEMS.getValue(key);
            if (container == null) {
                continue;
            }
            item.containerItem = container;
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

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            CommandSource source = getCommandSource((ServerPlayerEntity) player);
            for (String command : ZenTweakerCommonConfig.respawnCommandsVal.get()) {
                player.getServer().getCommandManager().handleCommand(source, command);
            }
        }
    }

    public static CommandSource getCommandSource(ServerPlayerEntity playerIn) {
        String s = playerIn.getName().getString();
        ITextComponent itextcomponent = playerIn.getDisplayName();
        return new CommandSource(ICommandSource.DUMMY, playerIn.getPositionVec(), playerIn.getPitchYaw(), playerIn.world instanceof ServerWorld ? (ServerWorld) playerIn.world : null, 2, s, itextcomponent, playerIn.world.getServer(), playerIn);
    }
}
