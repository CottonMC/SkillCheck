package io.github.cottonmc.skillworks.block;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ScribingTableScreen extends ContainerScreen<ScribingTableContainer> {
	private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
	private int index;
	private final ButtonPageWidget[] visibleButtons = new ButtonPageWidget[7];
	private int scroll;
	private boolean needsScroll;
	private ConfirmButtonWidget confirm;

	public ScribingTableScreen(ScribingTableContainer container, PlayerInventory inv, TextComponent name) {
		super(container, inv, name);
		this.containerWidth = 276;
	}

	private void syncClassIndex() {
		this.container.setCurrentSkill(index);
	}

	private void syncLevelUp() {

	}

	@Override
	protected void init() {
		super.init();
		int left = (this.width - this.containerWidth) / 2;
		int top = (this.height - this.containerHeight) / 2;
		int listHeight = top + 18;
		confirm = this.addButton(new ConfirmButtonWidget(left + 190, top + 100, (widget) -> {
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
		if (!classes.isEmpty()) {

		}
	}

	private boolean shouldScroll(int size) {
		return size > 7;
	}

	public boolean mouseScrolled(double double_1, double double_2, double double_3) {
		int amount = this.container.classes.size();
		if (shouldScroll(amount)) {
			int remaining = amount - 7;
			this.scroll = (int) ((double) this.scroll - double_3);
			this.scroll = MathHelper.clamp(this.scroll, 0, remaining);
		}
		return true;
	}

	public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
		int amount = container.classes.size();
		if (this.needsScroll) {
			int listTop = this.top + 18;
			int listBottom = listTop + 139;
			int offscreen = amount - 7;
			float float_1 = ((float)double_2 - (float)listTop - 13.5F) / ((float)(listBottom - listTop) - 27.0F);
			float_1 = float_1 * (float)offscreen + 0.5F;
			this.scroll = MathHelper.clamp((int)float_1, 0, offscreen);
			return true;
		} else {
			return super.mouseDragged(double_1, double_2, int_1, double_3, double_4);
		}
	}

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

		public void renderToolTip(int int_1, int int_2) {

		}
	}

	@Environment(EnvType.CLIENT)
	class ConfirmButtonWidget extends ButtonWidget {
		public ConfirmButtonWidget(int x, int y, PressAction action) {
			super(x, y, 89, 20, "", action);
		}
	}
}
