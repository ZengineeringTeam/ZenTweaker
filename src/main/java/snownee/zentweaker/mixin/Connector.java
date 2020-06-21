package snownee.zentweaker.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import snownee.zentweaker.ZenTweaker;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        ZenTweaker.logger.info("Invoking Mixin Connector");
        Mixins.addConfiguration("assets/zentweaker/zentweaker.mixins.json");
    }

}
