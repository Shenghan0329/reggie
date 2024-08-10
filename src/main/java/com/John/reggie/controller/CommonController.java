package com.John.reggie.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.John.reggie.Common.R;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Autowired
    ServletContext context;

    @Value("${reggie.path}")
    private String path;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String p = "reggie_images/";

        String originalName = file.getOriginalFilename();
        String name = UUID.randomUUID().toString()
            +originalName.substring(originalName.lastIndexOf("."));

        File dir = new File(p);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(p+name));
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return R.success(name);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse res){
        try {
            String p = "reggie_images/";
            File f = new File(p+name);
            FileInputStream fileInputStream = new FileInputStream(f);
            ServletOutputStream outputStream = res.getOutputStream();

            res.setContentType("img/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
       
    }
}
