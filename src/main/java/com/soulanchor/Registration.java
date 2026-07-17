package com.soulanchor;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = SoulAnchorMod.MODID)
public class Registration {
    public static final BlockSoulAnchor SOUL_ANCHOR = new BlockSoulAnchor();

    @SubscribeEvent
    public static void blocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(SOUL_ANCHOR);
        GameRegistry.registerTileEntity(TileEntitySoulAnchor.class, new ResourceLocation(SoulAnchorMod.MODID, "soul_anchor"));
    }

    @SubscribeEvent
    public static void items(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(new ItemBlock(SOUL_ANCHOR).setRegistryName(SOUL_ANCHOR.getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void models(ModelRegistryEvent e) {
        Item i = Item.getItemFromBlock(SOUL_ANCHOR);
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }
}
