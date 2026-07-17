package com.soulanchor;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class SoulTeleporter extends Teleporter {
    public SoulTeleporter(WorldServer world) { super(world); }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) { }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) { return true; }

    @Override
    public boolean makePortal(Entity entity) { return true; }

    @Override
    public void removeStalePortalLocations(long worldTime) { }
}
