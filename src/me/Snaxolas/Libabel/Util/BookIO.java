package me.Snaxolas.Libabel.Util;

import com.google.gson.JsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BookIO {

    private static String bookFolder = Bukkit.getPluginManager().getPlugin("Libabel").getDataFolder().getPath() + File.separator + "Books";


    public static boolean checkDuplicate(BookMeta book) {
        return false;
    }

    public static BookMeta pull(String scope, String title){
        return null;
    }

    public static void publish(BookMeta book) {
        saveObject(bookFolder + File.separator + book.hashCode() + ".book", book);

    }

    public static boolean saveObject(String filePath, Object hm) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(hm);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static Object loadObject(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            Object data = in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
