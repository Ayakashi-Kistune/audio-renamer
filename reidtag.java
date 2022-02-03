package ID3tagrename;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class reidtag {

    public static void main(String[] a) {
        filelist list = new filelist();                 

        list.makedirectory();
        list.addfileList();
        list.display();
        list.name();

        System.out.println("look at your file now at " + System.getProperty("user.dir") + "/" + "output");
    }

}

class filelist {    
    private List<String> musicList = new ArrayList<>();                      //where the name of each file is named and sorted by "void addfilelist()"
    private String mainDirectory = System.getProperty("user.dir");           // knows your main directory name, works with any computer its should be

    int size = musicList.size();

    void display() {                                                             // using your common sense//
        System.out.println("\nworking directory is: " + mainDirectory);
        if (musicList.isEmpty()) {
            System.out.println("list is empty\n");
        } else {
            for (String musicsString : musicList) {
                System.out.println(musicsString);
            }
            System.out.println();
        }

    }

    void addfileList() {                                   // filling up the list array of path and sort out the mp3 currently   // next update should be expanded to flac files
        File workingDirectory = new File(mainDirectory);                                                      
        String[] musicArr = workingDirectory.list();
        for (String fileArr : musicArr) {
            if (fileArr.contains(".mp3")) {
                set_musiclist(fileArr);
            }
        }

    }

    void set_musiclist(String element) {
        musicList.add(element);
    }

    void makedirectory() {
        File outputDir = new File(mainDirectory + "/" + "output");    // making a output directory, final output 
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
            System.out.println("output directory existed");
        }
    }

    renaming rename = new renaming();
    void name() {
        for (String string : musicList) {                           // for each path in the music list in the directory
            rename.renamed(string);
        }

    }
}















