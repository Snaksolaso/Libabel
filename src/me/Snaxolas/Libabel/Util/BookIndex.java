package me.Snaxolas.Libabel.Util;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;

public class BookIndex {
    private File file;
    private JSONObject json; // org.json.simple
    private JSONParser parser = new JSONParser();

    public BookIndex(File file) {
        this.file = file;
        reload();
    }
    public void reIndex() {
        File dir = file.getParentFile();
        File[] books = dir.listFiles(pathname -> FilenameUtils.getExtension(pathname.getPath()) == "book");
    }
    public void reload() {
        try {
            if (!file.exists()) {
                PrintWriter pw = new PrintWriter(file, "UTF-8");
                pw.print("{");
                pw.print("}");
                pw.flush();
                pw.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reIndex();
    }


}
