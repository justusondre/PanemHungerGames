package game.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar {
	
   public static void sendActionBar(Player player, String message) {
       if (player == null || message == null) return;
       String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
       nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

       //1.10 and up
       if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
           player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
           return;
       }

       try {
           Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
           Object craftPlayer = craftPlayerClass.cast(player);

           Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
           Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
           Object packetPlayOutChat;
           Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
           Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

           Method method = null;
           if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

           Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
           packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

           Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
           Object iCraftPlayer = handle.invoke(craftPlayer);
           Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
           Object playerConnection = playerConnectionField.get(iCraftPlayer);
           Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
           sendPacket.invoke(playerConnection, packetPlayOutChat);
       } catch (Exception ex) {
           ex.printStackTrace();
       }
   }
}