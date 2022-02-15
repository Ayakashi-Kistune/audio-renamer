package ID3tagrename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


//encapsulation     -   check
//abstraction       -   check
//inheritance       -   check
//object usage      -   check
//polymorphism      -   wala asa


public class tag {
    public static void main(String[] args) {

        List<music> musiclist = new ArrayList<music>();

        musicinsert insert = new musicinsert();
        copyprocess copymachine = new copyprocess();
        insert.set_musiclist(musiclist, System.getProperty("user.dir"));
        insert.displayinfo(musiclist);
        copymachine.docopy(musiclist);
        

    }
}

class music {
    private String name;
    private String artist;
    private String path;
    private boolean ismp3; // if not its flac
    private boolean isfake;
    
    public void setName(String name) {
        this.name = name;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setIsmp3(boolean ismp3) {
        this.ismp3 = ismp3;
    }
    public void setIsfake(boolean isfake) {
        this.isfake = isfake;
    }

    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }
    public String getPath() {
        return path;
    }
    public String getismp3(){
        return Boolean.toString(ismp3);
    }
    public String getisfake(){
        return Boolean.toString(isfake);
    }
}

class musicinsert extends processfinder {

    void    set_musiclist(List<music> musicstorageList, String workingdir) {
        File directory = new File(workingdir);
        String[] musicpaths = directory.list();
        List<String> filtered_music = new ArrayList<String>();

        for (String string : musicpaths) {
            if (string.contains("mp3")) {
                filtered_music.add(string);
            }
        }
        

        for (String string : filtered_music) {
            //System.out.println("--------------------------------------------------\n\n"+string);
            music file = new music();

            file.setName(set_name(string, workingdir));
            file.setArtist(set_artist(string, workingdir));
            file.setIsfake(set_isfake(string, workingdir));
            file.setIsmp3(set_ismp3(string));
            file.setPath(set_path(string, workingdir));

            musicstorageList.add(file);
        }

        // for (String string : musicpaths) {
        //     System.out.println(string);
        // }
    }

    String set_name(String name, String workdirString) {
        return filescan(workdirString,name, true);
    }

    String set_artist(String name, String workdirString) {
        return filescan(workdirString, name, false);
    }

    String set_path(String pathString, String workdirString) {
        return (workdirString + "/" + pathString);
    }

    boolean set_isfake(String name, String workidirString) {
        return filecheck(name, workidirString);
    }

    boolean set_ismp3(String name) {
        if (name.contains(".mp3")) {
            return true;
        } else {
            return false;
        }

    }
}

abstract class processfinder {
    String filescan(String dir,String filename, boolean isTitle) {
        File musicFile_Object = new File(dir + "/" + filename);
        String stringmodify = "";
        String first, lastALT, lastALT2;
        
        int[] charInt = new int[1000];
        List<Integer> sortInt = new ArrayList<Integer>();
        
        if (isTitle) {
            first = "TIT2";
            lastALT = "TPE1";
            lastALT2 = "TRCK";
        } else {
            first = "TPE1";
            lastALT = "TALB";
            lastALT2 = "TRCK";
        }

        try {
            FileInputStream inputStream = new FileInputStream(musicFile_Object);


            for (int i = 0; i < charInt.length; i++) {
                charInt[i] = inputStream.read();
            }

            inputStream.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sorter_array2list(charInt, sortInt);

        stringmodify = converter_Integer2string(sortInt, stringmodify);

        stringmodify = dosomecroping(stringmodify, first);


        // uncomment this for logs

        // if (isTitle) {
        //     System.out.println("title ");
        // }else{
        //     System.out.println("\nartist ");
        // }

        // System.out.println("log " + stringmodify);
        // System.out.println("index of - " + stringmodify.indexOf(lastALT)  + "\n\t" +stringmodify.indexOf(lastALT2));

        // System.out.println("finalize " + finalString(stringmodify,first, lastALT, lastALT2));

        
        return finalString(stringmodify, first, lastALT, lastALT2);

    }

    String finalString(String stringmodify,String start, String where1, String where2){
        //System.out.println("log final " + stringmodify);
        if (stringmodify.contains(where1) || stringmodify.contains(where2)) {
            if (stringmodify.contains(where1)) {
                return stringmodify.substring(stringmodify.indexOf(start)+4, stringmodify.indexOf(where1)).stripLeading();
            }else if(stringmodify.contains("TCON")){
                return stringmodify.substring(stringmodify.indexOf(start)+4, stringmodify.indexOf("TCON")).stripLeading();
            }else if (stringmodify.contains(where2)) {
                return stringmodify.substring(stringmodify.indexOf(start)+4, stringmodify.indexOf(where2)).stripLeading();
            }else{
                return "";
            }
        }else{
            return "";
        }
    }

    String dosomecroping(String which_to_crop, String startingline){
        if (which_to_crop.contains(startingline)) {
           return which_to_crop.substring(which_to_crop.indexOf(startingline));
        }else{
            return which_to_crop;
        }
    }

    void sorter_array2list(int[] array, List<Integer> list){
        for (int i : array) {
            
            if ((i == 32) || (i > 48 && i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122) || (i == 40) || (i==41) || (i == 39) ) {
                list.add(i);
            }
        }
    }

    String converter_Integer2string(List<Integer> list, String where_tostore){
        for (Integer integer : list) {
            where_tostore += Character.toString(integer);
        }
        return where_tostore;
    }

    boolean filecheck(String name, String dir){
        File file = new File(dir + "/" + name);
        int[] data = new int[1000];
        String dataString = "";
        FileInputStream find;

        try {
            find = new FileInputStream(file);
            
    
            for (int i = 0; i < data.length; i++) {
                data[i] = find.read();
            }
            find.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i : data) {
            dataString += Character.toString(i);
        }
        if (dataString.contains("TIT2") || dataString.contains("TPE1")) {
            return true;
        } else {
            return false;
        }
    }

    void displayinfo(List<music> files){
        System.out.printf("\n\n*****RESULTS*****\n%-55s %-30s %6s %6s %-10s\n", "name" , "artist", "mp3?" , "fake?", "path");
        for (music music : files) {
            if (Boolean.parseBoolean(music.getisfake()) && !music.getName().isEmpty() ) {
                System.out.printf("%-55s %-30s %6s %6s %-10s\n\n", music.getName(), music.getArtist(), music.getismp3() , music.getisfake(), music.getPath());
            }else{
                System.out.printf("%-55s %-30s %6s %6s %-10s\n\n", "-none-", "-none-", music.getismp3() , music.getisfake(), music.getPath());                
            }
        }
    }
}

class copyprocess{
    public void docopy(List<music> list){
        makedir();

        for (music music : list) {
            if (!music.getName().isEmpty()) {
                copymech(music.getName(), music.getArtist(),music.getPath());                
            }else{
                System.out.println("*!*" + " unable to copy   ".toUpperCase() + music.getPath());
            }
        }
    }
    
    private void makedir(){
        File outputdir = new File(System.getProperty("user.dir") + "/" + "output");
        if (!outputdir.exists()) {
            outputdir.mkdirs();
        } else {
            System.out.println("output directory already existed");
        }
    }

    private void copymech(String title, String artist, String path){
        try {
            Files.copy(Paths.get(path), Paths.get(System.getProperty("user.dir") + "/" + "output" + "/" + title + " - " + artist + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
            File exist = new File(System.getProperty("user.dir") + "/" + "output" + "/" + title + " - " + artist + ".mp3");
            if (exist.exists()) {
                System.out.println("copy-[done] " + title + " - " + artist);
            }else{
                System.out.println("copy-[fail] " + title + " - " + artist);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}