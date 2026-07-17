package com.soulanchor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SoulAnchorMod.MODID, name = "Soul Anchor", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
public class SoulAnchorMod {
    public static final String MODID = "soulanchor";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
