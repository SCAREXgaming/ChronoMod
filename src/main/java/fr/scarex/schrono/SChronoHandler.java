package fr.scarex.schrono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fr.scarex.schrono.client.gui.GuiScreenConfigFactory;
import fr.scarex.schrono.proxy.SChronoClientProxy;

public class SChronoHandler extends CommandBase
{
	public static boolean canrun = false;
	public static boolean show = true;
	public static byte ticks = 0;
	public static byte seconds = 0;
	public static byte minutes = 0;
	public static int hours = 0;
	public static float scale = 1.4F;
	public static float xPos = 4.0F;
	public static float yPos = 8.0F;
	public static EnumChatFormatting colorRun = EnumChatFormatting.DARK_GREEN;
	public static EnumChatFormatting colorPause = EnumChatFormatting.DARK_RED;
	public static boolean isCentered = false;

	@SubscribeEvent
	public void onServerTickEvent(TickEvent.ServerTickEvent event) {
		if ((SChrono.active) && (event.phase == TickEvent.Phase.START) && (canrun)) {
			if ((SChronoHandler.ticks = (byte) (ticks + 1)) % 20 == 0) {
				ticks = 0;
				seconds = (byte) (seconds + 1);
				writeChrono(SChrono.chronoFolder, Minecraft.getMinecraft().getIntegratedServer().getFolderName());
			}
			if (seconds >= 60) {
				seconds = 0;
				minutes = (byte) (minutes + 1);
			}
			if (minutes >= 60) {
				minutes = 0;
				hours += 1;
			}
		}
	}

	@SubscribeEvent
	public void onRenderGame(RenderGameOverlayEvent.Post event) {
		if ((SChrono.active) && (show) && (event.type == RenderGameOverlayEvent.ElementType.TEXT)) {
			GlStateManager.pushMatrix();
			String s = String.format("%s%02d:%02d:%02d%s", new Object[] {
					canrun ? (colorRun != null ? colorRun : EnumChatFormatting.WHITE) : (colorPause != null ? colorPause : EnumChatFormatting.WHITE),
					Integer.valueOf(hours),
					Byte.valueOf(minutes),
					Byte.valueOf(seconds),
					EnumChatFormatting.RESET });
			if (isCentered) {
				GlStateManager.translate((xPos - Minecraft.getMinecraft().fontRendererObj.getStringWidth(s) * scale) / 2.0F, yPos, 0.0F);
			} else {
				GlStateManager.translate(xPos, yPos, 0.0F);
			}
			GlStateManager.scale(scale, scale, 0.0F);
			Minecraft.getMinecraft().fontRendererObj.drawString(s, 0.0F, 0.0F, 16777215, false);
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void onInitGuiEvent(final GuiScreenEvent.InitGuiEvent.Post event) {
		if ((SChrono.active) && ((event.gui instanceof GuiIngameMenu))) {
			event.buttonList.add(new GuiButton(22, event.gui.width / 2 - 100, event.gui.height / 4 + 146, 200, 20, I18n.format("schrono.button.config", new Object[0])) {
				public void mouseReleased(int mouseX, int mouseY) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiScreenConfigFactory.GuiScreenConfig(event.gui, false));
				}
			});
		}
	}

	@SubscribeEvent
	public void onWorldLoadEvent(WorldEvent.Load event) {
		if (SChrono.active) {
			loadChrono(SChrono.chronoFolder, Minecraft.getMinecraft().getIntegratedServer().getFolderName());
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyPressEvent(InputEvent.KeyInputEvent event) {
		if (SChrono.active) {
			if (SChronoClientProxy.chronoPlayKey.isPressed()) {
				canrun = !canrun;
			}
			if (SChronoClientProxy.chronoShowKey.isPressed()) {
				show = !show;
			}
		}
	}

	public String getName() {
		return "chrono";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "schrono.command.chrono.usage";
	}

	public void execute(ICommandSender sender, String[] args) throws CommandException {
		if ((SChrono.active) && (args.length > 0)) {
			switch (args[0].toLowerCase()) {
			case "start":
				canrun = true;
				break;
			case "pause":
				canrun = false;
				break;
			case "switch":
				if (args.length > 1) {
					hours = Integer.parseInt(args[1]);
				}
				if (args.length > 2) {
					minutes = Byte.parseByte(args[2]);
				}
				if (args.length > 3) {
					seconds = Byte.parseByte(args[3]);
				}
				break;
			case "reset":
				hours = 0;
				minutes = 0;
				seconds = 0;
				break;
			}
		}
	}

	public boolean canCommandSenderUse(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) { return getListOfStringsMatchingLastWord(args, new String[] {
				"start", "pause",
				"reset", "switch" }); }
		if (args[0].equalsIgnoreCase("switch")) { return args.length == 3 ? Arrays.asList(new Byte[] { Byte.valueOf(minutes) }) : args.length == 2 ? Arrays.asList(new Integer[] { Integer.valueOf(hours) }) : Arrays.asList(new Byte[] { Byte.valueOf(seconds) }); }
		return null;
	}

	public static void loadChrono(File f, String name) {
		try {
			File f1 = new File(f, name + ".chrono");
			if (f1.getParentFile() != null) {
				f1.getParentFile().mkdirs();
			}
			if ((!f1.exists()) && (!f1.createNewFile())) {
				SChrono.log.error("couldn't create chrono file");
			}
			BufferedReader br = new BufferedReader(new FileReader(f1));
			String s;
			hours = Integer.parseInt((s = br.readLine()) != null ? s : "0");
			minutes = Byte.parseByte((s = br.readLine()) != null ? s : "0");
			seconds = Byte.parseByte((s = br.readLine()) != null ? s : "0");
			br.close();
		} catch (Throwable t) {
			SChrono.log.error("Couldn't load chrono file", t);
		}
	}

	public static void writeChrono(File f, String name) {
		try {
			File f1 = new File(f, name + ".chrono");
			if (f1.getParentFile() != null) {
				f1.getParentFile().mkdirs();
			}
			if ((!f1.exists()) && (!f1.createNewFile())) {
				SChrono.log.error("couldn't create chrono file");
			}
			PrintWriter w = new PrintWriter(f1);
			w.print("");
			w.println(hours);
			w.println(minutes);
			w.println(seconds);
			w.close();
		} catch (Throwable t) {
			SChrono.log.error("Couldn't write chrono file", t);
		}
	}
}
