package teha.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class TehaHackCheck implements ClientModInitializer {
    public record DetectedMod(String name, Text details) {}
    public static final List<DetectedMod> DETECTED_CHEATS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        System.out.println("[teha-hackcheck] Initializing...");
        checkCheats();
        System.out.println("[teha-hackcheck] Detected " + DETECTED_CHEATS.size() + " suspicious mods.");
    }

    private void checkCheats() {
        DETECTED_CHEATS.clear();
        for (ModContainer m : FabricLoader.getInstance().getAllMods()) {
            if (m.getOrigin().getKind() != ModOrigin.Kind.PATH) continue;
            String id = m.getMetadata().getId();
            if (id.equals("fabricloader") || id.equals("java") || id.equals("minecraft") || 
                id.equals("teha-hackcheck") ||
                (id.startsWith("fabric-") && !id.equals("fabric-api")) || id.equals("mixinextras")) continue;
            
            var res = Detector.detect(m);
            if (res.detected()) {
                DETECTED_CHEATS.add(new DetectedMod(m.getMetadata().getName(), res.summary()));
            }
        }
    }

    public static class Detector {
        enum Weight {
            CRITICAL(10), HIGH(5), MEDIUM(2);
            final int v;
            Weight(int v) { this.v = v; }
        }

        record KW(String kw, Weight w) {}

        static final Set<String> ID_BL = Set.of(
            "meteor-client","wurst","liquidbounce","aristois","bleachhack",
            "impact","inertia","future","rusherhack","lambda","kami-blue",
            "phobos","salhack","forgehax","xaero-minimap-fair",
            "ghosthack","sigma","wolfram","huzuni","jigsaw","konas"
        );

        static final List<KW> KWS = List.of(
            new KW("killaura",Weight.CRITICAL),new KW("autocrystal",Weight.CRITICAL),new KW("crystalaura",Weight.CRITICAL),
            new KW("aimbot",Weight.CRITICAL),new KW("triggerbot",Weight.CRITICAL),new KW("bowaimbot",Weight.CRITICAL),
            new KW("auto-totem",Weight.CRITICAL),new KW("autototem",Weight.CRITICAL),
            new KW("anticheat bypass",Weight.CRITICAL),new KW("anticheat_bypass",Weight.CRITICAL),
            new KW("hackmodule",Weight.CRITICAL),new KW("cheatmodule",Weight.CRITICAL),
            new KW("hackregistry",Weight.CRITICAL),new KW("cheat engine",Weight.CRITICAL),
            new KW("wallhack",Weight.CRITICAL),new KW("noclip hack",Weight.CRITICAL),
            new KW("teleport hack",Weight.CRITICAL),new KW("fly hack",Weight.CRITICAL),
            new KW("speed hack",Weight.CRITICAL),new KW("blink hack",Weight.CRITICAL),
            new KW("reach hack",Weight.CRITICAL),new KW("phase hack",Weight.CRITICAL),
            new KW("bookcrash",Weight.CRITICAL),new KW("scaffold",Weight.HIGH),
            new KW("nuker",Weight.HIGH),new KW("freecam",Weight.HIGH),new KW("xray",Weight.HIGH),
            new KW("bunnyhop",Weight.HIGH),new KW("antiknockback",Weight.HIGH),new KW("anti knockback",Weight.HIGH),
            new KW("autoclicker",Weight.HIGH),new KW("fastbreak",Weight.HIGH),new KW("bedaura",Weight.HIGH),
            new KW("anchoraura",Weight.HIGH),new KW("clickgui",Weight.HIGH),new KW("hack menu",Weight.HIGH),
            new KW("cheat menu",Weight.HIGH),new KW("boatfly",Weight.HIGH),new KW("elytrafly",Weight.HIGH),
            new KW("nofall hack",Weight.HIGH),new KW("step hack",Weight.HIGH),new KW("jesushack",Weight.HIGH),
            new KW("chest stealer",Weight.HIGH),new KW("invmanager",Weight.HIGH),new KW("module manager",Weight.HIGH),
            new KW("hack manager",Weight.HIGH),new KW("modulemanager",Weight.HIGH),new KW("moduleregistry",Weight.HIGH),
            new KW("grim bypass",Weight.HIGH),new KW("disabler hack",Weight.HIGH),new KW("storageesp",Weight.HIGH),
            new KW("playeresp",Weight.HIGH),new KW("holeesp",Weight.HIGH),new KW("cavefinder",Weight.HIGH),
            new KW("fastbow",Weight.HIGH),new KW("fastplace",Weight.HIGH),new KW("noslowdown",Weight.HIGH),
            new KW("autolog",Weight.HIGH),new KW("auto-log",Weight.HIGH),new KW("autoanchor",Weight.HIGH),
            new KW("autobed",Weight.HIGH),new KW("autopot",Weight.HIGH),new KW("autobuild",Weight.HIGH),
            new KW("autoweapon",Weight.HIGH),new KW("autoarmor",Weight.HIGH),new KW("combat module",Weight.MEDIUM),
            new KW("movement module",Weight.MEDIUM),new KW("render module",Weight.MEDIUM),new KW("auto-eat",Weight.MEDIUM),
            new KW("autoeat",Weight.MEDIUM),new KW("auto-fish",Weight.MEDIUM),new KW("autofish",Weight.MEDIUM),
            new KW("auto-mine",Weight.MEDIUM),new KW("automine",Weight.MEDIUM),new KW("autofarm",Weight.MEDIUM),
            new KW("autosoup",Weight.MEDIUM),new KW("autogap",Weight.MEDIUM)
        );

        public static Res detect(ModContainer mod) {
            String id = mod.getMetadata().getId().toLowerCase();
            if (ID_BL.contains(id)) return Res.blacklisted(id);
            if (mod.getOrigin().getKind() == ModOrigin.Kind.PATH) {
                List<Path> ps = mod.getOrigin().getPaths();
                if (!ps.isEmpty()) return scanJar(ps.get(0), id);
            }
            return Res.clean();
        }

        private static Res scanJar(Path p, String id) {
            Map<String,Integer> hits = new LinkedHashMap<>();
            try (InputStream is = Files.newInputStream(p); JarInputStream jis = new JarInputStream(is)) {
                JarEntry e;
                while ((e = jis.getNextJarEntry()) != null) {
                    String n = e.getName().toLowerCase();
                    score(n, hits, Weight.HIGH);
                    if (n.endsWith(".class") || n.endsWith(".json")) {
                        byte[] b = jis.readAllBytes();
                        if (n.endsWith(".class")) {
                            for (String c : extract(b)) score(c.toLowerCase(), hits, Weight.CRITICAL);
                        }
                        score(new String(b, StandardCharsets.ISO_8859_1).toLowerCase(), hits, Weight.MEDIUM);
                    }
                }
            } catch (Exception ignored) {}
            
            int t = hits.values().stream().mapToInt(Integer::intValue).sum();
            return t >= 10 ? Res.detected(id, t, hits) : Res.clean();
        }

        private static List<String> extract(byte[] b) {
            List<String> r = new ArrayList<>();
            if (b.length < 10 || (b[0]&0xFF) != 0xCA || (b[1]&0xFF) != 0xFE || (b[2]&0xFF) != 0xBA || (b[3]&0xFF) != 0xBE) return r;
            try {
                int cnt = ((b[8]&0xFF) << 8) | (b[9]&0xFF);
                for (int cp = 1, i = 10; cp < cnt && i < b.length; cp++) {
                    int t = b[i++] & 0xFF;
                    if (t == 1) {
                        int l = ((b[i]&0xFF) << 8) | (b[i+1]&0xFF); i += 2;
                        if (i + l <= b.length) {
                            String s = new String(b, i, l, StandardCharsets.UTF_8);
                            if (s.length() >= 4) r.add(s);
                        }
                        i += l;
                    } else if (t == 7 || t == 8 || t == 16 || t == 19 || t == 20) i += 2;
                    else if (t == 3 || t == 4 || t == 9 || t == 10 || t == 11 || t == 12 || t == 17 || t == 18) i += 4;
                    else if (t == 5 || t == 6) { i += 8; cp++; }
                    else if (t == 15) i += 3;
                }
            } catch (Exception ignored) {}
            return r;
        }

        private static void score(String t, Map<String,Integer> h, Weight m) {
            for (KW kw : KWS) if (t.contains(kw.kw())) {
                int w = Math.min(kw.w().v, m.v);
                h.merge(kw.kw(), w, Integer::sum);
            }
        }

        public record Res(boolean detected, boolean blacklisted, String modId, int score, Map<String,Integer> hits) {
            static Res clean() { return new Res(false, false, "", 0, Map.of()); }
            static Res blacklisted(String id) { return new Res(true, true, id, Integer.MAX_VALUE, Map.of()); }
            static Res detected(String id, int s, Map<String,Integer> h) {
                return new Res(true, false, id, s, Collections.unmodifiableMap(h));
            }
            public Text summary() {
                if (!detected) return Text.translatable("teha-hackcheck.clean");
                if (blacklisted) return Text.translatable("teha-hackcheck.blacklisted", modId);
                return Text.translatable("teha-hackcheck.detected_hacks", String.join(", ", hits.keySet()));
            }
        }
    }

    public static class WarningScreen extends Screen {
        private final Screen parent;
        private double scrollAmount = 0;
        private int totalListHeight = 0;
        
        public WarningScreen(Screen p) {
            super(Text.translatable("teha-hackcheck.screen.title").formatted(Formatting.RED, Formatting.BOLD));
            this.parent = p;
        }

        @Override
        protected void init() {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("teha-hackcheck.screen.continue"), 
                b -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build());
            
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("teha-hackcheck.screen.quit"), 
                b -> this.client.stop()).dimensions(this.width / 2 - 100, this.height - 65, 200, 20).build());
        }

        @Override
        public void render(DrawContext c, int mx, int my, float d) {
            if (this.parent != null) this.parent.renderBackground(c, mx, my, d);
            c.fill(0, 0, this.width, this.height, 0x99000000);
            super.render(c, mx, my, d);
            
            int cx = this.width / 2, yStart = 50;
            c.drawCenteredTextWithShadow(this.textRenderer, this.title, cx, yStart, 0xFFFFFFFF);
            c.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("teha-hackcheck.screen.warning").formatted(Formatting.YELLOW, Formatting.BOLD), cx, yStart + 20, 0xFFFFFFFF);
            
            int listTop = yStart + 35;
            int listBottom = this.height - 80;
            int availableHeight = listBottom - listTop;

            if (totalListHeight > availableHeight) {
                c.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("teha-hackcheck.screen.scroll").formatted(Formatting.DARK_GRAY, Formatting.ITALIC), cx, listTop, 0xFFFFFFFF);
                listTop += 15;
            }

            c.enableScissor(0, listTop, this.width, listBottom);
            int y = (int) (listTop - scrollAmount);
            
            for (DetectedMod m : DETECTED_CHEATS) {
                c.drawCenteredTextWithShadow(this.textRenderer, Text.literal(m.name()).formatted(Formatting.RED, Formatting.BOLD), cx, y, 0xFFFFFFFF);
                y += 12;
                c.drawCenteredTextWithShadow(this.textRenderer, m.details().copy().formatted(Formatting.GRAY), cx, y, 0xFFFFFFFF);
                y += 20;
            }
            
            this.totalListHeight = y - (int)(listTop - scrollAmount);
            c.disableScissor();

            if (totalListHeight > availableHeight) {
                c.fillGradient(0, listBottom - 20, this.width, listBottom, 0x00000000, 0x99000000);
            } else {
                this.scrollAmount = 0; // 画面内に収まっている場合はスクロールリセット
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            int listTop = 105; 
            int listBottom = this.height - 80;
            if (totalListHeight > (listBottom - listTop)) {
                this.scrollAmount = Math.max(0, Math.min(totalListHeight - (listBottom - listTop), this.scrollAmount - verticalAmount * 20));
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldCloseOnEsc() { return false; }
    }
}
