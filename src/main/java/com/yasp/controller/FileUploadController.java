package com.yasp.controller;

import com.yasp.dto.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileUploadController {

    // 从 application.yml 读取配置
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Response<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Response<String> resp = new Response<>(null);

        if (file.isEmpty()) {
            resp.setCode(400);
            resp.setMessage("文件不能为空");
            resp.setData("文件不能为空");
            return ResponseEntity.badRequest().body(resp);
        }

        try {
            // 1. 获取绝对路径并创建目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // 自动创建 ./uploads 文件夹
            }

            // 2. 生成随机文件名 (UUID)
            String originalFilename = file.getOriginalFilename();
            // 防止文件名为空
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".png";

            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 保存文件
            File dest = new File(directory.getAbsolutePath() + File.separator + newFileName);
//            file.transferTo(dest);

            Thumbnails.of(file.getInputStream())
                    .size(800, 800)
                    .outputQuality(0.8f)
                    .toFile(dest);

            // 4. 返回可访问的 URL
            // 前端拿到这个 /images/xxx.png 后，浏览器请求它，会被 WebMvcConfig 拦截并映射到硬盘
            String fileUrl = "/images/" + newFileName;

            resp.setCode(200);
            resp.setMessage("上传成功");
            resp.setData(fileUrl);

            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            e.printStackTrace();
            resp.setCode(500);
            resp.setMessage("上传失败");
            resp.setData("上传失败");
            return ResponseEntity.internalServerError().body(new Response<>( "上传失败: " + e.getMessage()));
        }
    }
}