class renaming {
    void renamed(String pathfile) {

        System.out.println(pathfile);                                           //show the path file
        File mp3 = new File(System.getProperty("user.dir") + "/"  + pathfile);                                          //new file          <--------------
                                                                                //                                 \
        try {                                                                   //                                  \
            BufferedReader reader = new BufferedReader(new FileReader(mp3));        //reader scanner for the file ---\       <<<<<<---
            List<String> identitysStrings = new ArrayList<>();                      // stores what the reader reads from the reader --\

            String line;                                                            //line element to store in each list array
            
            try {
                while ((line = reader.readLine()) != null) {    
                    identitysStrings.add(line);                                     // insert element   
                    if (line.contains("image")) {                                   // stop the reader if the reader saw a "image" string inside the string
                        break;
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

            //display(identitysStrings);                                              // uncomment this to see the logs, on how the program grabs the title and artist
            boolean isfakeMp3 = true;
            for (int i = 0; i < identitysStrings.size(); i++) {
                if (identitysStrings.get(i).contains("TIT2")) {
                    isfakeMp3 = false;
                    break;
                }

                if (i == identitysStrings.size()-1 && isfakeMp3) {
                    break;
                }
            }
            if (isfakeMp3) {
                System.out.println("file skipped, because your file is not original. in short, your mp3 is not genuine or just a convert from youtube, get a real file...\nPS dont be dumb your, your file doesnt contain metatag thats why...");
            }else{
                String artistname   = artistString(identitysStrings);
                String titlename    = titleString(identitysStrings) ;

                System.out.println("title : " + titlename + "..\nartist : " + artistname +"..");
                
                System.out.println("copying:....");
                functionCopy(titlename, artistname, pathfile);
            }
            
        } catch (FileNotFoundException e) {                                         // try and catch an error
            e.printStackTrace();
        }
        System.out.println("\n------------------------------------------------------------------------------------------------------------------\n\n");

        

    }

    void display(List<String> list) {                                               // common sense
        int x = 1;
        for (String string : list) {

            System.out.println("line " + (x++) + "  " + string);
        }
    }


    String artistString(List<String> lines){  ////////// you left croping the artist name
        
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("TPE1") && lines.get(i).contains("TALB")) {                   // IF BOTH SIDES IN THE SAME LINE
                //System.out.println("pass 1");
                return artistcroper(lines.get(i), true);
                
                
            }else if(lines.get(i).contains("TPE1") && lines.get(i+1).contains("TALB")){             // IF THE ONE OF THE SIDES ARE ON THE OTHER LINES //potential bug if TALB is on the 3rd line
                //System.out.println("pass 2");
                return artistcroper(lines.get(i+1), false);

            }else if (i == lines.size()-1) {
                System.out.println("the file does not contain any ARTIST metadata");
                break;
            }
        }
        return "unk";
    }
    String artistcroper(String ofaString, boolean condition1or2){                              // flip flop style
        String artist;
        if (condition1or2) {

            artist = ofaString.substring(ofaString.indexOf("TPE1")+4, ofaString.indexOf("TALB"));
            
            //int i = 0;
            while(true){
                artist = artist.substring(1);
                //System.out.println(i + "  =  " + artist);
                if (Character.isLetter(artist.charAt(0))) {
                    //System.out.println("is this char ?   " +  artist.charAt() + "  lol");
                    break;
                }

                // you left to remove the parentheses () by string.replace!
                //i++;
            }

        }else{
            //int i = 0;
            artist = ofaString.substring(0, ofaString.indexOf("TALB")); 
            while(true){
                artist = artist.substring(1);
                //System.out.println(i + "  =  " + artist);
                if (Character.isLetter(artist.charAt(0))) {
                    //System.out.println("is this char ?   " +  artist.charAt() + "  lol");
                    break;
                }
                //i++;
            }
            
        }
        //System.out.println("log cropped:    " + artist);
        
        if (artist.contains("(") && artist.contains(")")) {                 //if both contains
            if (!artist.startsWith("(")) {                                               // if   "title  (bruh)"
                artist = artist.substring(0, artist.indexOf("(")-1);
            }else{                                                                      // if   "(bruh)  title"
                artist = artist.substring(artist.indexOf(")")+1 , artist.length());         
            }
        }else if(artist.contains("(") && !artist.contains(")")){            //if "(" contains
            artist = artist.replace("(", "");
        }else if (!artist.contains("(") && artist.contains(")")) {
            artist = artist.replace(")", "");
        }

        artist = artist.replace("\u0000", "");                              // to remove file java.nio.file.InvalidPathException: Nul character not allowed         the solution is on java.base/sun.nio.fs.UnixPath.checkNotNul(UnixPath.java:91) website

        return artist;
    }


    String titleString(List<String> lines){
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("TIT2") && lines.get(i).contains("TPE1")) {                   // IF BOTH SIDES IN THE SAME LINE
                //System.out.println("pass 1");
                return titlecroper(lines.get(i), true);
                
                
            }else if(lines.get(i).contains("TIT2") && lines.get(i+1).contains("TPE1")){             // IF THE ONE OF THE SIDES ARE ON THE OTHER LINES //potential bug if TALB is on the 3rd line
                //System.out.println("pass 2");
                return titlecroper(lines.get(i+1), false);

            }else if (i == lines.size()-1) {
                System.out.println("the file does not contain any TITLE metadata");
                break;
            }
        }
        return "unk";
    }
    String titlecroper(String ofaString, boolean condition1or2){
        String title;
        if (condition1or2) {
            title = ofaString.substring(ofaString.indexOf("TIT2")+4, ofaString.indexOf("TPE1"));
            //int i = 0;
            while(true){
                title = title.substring(1);
                //System.out.println(i + "  =  " + title);
                if (Character.isLetterOrDigit(title.charAt(0)) && !title.contains("\\")  && !title.contains("0")) {
                    //System.out.println("is this char ?   " +  artist.charAt() + "  lol");
                    break;
                }
                //i++;
            }
            

        }else{
            title = ofaString.substring(0, ofaString.indexOf("TPE1")); 
            while(true){
                title = title.substring(1);
                
                if (Character.isLetter(title.charAt(0)) || title.charAt(0) != 0) {
                    //System.out.println("is this char ?   " +  artist.charAt() + "  lol");
                    break;
                }
            }
            
        }
        if (title.contains("(") && title.contains(")")) {                           //if both contains
            if (!title.startsWith("(")) {                                               // if   "title  (bruh)"
                title = title.substring(0, title.indexOf("(")-1);
            }else{                                                                      // if   "(bruh)  title"
                title = title.substring(title.indexOf(")")+1 , title.length());         
            }
        }else if(title.contains("(") && !title.contains(")")){                          //if "(" contains
            title = title.replace("(", "");
        }else if (!title.contains("(") && title.contains(")")) {                        // if only ")" contains
            title = title.replace(")", "");
        }
        title = title.replace("\u0000", "");                                            // to remove file java.nio.file.InvalidPathException: Nul character not allowed         the solution is on java.base/sun.nio.fs.UnixPath.checkNotNul(UnixPath.java:91) website
        //System.out.println("log cropped:    " + title);
        return title;
    }


    void functionCopy(String titlesString, String artistString, String beforefile){
        try {
            Path movesPath = Files.copy( Paths.get(System.getProperty("user.dir") + "/" + beforefile),   Paths.get(System.getProperty("user.dir") + "/" +  "output" + "/" + titlesString + " - " + artistString + ".mp3"), StandardCopyOption.REPLACE_EXISTING); 
            movesPath.getFileName();
        } catch (IOException e) {
            System.out.println("it failed tho");
            e.printStackTrace();
        }
    }   
}