package snownee.zentweaker;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

public final class ZenTweakerCommonConfig {

    public static final ForgeConfigSpec spec;

    public static int cactusGrowAge = 5;
    public static boolean nerfSeaPickle = true;
    public static final Set<EntityType<?>> NO_AI_ENTITIES = Sets.newIdentityHashSet();

    private static IntValue cactusGrowAgeVal;
    private static BooleanValue nerfSeaPickleVal;
    private static ConfigValue<List<? extends String>> noAIEntitiesVal;

    static {
        spec = new ForgeConfigSpec.Builder().configure(ZenTweakerCommonConfig::new).getRight();
    }

    private ZenTweakerCommonConfig(ForgeConfigSpec.Builder builder) {
        cactusGrowAgeVal = builder.defineInRange("cactusGrowAge", cactusGrowAge, 0, 15);
        nerfSeaPickleVal = builder.define("nerfSeaPickle", nerfSeaPickle);
        noAIEntitiesVal = builder.defineList("noAIEntities", Collections::emptyList, Predicates.alwaysTrue());
    }

    public static void refresh() {
        cactusGrowAge = cactusGrowAgeVal.get();
        nerfSeaPickle = nerfSeaPickleVal.get();
        NO_AI_ENTITIES.clear();
        try {
            for (String s : noAIEntitiesVal.get()) {
                EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s));
                if (type != null) {
                    NO_AI_ENTITIES.add(type);
                }
            }
        } catch (Exception e) {
            ZenTweaker.logger.catching(e);
        }
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }
}
