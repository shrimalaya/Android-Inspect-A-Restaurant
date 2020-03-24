package com.example.cmpt_cobalt.model;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.example.cmpt_cobalt.view.DownloadActivity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CSVDowloader {
    private String url;
    private String fileName;

    public CSVDowloader (String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void download() {
        try {
            InputStream in = new URL(this.url).openStream();
            File file = new File(Environment.getDataDirectory(),fileName);
            file.createNewFile();
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
