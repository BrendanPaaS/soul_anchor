package com.soulanchor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class EventHandler {

    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent e) {
        if (!ModConfig.bedsSetSpawn && !e.isForced()) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.isEndConquered()) return;
        if (!(e.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) e.player;

        NBTTagCompound d = SpawnUtil.getData(player);
        if (!d.hasKey("soulanchor_pos")) return;

        int dim = d.getInteger("soulanchor_dim");
        BlockPos pos = BlockPos.fromLong(d.getLong("soulanchor_pos"));

        MinecraftServer server = player.getServer();
        if (server == null) return;
        WorldServer world = server.getWorld(dim);
        if (world == null) { SpawnUtil.clear(player); return; }

        world.getChunk(pos);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != Registration.SOUL_ANCHOR) { SpawnUtil.clear(player); return; }

        TileEntity rawTe = world.getTileEntity(pos);
        if (!(rawTe instanceof TileEntitySoulAnchor)) { SpawnUtil.clear(player); return; }
        TileEntitySoulAnchor te = (TileEntitySoulAnchor) rawTe;

        // No charges left -> normal respawn, unbind (like a missing bed)
        if (te.getCharges() <= 0) { SpawnUtil.clear(player); return; }

        BlockPos safe = SpawnUtil.findSafeSpot(world, pos);
        if (safe == null) { SpawnUtil.clear(player); return; }

        if (player.dimension != dim) {
            server.getPlayerList().transferPlayerToDimension(player, dim, new SoulTeleporter(world));
        }
        player.setPositionAndUpdate(safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D);

        // Consume one charge on use
        te.setCharges(te.getCharges() - 1);
        if (te.getCharges() <= 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.8F);
        }
    }
}
