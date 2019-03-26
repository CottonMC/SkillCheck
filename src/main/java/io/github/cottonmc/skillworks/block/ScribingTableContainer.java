package io.github.cottonmc.skillworks.block;

import io.github.cottonmc.skillworks.traits.ClassManager;
import net.minecraft.block.BlockState;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;

public class ScribingTableContainer extends Container {
	private final PlayerEntity player;
	private final Inventory inv;
	private final Inventory result;
	private Identifier currentSkill;
	private BlockContext context;

	protected ScribingTableContainer(int syncId, PlayerInventory playerInv) {
		this(syncId, playerInv, BlockContext.EMPTY);
	}

	public ScribingTableContainer(int syncId, PlayerInventory playerInv, BlockContext context) {
		super(null, syncId);
		this.player = playerInv.player;
		this.inv = new BasicInventory(1);
		this.result = new CraftingResultInventory();
		this.context = context;
		this.addSlot(new Slot(this.inv, 0, 27, 47));
		this.addSlot(new Slot(this.result, 2, 134, 47) {
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			public boolean canTakeItems(PlayerEntity player) {
				return (player.abilities.creativeMode || player.experience >= ScribingTableContainer.this.getLevelCost()) && this.hasStack();
			}

			public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
				if (!player.abilities.creativeMode) {
					player.method_7316(-ScribingTableContainer.this.getLevelCost());
				}

				ScribingTableContainer.this.inv.getInvStack(0).subtractAmount(1);
				return stack;
			}
		});

		//player inventory
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		//player hotbar
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	private int getLevelCost() {
		if (currentSkill == null) return 0;
		int level = ClassManager.getLevel(player, currentSkill);
		if (level > 3) return 30;
		return 10*level;
	}

	public ItemStack transferSlot(PlayerEntity player, int slotId) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slotList.get(slotId);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotId == 1) {
				if (!this.insertItem(slotStack, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(slotStack, stack);
			} else if (slotId != 0) {
				if (slotId < 38 && !this.insertItem(slotStack, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(slotStack, 2, 38, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (slotStack.getAmount() == stack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, slotStack);
		}

		return stack;
	}

	public void close(PlayerEntity player) {
		super.close(player);
		this.context.run((world, pos) -> {
			this.dropInventory(player, world, this.inv);
		});
	}
}
