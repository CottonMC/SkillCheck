package io.github.cottonmc.skillcheck.container;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.LegacyClassManager;
import io.github.cottonmc.skillcheck.api.classes.PlayerClassType;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class CharacterSheetScreen extends AbstractContainerScreen<CharacterSheetContainer> {
	private static final Identifier TEXTURE = new Identifier(SkillCheck.MOD_ID, "textures/gui/container/scribing.png");
	private int index;
	private final ButtonPageWidget[] visibleButtons = new ButtonPageWidget[7];
	private int scroll;
	private boolean needsScroll;
	private ConfirmButtonWidget confirm;

	public CharacterSheetScreen(int syncId, PlayerEntity player) {
		super(new CharacterSheetContainer(syncId, player), player.inventory, new TranslatableText("container.skillcheck.scribing_table"));
		this.containerWidth = 276;
		this.index = -1;
	}

	private void syncClassIndex() {
		this.container.setCurrentSkill(index);
		SkillCheckNetworking.syncSelection(index);
	}

	private void syncLevelUp() {
		SkillCheckNetworking.syncLevelup(container.classes.get(index), container.getLevelCost());
	}

	@Override
	protected void init() {
		super.init();
		int left = (this.width - this.containerWidth) / 2;
		int top = (this.height - this.containerHeight) / 2;
		int listHeight = top + 18;
		confirm = this.addButton(new ConfirmButtonWidget(left + 143, top + 140, new TranslatableText("btn.skillcheck.levelup"), (widget) -> {
			this.playerInventory.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
			this.syncLevelUp();
		}));
		for (int i = 0; i < 7; i++) {
			this.visibleButtons[i] = this.addButton(new ButtonPageWidget(left + 5, listHeight, i, (widget) -> {
				if (widget instanceof ButtonPageWidget) {
					this.index = ((ButtonPageWidget)widget).getIndex() + scroll;
					this.syncClassIndex();
				}
			}));
			listHeight += 20;
		}
	}

	@Override
	protected void drawBackground(float v, int i, int i1) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		int left = (this.width - this.containerWidth) / 2;
		int top = (this.height - this.containerHeight) / 2;
		blit(left, top, this.blitOffset, 0.0F, 0.0F, this.containerWidth, this.containerHeight, 256, 512);
	}

	@Override
	public void render(int x, int y, float partialTicks) {
		this.renderBackground();
		super.render(x, y, partialTicks);
		confirm.active = container.canLevelUp();
		List<Identifier> classes = this.container.classes;
		TextRenderer textRenderer = this.minecraft.textRenderer;
		if (!classes.isEmpty()) {
			int left = (this.width - this.containerWidth) / 2;
			int top = (this.height - this.containerHeight) / 2;
			int drawHeight = top + 17;
			int listLeft = left + 10;
			int scrollOffset = 0;
			int rightPanelCenter = left + 187;
			GlStateManager.disableLighting();
			GlStateManager.disableBlend();
			for (Identifier id : classes) {
				TranslatableText className = new TranslatableText("class." + id.getNamespace() + "." + id.getPath());
				String level = className.asString() + ": " + new TranslatableText("text.skillcheck.level", LegacyClassManager.getLevel(playerInventory.player, id)).asString();
				if (shouldScroll(classes.size()) && (scrollOffset < this.scroll || scrollOffset >= 7 + this.scroll)) {
					scrollOffset++;
				} else {
					int renderHeight = drawHeight + 6;
					this.drawString(textRenderer, level, listLeft, renderHeight, 0xffffff);
					drawHeight += 20;
					scrollOffset++;
				}
			}
			if (index >= 0) {
				Identifier id = classes.get(index);
				PlayerClassType pClass = SkillCheck.PLAYER_CLASS_TYPES.get(id);
				int descLineHeight = top + 20;
				List<String> lines = new ArrayList<>();
				for (Text line : pClass.getClassDescription()) {
					String textToDraw = line.asString();
					List<String> toAdd = textRenderer.wrapStringToWidthAsList(textToDraw, 161);
					lines.addAll(toAdd);
				}
				for (String line : lines) {
					this.drawCenteredString(textRenderer, line, rightPanelCenter, descLineHeight, 0xffffff);
					descLineHeight += 10;
				}
				String cost = new TranslatableText("text.skillcheck.cost", this.container.getLevelCost()).asString();
				this.drawCenteredString(textRenderer, cost, rightPanelCenter, descLineHeight, 0x55ff55);
			}
			GlStateManager.enableLighting();
			GlStateManager.enableBlend();

			for (ButtonPageWidget button : this.visibleButtons) {
				if (button.isHovered()) {
					button.renderToolTip(x, y);
				}
				button.visible = button.index < this.container.classes.size();
				button.active = button.index != this.index;
			}
			GuiLighting.disable();
		}
	}

	private boolean shouldScroll(int size) {
		return size > 7;
	}

	@Override
	public boolean mouseScrolled(double double_1, double double_2, double double_3) {
		int amount = this.container.classes.size();
		if (shouldScroll(amount)) {
			int remaining = amount - 7;
			this.scroll = (int) ((double) this.scroll - double_3);
			this.scroll = MathHelper.clamp(this.scroll, 0, remaining);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
		int amount = container.classes.size();
		if (this.needsScroll) {
			int listTop = this.top + 18;
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
	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		this.needsScroll = false;
		int int_2 = (this.width - this.containerWidth) / 2;
		int int_3 = (this.height - this.containerHeight) / 2;
		if (this.shouldScroll(container.classes.size()) && double_1 > (double)(int_2 + 94) && double_1 < (double)(int_2 + 94 + 6) && double_2 > (double)(int_3 + 18) && double_2 <= (double)(int_3 + 18 + 139 + 1)) {
			this.needsScroll = true;
		}

		return super.mouseClicked(double_1, double_2, int_1);
	}

	@Environment(EnvType.CLIENT)
	class ButtonPageWidget extends ButtonWidget {
		final int index;

		public ButtonPageWidget(int x, int y, int index, PressAction action) {
			super(x, y, 89, 20, "", action);
			this.index = index;
			this.visible = false;
		}

		public int getIndex() {
			return this.index;
		}

		@Override
		public void renderToolTip(int int_1, int int_2) {

		}
	}

	@Environment(EnvType.CLIENT)
	class ConfirmButtonWidget extends ButtonWidget {
		public ConfirmButtonWidget(int x, int y, TranslatableText name, PressAction action) {
			super(x, y, 89, 20, name.asString(), action);
		}
	}
}
