package snownee.zentweaker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public final class ZenTweakerCommonConfig {

    public static final ForgeConfigSpec spec;

    public static int cactusGrowAge = 5;
    public static boolean nerfSeaPickle = true;

    private static IntValue cactusGrowAgeVal;
    private static BooleanValue nerfSeaPickleVal;

    static {
        spec = new ForgeConfigSpec.Builder().configure(ZenTweakerCommonConfig::new).getRight();
    }

    private ZenTweakerCommonConfig(ForgeConfigSpec.Builder builder) {
        cactusGrowAgeVal = builder.defineInRange("cactusGrowAge", cactusGrowAge, 0, 15);
        nerfSeaPickleVal = builder.define("nerfSeaPickle", nerfSeaPickle);
    }

    public static void refresh() {
        cactusGrowAge = cactusGrowAgeVal.get();
        nerfSeaPickle = nerfSeaPickleVal.get();
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
        refresh();
    }
}
