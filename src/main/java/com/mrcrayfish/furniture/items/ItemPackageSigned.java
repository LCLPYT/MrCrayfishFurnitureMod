/**
 * MrCrayfish's Furniture Mod
 * Copyright (C) 2016  MrCrayfish (http://www.mrcrayfish.com/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mrcrayfish.furniture.items;

import com.mrcrayfish.furniture.MrCrayfishFurnitureMod;
import com.mrcrayfish.furniture.gui.inventory.InventoryPackage;
import com.mrcrayfish.furniture.init.FurnitureItems;
import com.mrcrayfish.furniture.tileentity.TileEntityMailBox;
import com.mrcrayfish.furniture.util.NBTHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPackageSigned extends Item implements IMail
{
	public ItemPackageSigned()
	{
		setMaxStackSize(1);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + I18n.format("item.mail.name");
	}

	@Override
	public boolean getShareTag()
	{
		return true;
	}

	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound nbttagcompound = stack.getTagCompound();
			NBTTagString nbttagstring = (NBTTagString) nbttagcompound.getTag("Author");

			if (nbttagstring != null)
			{
				tooltip.add(TextFormatting.GRAY + "from " + nbttagstring.getString());
			}
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntity tile_entity = world.getTileEntity(pos);
		if (!world.isRemote)
		{
			NBTTagList var2 = (NBTTagList) NBTHelper.getCompoundTag(heldItem, "Package").getTag("Items");
			if (var2.tagCount() > 0)
			{
				if (player.capabilities.isCreativeMode && player.isSneaking() && tile_entity instanceof TileEntityMailBox)
				{
					player.sendMessage(new TextComponentString("You cannot use this in creative."));
				}
				else if (tile_entity instanceof TileEntityMailBox)
				{
					TileEntityMailBox tileEntityMailBox = (TileEntityMailBox) tile_entity;
					if(player.isSneaking())
					{
						if (!tileEntityMailBox.isMailBoxFull())
						{
							ItemStack itemStack = heldItem.copy();
							tileEntityMailBox.addMail(itemStack);
							player.sendMessage(new TextComponentString("Thank you! - " + TextFormatting.YELLOW + tileEntityMailBox.getOwner()));
							heldItem.shrink(1);
						}
						else
						{
							player.sendMessage(new TextComponentString(TextFormatting.YELLOW + tileEntityMailBox.getOwner() + "'s" + TextFormatting.WHITE + " mail box seems to be full. Try again later."));
						}
					}
				}
			}
			else
			{
				player.sendMessage(new TextComponentString("You cannot insert a used package."));
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) 
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		if (!worldIn.isRemote)
		{
			if (this == FurnitureItems.itemPackageSigned)
			{
				playerIn.openGui(MrCrayfishFurnitureMod.instance, 8, worldIn, 0, 0, 0);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	public static IInventory getInv(EntityPlayer player)
	{
		ItemStack mail = player.inventory.getCurrentItem();
		InventoryPackage invMail = null;
		if (mail != null && mail.getItem() instanceof ItemPackageSigned)
		{
			invMail = new InventoryPackage(player, mail);
		}
		return invMail;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return true;
	}
}