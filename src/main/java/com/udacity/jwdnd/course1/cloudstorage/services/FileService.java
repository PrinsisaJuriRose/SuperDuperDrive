package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class FileService {
    private FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<File> getAllFiles(Integer userId)
    {
        return fileMapper.GetFilesOfUser(userId);
    }

    public File getFileById(Integer fileId)
    {
        return fileMapper.GetFileById(fileId);
    }

    public int uploadFile(MultipartFile multipartFile, Integer userId) throws IOException, SQLException {

        File file = new File();

        file.setFileName(multipartFile.getOriginalFilename());
        file.setContentType(multipartFile.getContentType());
        file.setFileData(multipartFile.getBytes());
        file.setFileSize(String.valueOf(multipartFile.getSize()));
        file.setUserId(userId);

        return fileMapper.insertFile(file);
    }

    public int deleteFile(Integer fileId)
    {
        return fileMapper.deleteFile(fileId);
    }

    public int updateNote(File file)
    {
        return fileMapper.updateFile(file);
    }

    public boolean checkFileNameDuplicate(MultipartFile multipartFile, Integer userId)
    {
        boolean fileNameExistBefore = false;
        List <File> files = fileMapper.GetFilesOfUser(userId);

        for (File currFile:files){

            if (currFile.getFileName().equals(multipartFile.getOriginalFilename())) {
                fileNameExistBefore = true;
                break;
            }
        }
        return fileNameExistBefore;
    }
}
