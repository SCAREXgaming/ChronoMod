package fr.scarex.schrono.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class SChronoClientProxy
  extends SChronoCommonProxy
{
  public static final KeyBinding chronoPlayKey = new KeyBinding("schrono.key.playKey", Keyboard.KEY_P, "schrono.key.categories.chrono");
  public static final KeyBinding chronoShowKey = new KeyBinding("schrono.key.showKey", Keyboard.KEY_B, "schrono.key.categories.chrono");
  
  public void registerKeyBinds()
  {
    ClientRegistry.registerKeyBinding(chronoPlayKey);
    ClientRegistry.registerKeyBinding(chronoShowKey);
  }
}
