package com.example.cmpt_cobalt.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CSVDowloader {
    private String url;
    private String fileName;

    public CSVDowloader(String url, String fileName){
        this.url = url;
        this.fileName = fileName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void download(){
        try{
            InputStream in = new URL(this.url).openStream();
            Files.copy(in, Paths.get(this.fileName), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
