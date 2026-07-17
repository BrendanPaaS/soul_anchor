package com.soulanchor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnUtil {

    public static NBTTagCompound getData(EntityPlayer player) {
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        return root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    public static void clear(EntityPlayer player) {
        NBTTagCompound d = getData(player);
        d.removeTag("soulanchor_dim");
        d.removeTag("soulanchor_pos");
    }

    /** Finds a safe standing spot next to the anchor. */
    public static BlockPos findSafeSpot(World world, BlockPos anchor) {
        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    BlockPos p = anchor.add(x, y, z);
                    if (isSafe(world, p)) return p;
                }
            }
        }
        return null;
    }

    private static boolean isSafe(World world, BlockPos p) {
        return world.getBlockState(p.down()).isSideSolid(world, p.down(), EnumFacing.UP)
                && !world.getBlockState(p).causesSuffocation()
                && !world.getBlockState(p.up()).causesSuffocation();
    }
}
