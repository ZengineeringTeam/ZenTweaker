package snownee.zentweaker.mixin;

import java.net.URI;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.Strings;

import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import snownee.zentweaker.ZenTweaker;
import snownee.zentweaker.ZenTweakerClientConfig;

@Mixin(MainMenuScreen.class)
public class MixinMainMenuScreen extends Screen {

    protected MixinMainMenuScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    private static final Random RAND = new Random();
    @Shadow
    private String splashText;

    @Inject(at = @At("HEAD"), method = "areRealmsNotificationsEnabled", cancellable = true)
    public void zentweaker_areRealmsNotificationsEnabled(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(Boolean.FALSE);
    }

    @Inject(at = @At("TAIL"), method = "init")
    protected void zentweaker_init(CallbackInfo info) {
        List<? extends String> list = ZenTweakerClientConfig.splashTextVal.get();
        if (!list.isEmpty()) {
            String text = list.get(RAND.nextInt(list.size()));
            if (!Strings.isNullOrEmpty(text)) {
                splashText = I18n.format(text);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "addSingleplayerMultiplayerButtons", cancellable = true)
    private void zentweaker_addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn, CallbackInfo info) {
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.singleplayer"), (p_213089_1_) -> {
            this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.multiplayer"), (p_213086_1_) -> {
            if (this.minecraft.gameSettings.field_230152_Z_) {
                this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
            } else {
                this.minecraft.displayGuiScreen(new MultiplayerWarningScreen(this));
            }
        }));
        String title = ZenTweakerClientConfig.realmsBtnTitleVal.get();
        if (Strings.isNullOrEmpty(title)) {
            this.addButton(new Button(this.width / 2 + 2, yIn + rowHeightIn * 2, 98, 20, new TranslationTextComponent("menu.online"), (p_213095_1_) -> {
                this.switchToRealms();
            }));
        } else {
            this.addButton(new Button(this.width / 2 + 2, yIn + rowHeightIn * 2, 98, 20, new TranslationTextComponent(title), (p_213095_1_) -> {
                String uriStr = ZenTweakerClientConfig.realmsBtnURLVal.get();
                if (Strings.isNullOrEmpty(uriStr)) {
                    return;
                }
                URI uri;
                try {
                    uri = new URI(uriStr);
                } catch (Exception e) {
                    ZenTweaker.logger.catching(e);
                    return;
                }
                Util.getOSType().openURI(uri);
            }));
        }
        info.cancel();
    }

    @Shadow
    private void switchToRealms() {
    }
}
