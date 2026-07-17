package com.soulanchor;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSoulAnchor extends BlockContainer {
    public static final PropertyBool CHARGED = PropertyBool.create("charged");

    public BlockSoulAnchor() {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(SoulAnchorMod.MODID, "soul_anchor"));
        setTranslationKey(SoulAnchorMod.MODID + ".soul_anchor");
        setHardness(22.5F);
        setResistance(1000.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setDefaultState(blockState.getBaseState().withProperty(CHARGED, false));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySoulAnchor();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CHARGED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGED) ? 1 : 0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntitySoulAnchor) return ((TileEntitySoulAnchor) te).getLightLevel();
        return 0;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(Blocks.OBSIDIAN, 8));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.OBSIDIAN);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 8;
    }

    /** The configured charge item, or null if none set / not found. */
    private static Item getChargeItem() {
        String id = ModConfig.activationItem;
        if (id == null || id.trim().isEmpty()) return null;
        return Item.REGISTRY.getObject(new ResourceLocation(id.trim()));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity rawTe = world.getTileEntity(pos);
        if (!(rawTe instanceof TileEntitySoulAnchor)) return true;
        TileEntitySoulAnchor te = (TileEntitySoulAnchor) rawTe;

        int charges = te.getCharges();
        Item chargeItem = getChargeItem();
        ItemStack held = player.getHeldItem(hand);

        // Shift + right click with empty hand: check charges only
        if (player.isSneaking() && held.isEmpty()) {
            player.sendStatusMessage(new TextComponentString("Charges: " + charges + "/" + ModConfig.maxCharges), true);
            return true;
        }

        // Charging: holding the charge item and not full -> add a charge (works in any dimension)
        if (chargeItem != null && held.getItem() == chargeItem && charges < ModConfig.maxCharges) {
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            te.setCharges(charges + 1);
            world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.sendStatusMessage(new TextComponentString("Charges: " + te.getCharges() + "/" + ModConfig.maxCharges), true);
            return true;
        }

        // No charge item configured: first click charges to full for free
        if (chargeItem == null && charges == 0) {
            te.setCharges(ModConfig.maxCharges);
            world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.sendStatusMessage(new TextComponentString("Soul Anchor charged. Right click again to set your respawn."), true);
            return true;
        }

        // Uncharged and not holding the charge item -> hint
        if (charges == 0) {
            if (chargeItem != null) {
                player.sendStatusMessage(new TextComponentString("Charge with " + new ItemStack(chargeItem).getDisplayName() + "."), true);
            }
            return true;
        }

        // Using a charged anchor outside whitelisted dims -> boom (intentional game design)
        int dim = world.provider.getDimension();
        if (!ModConfig.isWhitelisted(dim)) {
            if (ModConfig.intentionalGameDesign) {
                world.setBlockToAir(pos);
                world.newExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0F, true, true);
            } else {
                player.sendStatusMessage(new TextComponentString("The Soul Anchor doesn't work here."), true);
            }
            return true;
        }

        // Anti-spam: already bound to this anchor
        NBTTagCompound data = SpawnUtil.getData(player);
        if (data.hasKey("soulanchor_pos")
                && data.getLong("soulanchor_pos") == pos.toLong()
                && data.getInteger("soulanchor_dim") == dim) {
            player.sendStatusMessage(new TextComponentString("Respawn already set here."), true);
            return true;
        }

        // Set spawn (free; charges are consumed on respawn)
        data.setInteger("soulanchor_dim", dim);
        data.setLong("soulanchor_pos", pos.toLong());
        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        player.sendStatusMessage(new TextComponentString("Respawn point set."), true);
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return true; }

    @Override
    public boolean isFullCube(IBlockState state) { return true; }
}
