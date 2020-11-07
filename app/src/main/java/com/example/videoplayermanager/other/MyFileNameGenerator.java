package com.example.videoplayermanager.other;

import com.danikula.videocache.file.FileNameGenerator;

public class MyFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(String url) {
        String[]strs=url.split("/");
        return strs[strs.length-1];
    }
}
