package teha.mixin;
import teha.client.TehaAntiCheatClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    private boolean h = false;
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        if (!h && !TehaAntiCheatClient.DETECTED_CHEATS.isEmpty()) {
            MinecraftClient.getInstance().setScreen(new TehaAntiCheatClient.WarningScreen((TitleScreen) (Object) this));
            h = true;
        }
    }
}
