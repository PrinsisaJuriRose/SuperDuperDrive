package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Controller
@RequestMapping("/file")
public class FileController {

    FileService fileService;
    UserService userService;
    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public String uploadFile(Authentication authentication, MultipartFile fileUpload, Model model) throws SQLException, IOException {
        Integer userId = userService.getUserIdByUserName(authentication.getName()); //Get the ID of the current login user
        if(fileUpload.isEmpty())
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "Can not find the file");
        }
        else if( fileService.checkFileNameDuplicate(fileUpload, userId))
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "The file name exists before, please change it");
        }
        else
        {
            int rowAdded = fileService.uploadFile(fileUpload, userId);
            if(rowAdded == 1)
            {
                model.addAttribute("errorFlag", false);
            }
            else
            {
                model.addAttribute("errorFlag", true);
                model.addAttribute("displayMessage", "There is error when uploading a new file");
            }
        }

        model.addAttribute("activeTab", "nav-files");
        return "result";
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam("id") Integer fileId, Model model)
    {
        if(fileId > 0) {
           int rowDeleted =  fileService.deleteFile(fileId);
           if(rowDeleted == 1)
           {
               model.addAttribute("errorFlag", false);
           }
           else
           {
               model.addAttribute("errorFlag", true);
               model.addAttribute("displayMessage", "There is error when deleting the file");
           }
        }
        else
        {
            model.addAttribute("errorFlag", true);
            model.addAttribute("displayMessage", "Can not find the file");
        }

        model.addAttribute("activeTab", "nav-files");
        return "result";
    }

    @GetMapping("/download")
    public ResponseEntity downloadFile(@RequestParam("id") Integer fileId)
    {
        File file = fileService.getFileById(fileId);
        if (file == null) {
            return ResponseEntity.notFound().build(); //Return a 404 if the file is not found
        }

        String fileName = file.getFileName();

        // Encode the filename to handle special characters (like Arabic)
        String encodedFileName = "";
        try {
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the Content-Disposition header to indicate a file attachment
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);

        // Return the file data as a downloadable response
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .headers(headers)
                .body(file.getFileData());
    }
}
