package me.Snaxolas.Libabel.Util;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Text2Book {
    public static ItemStack[] stringToBooks(String s, String title){

        int authorIndex = s.indexOf("Author: ");
        String author = "";
        if(authorIndex == -1){
            authorIndex = s.indexOf("#author ");
        }
        if (authorIndex != -1){
            int newLineIndex = s.indexOf("\n", authorIndex);
            int returnIndex = s.indexOf("\r", authorIndex);

            if(returnIndex != -1 && newLineIndex != -1){
                author = s.substring(authorIndex + 8, Integer.min(newLineIndex, returnIndex)).trim();
            }else{
                author = s.substring(authorIndex + 8, (newLineIndex == -1) ? returnIndex : newLineIndex ).trim();
            }

        }


        // sanitize
        s = s.strip();
        s = s.replaceAll("\n", " ");
        s = s.replaceAll("\r", " ");
        s = s.replaceAll("\t", " ");
        s = s.replaceAll("\f", " ");
        for(int i = 0; i < 10; i++){
            s = s.replaceAll(" {2}", " ");
        }



        int volCount = (int)Math.ceil(s.length() / 12800.0);

        String[] titleArr = new String[volCount];

        if(volCount == 1){
            if(title.length() < 32){
                titleArr[0] = title;
            }else{
                titleArr[0] = title.substring(0, 32);
            }
        }else{
            for(int i = 1; i < volCount + 1; i++) {
                String t;
                if(title.length() + (" Vol. " + volCount).length() < 32) {
                    t = title;
                }else{
                    t = title.substring(0, 32 - (" Vol. " + volCount + 1).length());
                }
                t += " Vol. " + i;
                titleArr[i-1] = t;
            }
        }

        ItemStack[] itemStackArr  = new ItemStack[volCount];

        int charCount = 0;

        for(int i = 0; i < volCount && charCount <= s.length(); i++){
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta)book.getItemMeta();
            bm.setTitle(titleArr[i]);
            bm.setAuthor(author);
            for(int j = 0; j < 50; j++){
                if((charCount + 250) > s.length()){
                    bm.addPage(s.substring(charCount));
                    break;
                }else if(!Character.isWhitespace(s.charAt(249)) && Character.isWhitespace(s.charAt(250))){
                    bm.addPage(s.substring(charCount, charCount + 250));
                    charCount += 250;
                }else{
                    bm.addPage(s.substring(charCount, charCount + 250) + "-");
                    charCount += 250;
                }
            }
            book.setItemMeta(bm);
            itemStackArr[i] = book;
        }

        return itemStackArr;
    }
}
