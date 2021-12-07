package me.Snaxolas.Libabel.Util;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TextFileTools {
    public static boolean downloadFromURL(File toPath, URL url){
        try {
            FilterInputStream ftis = (FilterInputStream) url.getContent();
            byte[] s = ftis.readAllBytes();
            FileOutputStream fos = new FileOutputStream(toPath);
            fos.write(s);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public static boolean writeStringToFile(File toPath, String str){
        try {
            byte[] s = str.getBytes(StandardCharsets.UTF_8);
            FileOutputStream fos = new FileOutputStream(toPath);
            fos.write(s);
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public static String getStringFromFile(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            String s = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            return s;
        }catch (FileNotFoundException e){
            return null;
        }catch (IOException e){
            return null;
        }
    }
}
