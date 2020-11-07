package snownee.zentweaker;

import java.util.Collections;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.base.Predicates;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class ZenTweakerClientConfig {

    public static final ForgeConfigSpec spec;

    public static ConfigValue<List<? extends String>> splashTextVal;
    public static ConfigValue<String> realmsBtnTitleVal;
    public static ConfigValue<String> realmsBtnURLVal;

    static {
        spec = new ForgeConfigSpec.Builder().configure(ZenTweakerClientConfig::new).getRight();
    }

    private ZenTweakerClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("mainMenu");
        splashTextVal = builder.defineList("splashText", Collections::emptyList, Predicates.alwaysTrue());
        realmsBtnTitleVal = builder.define("realmsBtnTitle", "");
        realmsBtnURLVal = builder.define("realmsBtnURL", "");
    }

    public static void refresh() {}

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event) {
        ((CommentedFileConfig) event.getConfig().getConfigData()).load();
    }
}
