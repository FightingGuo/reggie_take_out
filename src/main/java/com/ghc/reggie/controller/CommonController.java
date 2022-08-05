package com.ghc.reggie.controller;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/26 - 21:29
 */

import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传下载
 */
@ResponseBody
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    public String path;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")  //MultipartFile 的型参必须和前端的文件框的name属性的值保持一致
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
//        log.info("file:{}",file);

        //获得原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取原文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String filename = UUID.randomUUID().toString();
        filename+=suffix;

        //创建一个目录对象
        File dir=new File(path);
        //判断当前目录是否存在
        if (!dir.exists()){ //不存在时需要创建
            dir.mkdir();
        }


        try {
            file.transferTo(new File(path+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }


    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response){
        //输入流，通过输入流读取文件内容
        try {
            InputStream inputStream=new FileInputStream(new File(path+name));

            //输出流，通过输出流将图片展示到浏览器上
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int length=0;
            byte[] bytes=new byte[1024];
            //inputStream读到的数据放入bytes中 read()!=-1就每没读完
            while ( (length= inputStream.read(bytes) ) !=-1){
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
