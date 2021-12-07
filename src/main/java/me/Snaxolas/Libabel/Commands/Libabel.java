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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


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
                            ChatColor.GREEN + "    /lbb search <terms>\n" +
                            ChatColor.DARK_AQUA + "Searches the authors and titles of books for the terms and returns an indexed list of the matching books.\n" +
                            ChatColor.GREEN + "    /lbb get <index>\n" +
                            ChatColor.DARK_AQUA + "Gets the book in the repository at the index provided.\n" +
                            ChatColor.GREEN + "    /lbb info <index>\n" +
                            ChatColor.DARK_AQUA + "Retrieves info about the book at the index provided.\n" +
                            ChatColor.GREEN +"    /lbb publish <description>\n" +
                            ChatColor.DARK_AQUA + "Publishes the book in your hand to the repository, with the provided description.\n" +
                            ChatColor.GREEN +"    /lbb remove <index>\n" +
                            ChatColor.DARK_AQUA + "Removes the book at the index provided from the repository.\n" +
                            ChatColor.GREEN +"    /lbb geturl [-h] [-cpp <chars per page>] <plaintext file URL> <book title>\n"+
                            ChatColor.DARK_AQUA + "Retrieves the plaintext file at the provided URL as a book with the provided title.\n" +
                            ChatColor.GOLD + "----------------------------------------");
                    return true;
                case 1:
                    switch(args[0].toLowerCase()){

                        case "get":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb get <book index>");
                            return true;

                        case "publish":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb publish <description>");
                            return true;

                        case "remove":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb remove <book index>");
                            return true;

                        case "search":
                            String[] results = index.search(new String[0]);

                            String toPrint = ChatColor.RED + "Results: \n";
                            for(int i = 0; i < results.length; i++){
                                toPrint += ChatColor.GOLD + "Index " + i + ": " + results[i] +"\n";
                            }
                            toPrint = toPrint.stripTrailing();
                            sender.sendMessage(toPrint);
                            return true;

                        case "info":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb info <book index>");
                            return true;

                        case "geturl":
                            sender.sendMessage(ChatColor.GOLD + "Usage: \n/lbb [-h] [-cpp <chars per page>] <plaintext file URL> <book title>");
                            return true;

                        default:
                            sender.sendMessage(ChatColor.RED + "Invalid arguments!");
                            return false;
                    }
                case 2:
                    switch(args[0].toLowerCase()){

                        case "geturl":
                            sender.sendMessage(ChatColor.RED + "You must include a title for the book!");
                            return false;

                    }
                default:
                    switch(args[0].toLowerCase()){

                        case "get":
                            try{
                                if(sender instanceof Player){
                                    Player p = (Player) sender;
                                    int i = Integer.parseInt(args[1]);
                                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

                                    // gets the book at the index included by the user in the json index
                                    BookMeta bookMeta = index.get(i);

                                    if(bookMeta != null){
                                        book.setItemMeta(bookMeta);
                                        p.getInventory().addItem(book);
                                        return true;
                                    }else{
                                        sender.sendMessage(ChatColor.RED + "No such index!");
                                        return false;
                                    }

                                }else{
                                    sender.sendMessage("You must be a player to get a book!");
                                    return false;
                                }
                            }catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.RED + "Index must be an integer!");
                                return false;
                            }

                        case "info":
                            try{
                                if(sender instanceof Player){
                                    Player p = (Player) sender;
                                    int i = Integer.parseInt(args[1]);
                                    String info = index.getInfo(i);
                                    if(info != null){
                                        p.sendMessage(info);
                                        return true;
                                    }else{
                                        sender.sendMessage(ChatColor.RED + "No such index!");
                                        return false;
                                    }

                                }else{
                                    sender.sendMessage("You must be a player to get a book!");
                                    return false;
                                }
                            }catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.RED + "Index must be an integer!");
                                return false;
                            }

                        case "remove":
                            try{
                                int toRemoveIndex = Integer.parseInt(args[1]);
                                BookMeta book = index.get(toRemoveIndex);
                                if(book != null){
                                    if(book.getAuthor().equals(sender.getName()) || sender.hasPermission("libabel.remove.others")) {
                                        if(index.remove(toRemoveIndex)){
                                            sender.sendMessage(ChatColor.GREEN + "Successfully removed book.");
                                            return true;
                                        }else{
                                            sender.sendMessage(ChatColor.RED + "Could not remove book.");
                                            return false;
                                        }
                                    }else{
                                        sender.sendMessage(ChatColor.RED + "You cannot remove others' books.");
                                        return false;
                                    }
                                }else{
                                    sender.sendMessage(ChatColor.RED + "Index out of bounds!");
                                    return false;
                                }
                            }catch(NumberFormatException e){
                                sender.sendMessage(ChatColor.RED + "Index must be an integer!");
                                return false;
                            }

                        case "search":
                            String[] terms = new String[args.length - 1];
                            for(int i = 1; i < args.length; i++){
                                terms[i-1] = args[i];
                            }
                            String[] results = index.search(terms);

                            String fullSearch = "";
                            for(String term : terms){
                                fullSearch += term + " ";
                            }
                            fullSearch = fullSearch.stripTrailing();

                            String toPrint = ChatColor.GREEN + "\nResults for \"" + fullSearch +  "\": \n";
                            if(results.length == 0){
                                toPrint += ChatColor.RED + "Nothing.";
                            }else{
                                for(int i = 0; i < results.length; i++){
                                    toPrint += ChatColor.GOLD + "Index " + i + ": " + results[i] +"\n";
                                }
                            }
                            toPrint = toPrint.stripTrailing();
                            sender.sendMessage(toPrint);
                            return true;

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

                                    // moving this away from the optional argument loop guarantees that argIndex is correct.
                                    if(header){
                                        s += "Pulled from: " + args[argIndex + 1] + " by " + sender.getName() + ". ------------------- ";
                                    }

                                    File path = new File(Bukkit.getPluginManager().getPlugin("Libabel").getDataFolder().getPath() + File.separator + args[argIndex + 1].hashCode() + ".txt");
                                    boolean success = TextFileTools.downloadFromURL(path, new URL(args[argIndex + 1]));
                                    if (success) {
                                        s += TextFileTools.getStringFromFile(path);
                                        path.delete();
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

                        case "publish":
                            if(sender instanceof Player){
                                Player p = (Player) sender;
                                PlayerInventory inv = p.getInventory();
                                if(inv.getItemInMainHand().getType().equals(Material.WRITTEN_BOOK)){
                                    BookMeta book = (BookMeta)inv.getItemInMainHand().getItemMeta();
                                    if (!index.checkDuplicate(book)){

                                        String description = "";
                                        for(int i = 1; i < args.length; i++){
                                            description += args[i] + " ";
                                        }
                                        description = description.trim();

                                        index.publish(book, description);
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


                        default:
                            sender.sendMessage(ChatColor.RED + "Invalid Arguments!");
                            return false;
                    }
            }
        }
        return false;
    }

}
