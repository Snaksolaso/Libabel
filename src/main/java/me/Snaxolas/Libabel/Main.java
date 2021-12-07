package me.Snaxolas.Libabel;
import me.Snaxolas.Libabel.Commands.Libabel;
import me.Snaxolas.Libabel.Util.BookIndex;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.HashMap;

public class Main extends JavaPlugin{
    BookIndex index;

    private void onFirstRun(){
        Bukkit.getScheduler().runTaskLater(this, () -> index.reIndex(), 5);
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
        index = new BookIndex(new File(this.getDataFolder().getPath() + File.separator + "Books" + File.separator + "bookIndex.json"));

        this.getCommand("libabel").setExecutor(new Libabel(index));
        this.getCommand("lbb").setExecutor(new Libabel(index));
    }
}
