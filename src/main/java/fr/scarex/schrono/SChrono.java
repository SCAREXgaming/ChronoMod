package fr.scarex.schrono;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import fr.scarex.schrono.proxy.SChronoCommonProxy;

@Mod(modid = SChrono.MODID, version = SChrono.VERSION, name = SChrono.NAME, clientSideOnly = true, canBeDeactivated = true, guiFactory = "fr.scarex.schrono.client.gui.GuiScreenConfigFactory")
public class SChrono
{
	public static final String MODID = "SChrono";
	public static final String VERSION = "1.3d";
	public static final String NAME = "SCAREX Chrono";
	public static final Logger log = LogManager.getLogger("SChrono");
	public static File chronoFolder;
	public static File chronoConfig;
	public static File modFolder;
	public SChronoHandler handler;

	public SChrono() {
		this.handler = new SChronoHandler();
	}

	public static boolean active = true;
	@Mod.Instance("SChrono")
	public static SChrono instance;
	@SidedProxy(clientSide = "fr.scarex.schrono.proxy.SChronoClientProxy", serverSide = "fr.scarex.schrono.client.gui.GuiScreenConfigFactory")
	public static SChronoCommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		modFolder = event.getSourceFile().getParentFile();
		chronoFolder = new File(event.getModConfigurationDirectory(), "Chrono");
		if ((!chronoFolder.exists()) && (!chronoFolder.mkdirs())) {
			log.error("Couldn't create the chrono folder");
		}
		chronoConfig = new File(chronoFolder, "config.chrono");
		try {
			if ((!chronoConfig.exists()) && (!chronoConfig.createNewFile())) {
				log.error("Couldn't create the chrono configuration file");
			}
			loadConfig();
		} catch (Throwable t) {
			log.error("Couldn't create the chrono configuration file", t);
		}
		MinecraftForge.EVENT_BUS.register(this.handler);
		FMLCommonHandler.instance().bus().register(this.handler);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerKeyBinds();
	}

	@Mod.EventHandler
	public void onServerStartingEvent(FMLServerStartingEvent event) {
		event.registerServerCommand(this.handler);
	}

	public void setEnabledState(boolean enabled) {
		active = enabled;
		log.info("setEnabledState triggered : " + active);
	}

	public String getVersionFileURL() {
		return "http://scarex.fr/SChrono/versions.json";
	}

	public static void loadConfig() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(chronoConfig));
			String[] s = br.readLine().split(" ");
			if (s != null) {
				if (s.length > 0) {
					SChronoHandler.scale = Float.parseFloat(s[0]);
				}
				if (s.length > 1) {
					SChronoHandler.xPos = Float.parseFloat(s[1]);
				}
				if (s.length > 2) {
					SChronoHandler.yPos = Float.parseFloat(s[2]);
				}
				if (s.length > 3) {
					SChronoHandler.colorRun = EnumChatFormatting.func_175744_a(Integer.parseInt(s[3]));
				}
				if (s.length > 4) {
					SChronoHandler.colorPause = EnumChatFormatting.func_175744_a(Integer.parseInt(s[4]));
				}
				if (s.length > 5) {
					SChronoHandler.isCentered = Boolean.parseBoolean(s[5]);
				}
			}
			br.close();
		} catch (Throwable t) {
			log.error("Couldn't load the chrono configuration file, if this is the first time you run this mod don't worry", t);
		}
	}

	public static void saveConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(chronoConfig));
			bw.write(SChronoHandler.scale + " " + SChronoHandler.xPos + " " + SChronoHandler.yPos + " " + (SChronoHandler.colorRun != null ? SChronoHandler.colorRun.getColorIndex() : 0) + " " + (SChronoHandler.colorPause != null ? SChronoHandler.colorPause.getColorIndex() : 0) + " " + SChronoHandler.isCentered);
			bw.close();
		} catch (Throwable t) {
			log.error("Couldn't save the chrono configuration file", t);
		}
	}

	public String getUpdaterVersion() {
		return "1.3c";
	}
}
