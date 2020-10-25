package io.github.cottonmc.skillcheck.container;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClass;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.impl.SkillCheckCharacterClass;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class CharacterSheetScreen extends HandledScreen<CharacterSheetContainer> {
	private static final Identifier TEXTURE = new Identifier(SkillCheck.MOD_ID, "textures/gui/container/scribing.png");
	private int index;
	private final ButtonPageWidget[] visibleButtons = new ButtonPageWidget[7];
	private int scroll;
	private boolean needsScroll;
	private ConfirmButtonWidget confirm;

	public CharacterSheetScreen(int syncId, PlayerEntity player) {
		super(new CharacterSheetContainer(syncId, player), player.inventory, new TranslatableText("item.skillcheck.character_sheet"));
		this.backgroundWidth = 276;
		this.index = -1;
	}

	private void syncClassIndex() {
		this.handler.setCurrentSkill(index);
		SkillCheckNetworking.syncSelection(index);
	}

	private void syncLevelUp() {
		SkillCheckNetworking.syncLevelup(handler.classes.get(index).getId());
	}

	@Override
	protected void init() {
		super.init();
		int left = (this.width - this.backgroundWidth) / 2;
		int top = (this.height - this.backgroundHeight) / 2;
		int listHeight = top + 18;
		confirm = this.addButton(new ConfirmButtonWidget(left + 143, top + 140, new TranslatableText("btn.skillcheck.levelup"), (widget) -> {
			this.playerInventory.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
			this.syncLevelUp();
		}));
		for (int i = 0; i < 7; i++) {
			this.visibleButtons[i] = this.addButton(new ButtonPageWidget(left + 5, listHeight, i, (widget) -> {
				if (widget instanceof ButtonPageWidget) {
					this.index = ((ButtonPageWidget) widget).getIndex() + scroll;
					this.syncClassIndex();
				}
			}));
			listHeight += 20;
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float v, int i, int i1) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int left = (this.width - this.backgroundWidth) / 2;
		int top = (this.height - this.backgroundHeight) / 2;
		drawTexture(matrices, left, top, this.getZOffset(), 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 512);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float partialTicks) {
		this.renderBackground(matrices);
		super.render(matrices, x, y, partialTicks);
		confirm.active = handler.canLevelUp();
		List<CharacterClass> classes = this.handler.classes;
		TextRenderer textRenderer = this.client.textRenderer;
		if (!classes.isEmpty()) {
			int left = (this.width - this.backgroundWidth) / 2;
			int top = (this.height - this.backgroundHeight) / 2;
			int drawHeight = top + 17;
			int listLeft = left + 10;
			int scrollOffset = 0;
			int rightPanelCenter = left + 187;
			int maxDescLineHeight = top + 120;
			RenderSystem.disableLighting();
			RenderSystem.disableBlend();
			CharacterClasses pClasses = CharacterData.get(playerInventory.player).getClasses();
			for (CharacterClass clazz : classes) {
				int levelVal;
				if (pClasses.has(clazz)) levelVal = pClasses.get(clazz).getLevel();
				else levelVal = 0;
				Text level = new TranslatableText("text.skillcheck.level", clazz.getName(), levelVal);
				if (!shouldScroll(classes.size()) || (scrollOffset >= this.scroll && scrollOffset < 7 + this.scroll)) {
					int renderHeight = drawHeight + 6;
					textRenderer.draw(matrices, level, listLeft, renderHeight, 0xffffff);
					drawHeight += 20;
				}
				scrollOffset++;
			}
			if (index >= 0) {
				CharacterClass pClass = classes.get(index);
				int descLineHeight = top + 20;
				List<OrderedText> lines = new ArrayList<>();
				for (Text line : pClass.getDescription()) {
					lines.addAll(textRenderer.wrapLines(line, 161));
				}
				for (OrderedText line : lines) {
					// drawCenteredText does not exist for OrderedText, this is the inlined implementation
					textRenderer.drawWithShadow(matrices, line, rightPanelCenter - 80, descLineHeight, 0xffffff);
					descLineHeight += 10;
				}

				drawCenteredText(matrices, textRenderer, ((SkillCheckCharacterClass)pClass).getLevelRequirement(pClasses.has(pClass)? pClasses.get(pClass).getLevel() : 0, playerInventory.player), rightPanelCenter, maxDescLineHeight, 0x55ff55);
			}
			RenderSystem.enableLighting();
			RenderSystem.enableBlend();

			for (ButtonPageWidget button : this.visibleButtons) {
				if (button.isHovered()) {
					button.renderToolTip(matrices, x, y);
				}
				button.visible = button.index < this.handler.classes.size();
				button.active = button.index != this.index;
			}
			DiffuseLighting.disable();
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		this.textRenderer.draw(matrices, this.title, this.titleX, this.titleY, 0x404040);
	}

	private boolean shouldScroll(int size) {
		return size > 7;
	}

	@Override
	public boolean mouseScrolled(double double_1, double double_2, double double_3) {
		int amount = this.handler.classes.size();
		if (shouldScroll(amount)) {
			int remaining = amount - 7;
			this.scroll = (int) ((double) this.scroll - double_3);
			this.scroll = MathHelper.clamp(this.scroll, 0, remaining);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
		int amount = handler.classes.size();
		if (this.needsScroll) {
			int listTop = this.y + 18;
			int listBottom = listTop + 139;
			int offscreen = amount - 7;
			float scrollAmount = ((float)double_2 - (float)listTop - 13.5F) / ((float)(listBottom - listTop) - 27.0F);
			scrollAmount = scrollAmount * (float)offscreen + 0.5F;
			this.scroll = MathHelper.clamp((int)scrollAmount, 0, offscreen);
			return true;
		} else {
			return super.mouseDragged(double_1, double_2, int_1, double_3, double_4);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.needsScroll = false;
		int left = (this.width - this.backgroundWidth) / 2;
		int top = (this.height - this.backgroundHeight) / 2;
		if (this.shouldScroll(handler.classes.size()) && mouseX > (double)(left + 94) && mouseX < (double)(left + 94 + 6) && mouseY > (double)(top + 18) && mouseY <= (double)(top + 18 + 139 + 1)) {
			this.needsScroll = true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Environment(EnvType.CLIENT)
	static class ButtonPageWidget extends ButtonWidget {
		final int index;

		public ButtonPageWidget(int x, int y, int index, PressAction action) {
			super(x, y, 89, 20, LiteralText.EMPTY, action);
			this.index = index;
			this.visible = false;
		}

		public int getIndex() {
			return this.index;
		}

		@Override
		public void renderToolTip(MatrixStack matrices, int int_1, int int_2) {

		}
	}

	@Environment(EnvType.CLIENT)
	static class ConfirmButtonWidget extends ButtonWidget {
		public ConfirmButtonWidget(int x, int y, TranslatableText name, PressAction action) {
			super(x, y, 89, 20, name, action);
		}
	}
}
