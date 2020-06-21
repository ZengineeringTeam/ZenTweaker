package snownee.zentweaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(ZenTweaker.MODID)
@EventBusSubscriber
public final class ZenTweaker {

    public static final String MODID = "zentweaker";
    public static final Logger logger = LogManager.getLogger("Zentweaker");

}
