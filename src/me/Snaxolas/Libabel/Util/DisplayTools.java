package me.Snaxolas.Libabel.Util;

import org.bukkit.Bukkit;

public class DisplayTools {

    //assumes each line is short enough not to wrap.
    public static String showLess(String full, int linesPer, int index){
        String toBuild = "";
        String[] broke = full.split("\n");
        int start = linesPer * index;

        for(int i = 0; i + start < broke.length && i < linesPer; i++){
            toBuild += broke[start + i];
            if (i != linesPer - 1) {
                toBuild += "\n";
            }
        }
        return toBuild;
    }
}
