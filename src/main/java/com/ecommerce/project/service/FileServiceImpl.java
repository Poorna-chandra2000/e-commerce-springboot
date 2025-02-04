package com.ecommerce.project.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileServiceImpl implements FileService{

    @Value("${project.image}")
    private String imagePath;



    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {//file can be replsced as image also

        // Use imagePath instead of the passed `path`
        path = imagePath;

        //get file names of currrent or original file
        String originalFileName=file.getOriginalFilename();
        //Generate a unique file name we random uui
        String randomId= UUID.randomUUID().toString();
        //example:mat.jpg --> 1234 --> 1234.jpg
        String fileName=randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));//this preserves original extension
        String filePath=path + File.separator + fileName;

        //check if path exist and create
        File folder=new File(path);
        if(!folder.exists()) folder.mkdir();

        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        //returing file name
        return fileName;
    }
}
