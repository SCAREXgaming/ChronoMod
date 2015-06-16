package fr.scarex.schrono.client.gui;

import java.io.IOException;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.IModGuiFactory;

import org.lwjgl.input.Keyboard;

import fr.scarex.schrono.SChrono;
import fr.scarex.schrono.SChronoHandler;

public class GuiScreenConfigFactory implements IModGuiFactory
{
	public void initialize(Minecraft minecraftInstance) {}

	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiScreenConfig.class;
	}

	public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class GuiScreenConfig extends GuiScreen
	{
		protected GuiTextField textFieldColorRunning;
		protected GuiTextField textFieldColorPause;
		protected GuiButton backButton;
		protected GuiButton centerButton;
		protected int counter = 0;
		protected GuiScreen prevGui;
		protected boolean renderChrono;

		public GuiScreenConfig(GuiScreen g) {
			this.prevGui = g;
			this.renderChrono = true;
		}

		public GuiScreenConfig(GuiScreen gui, boolean b) {
			this.prevGui = gui;
			this.renderChrono = b;
		}

		public void initGui() {
			this.buttonList.clear();
			Keyboard.enableRepeatEvents(true);
			this.buttonList.add(this.backButton = new GuiButton(0, this.width / 2 - 100, this.height / 2 + 40, 200, 20, I18n.format("schrono.button.config.back", new Object[0])));

			this.buttonList.add(new GuiButton(1, this.width / 2 - 34, this.height / 2 - 10, 30, 20, "-"));
			this.buttonList.add(new GuiButton(2, this.width / 2 + 6, this.height / 2 - 10, 30, 20, "+"));
			this.buttonList.add(new GuiButton(3, this.width / 2 - 15, this.height / 2 - 32, 30, 20, "+"));
			this.buttonList.add(new GuiButton(4, this.width / 2 - 15, this.height / 2 + 12, 30, 20, "-"));

			this.buttonList.add(new GuiButton(5, this.width / 2 - 84, this.height / 2 + 64, 80, 20, "-"));
			this.buttonList.add(new GuiButton(6, this.width / 2 + 4, this.height / 2 + 64, 80, 20, "+"));

			this.buttonList.add(this.centerButton = new GuiButton(7, this.width / 2 - 100, this.height / 2 + 100, 200, 20, I18n.format("schrono.button.config.center", new Object[] { SChronoHandler.isCentered ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED })));

			this.buttonList.add(new GuiButton(8, this.width / 2 - 80, this.height / 2 + 140, 160, 20, I18n.format("schrono.button.config.reset", new Object[0])));

			this.textFieldColorRunning = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 126, 80, 20);
			this.textFieldColorRunning.setMaxStringLength(20);
			this.textFieldColorRunning.setFocused(true);
			this.textFieldColorRunning.setText(SChronoHandler.colorRun != null ? SChronoHandler.colorRun.getFriendlyName() : "white");
			this.textFieldColorPause = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 76, 80, 20);
			this.textFieldColorPause.setMaxStringLength(20);
			this.textFieldColorPause.setText(SChronoHandler.colorPause != null ? SChronoHandler.colorPause.getFriendlyName() : "white");
		}

		public void onGuiClosed() {
			Keyboard.enableRepeatEvents(false);
		}

		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			if (this.renderChrono) {
				drawDefaultBackground();
			}
			this.textFieldColorRunning.drawTextBox();
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.width / 2 - 100, this.height / 2 - 140, 0.0F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			this.fontRendererObj.drawString(I18n.format("schrono.text.config.running", new Object[] { SChronoHandler.colorRun != null ? SChronoHandler.colorRun : EnumChatFormatting.WHITE }), 0.0F, 0.0F, 16777215, false);
			GlStateManager.popMatrix();

			this.textFieldColorPause.drawTextBox();
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.width / 2 - 100, this.height / 2 - 90, 0.0F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			this.fontRendererObj.drawString(I18n.format("schrono.text.config.paused", new Object[] { SChronoHandler.colorPause != null ? SChronoHandler.colorPause : EnumChatFormatting.WHITE }), 0.0F, 0.0F, 16777215, false);
			GlStateManager.popMatrix();
			this.centerButton.displayString = I18n.format("schrono.button.config.center", new Object[] { SChronoHandler.isCentered ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED });
			super.drawScreen(mouseX, mouseY, partialTicks);
			if (this.renderChrono) {
				GlStateManager.pushMatrix();
				String s = "00:00:00";
				if (SChronoHandler.isCentered) {
					GlStateManager.translate((SChronoHandler.xPos - Minecraft.getMinecraft().fontRendererObj.getStringWidth(s) * SChronoHandler.scale) / 2.0F, SChronoHandler.yPos, 0.0F);
				} else {
					GlStateManager.translate(SChronoHandler.xPos, SChronoHandler.yPos, 0.0F);
				}
				GlStateManager.scale(SChronoHandler.scale, SChronoHandler.scale, 0.0F);
				Minecraft.getMinecraft().fontRendererObj.drawString(s, 0.0F, 0.0F, 16777215, false);
				GlStateManager.popMatrix();
			}
		}

		public void updateScreen() {
			this.counter += 1;
			this.textFieldColorRunning.updateCursorCounter();
			this.textFieldColorPause.updateCursorCounter();
		}

		protected void actionPerformed(GuiButton button) throws IOException {
			switch (button.id) {
			case 0:
				SChrono.saveConfig();
				this.mc.displayGuiScreen(this.prevGui);
				break;
			case 1:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.xPos -= 200.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.xPos -= 50.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.xPos -= 0.2F;
				} else {
					SChronoHandler.xPos -= 10.0F;
				}
				break;
			case 2:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.xPos += 200.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.xPos += 50.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.xPos += 0.2F;
				} else {
					SChronoHandler.xPos += 10.0F;
				}
				break;
			case 3:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.yPos -= 200.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.yPos -= 50.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.yPos -= 0.2F;
				} else {
					SChronoHandler.yPos -= 10.0F;
				}
				break;
			case 4:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.yPos += 200.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.yPos += 50.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.yPos += 0.2F;
				} else {
					SChronoHandler.yPos += 10.0F;
				}
				break;
			case 5:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.scale -= 4.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.scale -= 1.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.scale -= 0.1F;
				} else {
					SChronoHandler.scale -= 0.5F;
				}
				break;
			case 6:
				if ((isCtrlKeyDown()) && (isShiftKeyDown())) {
					SChronoHandler.scale += 4.0F;
				} else if (isShiftKeyDown()) {
					SChronoHandler.scale += 1.0F;
				} else if (isCtrlKeyDown()) {
					SChronoHandler.scale += 0.1F;
				} else {
					SChronoHandler.scale += 0.5F;
				}
				break;
			case 7:
				SChronoHandler.isCentered = !SChronoHandler.isCentered;
				if (SChronoHandler.isCentered) {
					SChronoHandler.xPos = this.width;
				} else {
					SChronoHandler.xPos = this.width / 2;
				}
				break;
			case 8:
				SChronoHandler.scale = 1.4F;
				SChronoHandler.xPos = 4.0F;
				SChronoHandler.yPos = 8.0F;
				SChronoHandler.colorRun = EnumChatFormatting.DARK_GREEN;
				SChronoHandler.colorPause = EnumChatFormatting.DARK_RED;
				SChronoHandler.isCentered = false;
			}
		}

		protected void keyTyped(char typedChar, int keyCode) throws IOException {
			this.textFieldColorRunning.textboxKeyTyped(typedChar, keyCode);
			this.textFieldColorPause.textboxKeyTyped(typedChar, keyCode);
			if ((keyCode != 28) && (keyCode != 156) && (keyCode != 1)) {
				SChronoHandler.colorRun = EnumChatFormatting.getValueByName(this.textFieldColorRunning.getText());
				SChronoHandler.colorPause = EnumChatFormatting.getValueByName(this.textFieldColorPause.getText());
			} else {
				actionPerformed(this.backButton);
			}
		}

		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			super.mouseClicked(mouseX, mouseY, mouseButton);
			this.textFieldColorRunning.mouseClicked(mouseX, mouseY, mouseButton);
			this.textFieldColorPause.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
}
