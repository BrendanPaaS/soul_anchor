package com.soulanchor;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SoulAnchorMod.MODID)
public class ModConfig {

    @Config.Comment("Dimension IDs the Soul Anchor can be used in. Default: -1 (Nether)")
    public static int[] dimensionWhitelist = { -1 };

    @Config.Comment("If false, beds no longer set your respawn point (you can still sleep).")
    public static boolean bedsSetSpawn = true;

    @Config.Comment("If true, using the anchor in a non-whitelisted dimension explodes like a bed.")
    public static boolean intentionalGameDesign = true;

    @Config.Comment("Item used to charge the anchor. One is consumed per charge. Leave empty for no item requirement (first click charges to full).")
    public static String activationItem = "minecraft:glowstone";

    @Config.Comment("Maximum number of charges the anchor can hold.")
    @Config.RangeInt(min = 1, max = 64)
    public static int maxCharges = 4;

    public static boolean isWhitelisted(int dim) {
        for (int d : dimensionWhitelist) if (d == dim) return true;
        return false;
    }

    @Mod.EventBusSubscriber(modid = SoulAnchorMod.MODID)
    private static class Handler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
            if (e.getModID().equals(SoulAnchorMod.MODID)) {
                ConfigManager.sync(SoulAnchorMod.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
