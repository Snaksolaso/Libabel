package me.Snaxolas.Libabel.Util;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;

import java.awt.print.Book;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BookIndex {private File jsonIndexFile;
    private File bookFolder = new File(Bukkit.getPluginManager().getPlugin("Libabel").getDataFolder().getPath() + File.separator + "Books");
    private JsonArray jsonArr; // org.json.simple

    public BookIndex(File file) {
        this.jsonIndexFile = file;
        reload();
        loadIndex();
    }

    public String[] search(String[] terms){

        ArrayList<String> matches = new ArrayList<>();
        for(int i = 0; i < jsonArr.size(); i++){

            JsonObject atIndex = ((JsonObject) jsonArr.get(i));
            String blob = "";
            blob += atIndex.getAsJsonPrimitive("title").getAsString() + " ";
            blob += atIndex.getAsJsonPrimitive("author").getAsString() + " ";
            blob += atIndex.getAsJsonPrimitive("description").getAsString() + " ";
            blob = blob.toLowerCase();

            boolean match = true;
            for(String s : terms){
                if (!blob.contains(s.toLowerCase())){
                    match = false;
                    break;
                }
            }

            if(match){
                matches.add(getInfo(i));
            }
        }
        return matches.toArray(new String[0]);
    }

    public String getInfo(int index) {
        if(index >= jsonArr.size()) {
            return null;
        }else {
            JsonObject atIndex = ((JsonObject) jsonArr.get(index));
            String title = atIndex.getAsJsonPrimitive("title").getAsString();
            String author = atIndex.getAsJsonPrimitive("author").getAsString();
            String pageCount = atIndex.getAsJsonPrimitive("page_count").getAsString();
            String description = atIndex.getAsJsonPrimitive("description").getAsString();
            return ChatColor.DARK_AQUA + title + (author == null ? "" : ", by " + author) + ". Page Count: " + pageCount + ChatColor.DARK_GREEN + "\n    " + description;
        }
    }

    public void publish(BookMeta book, String description) {
        addBook(book, description);
    }

    public void addBook(BookMeta book, String description) {
        if(!checkDuplicate(book)){
            String bookPath = bookFolder.getPath() + File.separator + book.hashCode() + ".book";
            BookIO.saveBook(bookPath, book);
            jsonArr.add(getBookInfoAsJson(book, description, bookPath));
            saveIndex();
        }
    }

    public boolean checkDuplicate(BookMeta book) {
        for(int i = 0; i < jsonArr.size(); i++){
            BookMeta other = BookIO.loadBook(((JsonObject)jsonArr.get(i)).get("path").getAsString());
            if(other.getTitle().equals(book.getTitle()) && other.getPages().equals(book.getPages()) && other.getAuthor().equals(book.getAuthor()))
                return true;
        }
        return false;
    }

    public BookMeta get(int index){
        if(index >= jsonArr.size() || index < 0) {
            return null;
        }else{
            JsonObject atIndex = ((JsonObject)jsonArr.get(index));
            return BookIO.loadBook(atIndex.getAsJsonPrimitive("path").getAsString());
        }
    }

    public boolean remove(int index){
        if(index >= jsonArr.size() || index < 0 ) {
            return false;
        }else {
            jsonArr.remove(index);
            saveIndex();
            return true;
        }
    }

    public JsonObject getBookInfoAsJson(BookMeta book, String description, String bookPath){
        JsonObject bookInfo = new JsonObject();
        bookInfo.addProperty("path", bookPath);
        bookInfo.addProperty("author", book.getAuthor());
        bookInfo.addProperty("title", book.getTitle());
        bookInfo.addProperty("page_count", book.getPageCount());
        bookInfo.addProperty("description", description);
        return bookInfo;
    }

    private void saveIndex(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TextFileTools.writeStringToFile(jsonIndexFile, gson.toJson(jsonArr));
    }

    private void loadIndex(){
        String s = TextFileTools.getStringFromFile(jsonIndexFile);
        JsonParser jp = new JsonParser();
        jsonArr = jp.parse(s).getAsJsonArray();
    }


    // Evil, evil method, which should never be called unless in a dire pinch (the index file got obliterated, somehow).
    // Completely wipes all descriptions and scrambles indexing, if they still exist.
    public void reIndex() {
        File dir = jsonIndexFile.getParentFile();
        File[] bookFiles = dir.listFiles(file -> !file.getPath().equals(jsonIndexFile.getPath()));
        JsonArray allBooks = new JsonArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if(bookFiles.length != 0) {

            BookMeta[] bookArr = new BookMeta[bookFiles.length];
            for(int i = 0; i < bookFiles.length; i++){
                bookArr[i] = BookIO.loadBook(bookFiles[i].getPath());
            }

            for (int i = 0; i < bookArr.length; i++) {
                JsonObject bookInfo = getBookInfoAsJson(bookArr[i], bookFiles[i].getPath(), "This book appears to be from an ancient period, lost to time.");
                allBooks.add(bookInfo);
            }
            jsonArr = allBooks;
            TextFileTools.writeStringToFile(jsonIndexFile, gson.toJson(allBooks));
        }else {
            TextFileTools.writeStringToFile(jsonIndexFile, "[]");
        }
    }

    public void reload() {
        try {
            if (!jsonIndexFile.exists()) {
                reIndex();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        loadIndex();
    }
}
