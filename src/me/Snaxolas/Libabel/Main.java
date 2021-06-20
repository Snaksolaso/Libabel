package me.Snaxolas.Libabel;
import me.Snaxolas.Libabel.Commands.Libabel;
import me.Snaxolas.Libabel.Util.BookIndex;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class Main extends JavaPlugin{
    private void onFirstRun(){

    }

    @Override
    public void onEnable(){
        File bookFolder = new File(this.getDataFolder().getPath() + File.separator + "Books");
        if(!bookFolder.exists())
            bookFolder.mkdirs();
        registerCmds();
    }

    @Override
    public void onDisable(){
    }

    public void registerCmds(){

        BookIndex index = new BookIndex(new File(this.getDataFolder().getPath() + File.separator + "Books" + File.separator + "bookIndex.json"));

        this.getCommand("libabel").setExecutor(new Libabel(index));
        this.getCommand("lbb").setExecutor(new Libabel(index));
    }
}
