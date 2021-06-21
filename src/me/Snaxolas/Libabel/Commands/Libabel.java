package me.Snaxolas.Libabel.Commands;

import me.Snaxolas.Libabel.Util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import javax.print.DocFlavor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

public class Libabel implements CommandExecutor {


    BookIndex index;

    public Libabel(BookIndex bi){
        index = bi;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("Libabel") || label.equalsIgnoreCase("lbb")){
            switch(args.length){
                case 0:
                    sender.sendMessage(ChatColor.GOLD + "----------------------------------------\n" +
                            "Libabel is a written book repository, through which you can acquire written books which have been published by other users.\n" +
                            "----------------------------------------\n" +
                            ChatColor.RED + "Commands: \n" +
                            ChatColor.GREEN + "    /lbb search [terms]\n" +
                            ChatColor.DARK_AQUA + "Searches the authors and titles of books for the terms and returns an indexed list of the matching books.\n " +
                            "Returns all books if no terms are provided.\n" +
                            ChatColor.GREEN + "    /lbb get <index>\n" +
                            ChatColor.DARK_AQUA + "Gets the book in the repository at that index.\n" +
                            ChatColor.GREEN +"    /lbb publish\n" +
                            ChatColor.DARK_AQUA + "Publishes the book in your hand to the repository.\n" +
                            ChatColor.GREEN +"    /lbb geturl [-h] [-cpp <chars per page>] <plaintext file URL> <book title>\n"+
                            ChatColor.DARK_AQUA + "Retrieves the plaintext file at the provided URL as a book with the provided title.\n" +
                            ChatColor.GOLD + "----------------------------------------");
                    return true;
                case 1:
                    switch(args[0].toLowerCase()){

                        case "get":
                            sender.sendMessage(ChatColor.RED + "Please include the index of the book you wish to pull.");
                            return true;

                        case "publish":
                            if(sender instanceof Player){
                                Player p = (Player) sender;
                                PlayerInventory inv = p.getInventory();
                                if(inv.getItemInMainHand().getType().equals(Material.WRITTEN_BOOK)){
                                    BookMeta book = (BookMeta)inv.getItemInMainHand().getItemMeta();
                                    if (!BookIO.checkDuplicate(book)){
                                        BookIO.publish(book);
                                        sender.sendMessage(ChatColor.GREEN + "Book successfully published!");
                                        return true;
                                    }else{
                                        sender.sendMessage(ChatColor.RED + "This book has already been published!");
                                        return false;
                                    }
                                }
                            }else {
                                sender.sendMessage("You must be a player to publish books.");
                                return false;
                            }

                        case "search":
                            sender.sendMessage(ChatColor.RED + "Please include at least 1 search term.");
                            return false;

                        default:
                            sender.sendMessage(ChatColor.RED + "Invalid arguments!");
                            return false;

                        case "geturl":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb [-h] [-cpp <chars per page>] <plaintext file URL> <book title>");
                            return true;
                    }
                case 2:
                    switch(args[0].toLowerCase()){
                        case "geturl":
                            sender.sendMessage(ChatColor.RED + "You must include a title for the book!");
                            return true;
                    }
                default:
                    switch(args[0].toLowerCase()){
                        case "geturl":
                            if (sender instanceof Player) {
                                Player p = ((Player) sender);

                                String s = "";
                                int charsPerPage = 240;
                                int argIndex = 0;
                                try {

                                    boolean header = false;

                                    // defines optional arguments, adjusting the expected indexing for the rest of the arguments.
                                    for(int i = 1; i < args.length; i++){
                                        if(args[i].equalsIgnoreCase("-cpp")){
                                            argIndex += 2;
                                            charsPerPage = Integer.parseInt(args[i + 1]);
                                        }else if (args[i].equalsIgnoreCase("-h")){
                                            argIndex++;
                                            header = true;
                                        }
                                    }

                                    //moving this away from the optional argument loop guarantees that argIndex is correct.
                                    if(header){
                                        s += "Pulled from: " + args[argIndex + 1] + " by " + sender.getName() + ". ------------------- ";
                                    }

                                    File path = new File(Bukkit.getPluginManager().getPlugin("Libabel").getDataFolder().getPath() + File.separator + args[argIndex + 1].hashCode() + ".txt");
                                    boolean success = TextFileTools.downloadFromURL(path, new URL(args[argIndex + 1]));
                                    if (success) {
                                        s += TextFileTools.getStringFromFile(path);
                                        if (s != null) {
                                            String title = "";
                                            for(int i = argIndex + 2; i < args.length; i++){
                                                title += args[i] + " ";
                                            }
                                            title = title.stripTrailing();

                                            int emptySlots = 0;
                                            for(ItemStack is : p.getInventory().getStorageContents()){
                                                if(is == null){
                                                    emptySlots++;
                                                }
                                            }

                                            int i;
                                            ItemStack[] books = Text2Book.stringToBooks(s, title, charsPerPage);
                                            for(i = 0; i < books.length && i < emptySlots; i++){
                                                p.getInventory().addItem(books[i]);
                                            }
                                            if(i < books.length){
                                                for( ; i < books.length; i++){
                                                    p.getWorld().dropItemNaturally(p.getLocation(), books[i]);
                                                }

                                            }
                                            return true;
                                        } else {
                                            sender.sendMessage(ChatColor.RED + "Unable to parse.");
                                            return false;
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Retrieval failed.");
                                        return false;
                                    }
                                } catch (MalformedURLException e) {
                                    if(argIndex > 0){
                                        sender.sendMessage(ChatColor.RED + "Optional arguments must be placed at the beginning of the command.");
                                    }
                                    return false;
                                }catch (ArrayIndexOutOfBoundsException e){
                                    sender.sendMessage(ChatColor.RED + "Usage: \n/lbb [-h] [-cpp <chars per page>] <plaintext file URL> <book title>");
                                }
                            }else{
                                sender.sendMessage("You must be a player to get books from a URL.");
                                return false;
                            }
                        default:
                            sender.sendMessage(ChatColor.RED + "Invalid Arguments!");
                            return false;
                    }

            }
        }
        return false;
    }

}
