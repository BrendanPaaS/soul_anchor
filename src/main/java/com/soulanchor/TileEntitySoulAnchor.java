package com.soulanchor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/** Holds the anchor's charge count. Configurable max, so it can't live in the blockstate. */
public class TileEntitySoulAnchor extends TileEntity {
    private int charges = 0;

    public int getCharges() { return charges; }

    public void setCharges(int value) {
        charges = Math.max(0, Math.min(value, ModConfig.maxCharges));
        markDirty();
        if (world != null) {
            // Update CHARGED blockstate (texture) and relight
            boolean lit = charges > 0;
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == Registration.SOUL_ANCHOR && state.getValue(BlockSoulAnchor.CHARGED) != lit) {
                world.setBlockState(pos, state.withProperty(BlockSoulAnchor.CHARGED, lit), 3);
            } else {
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            world.checkLight(pos);
        }
    }

    /** Light level scaled by percent full: 0..15 */
    public int getLightLevel() {
        if (charges <= 0) return 0;
        return Math.max(1, Math.round(15.0F * charges / ModConfig.maxCharges));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("charges", charges);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        charges = tag.getInteger("charges");
    }

    // --- client sync so light renders correctly ---

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (world != null && world.isRemote) world.checkLight(pos);
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                 net.minecraft.block.state.IBlockState oldState, net.minecraft.block.state.IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
