package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandShellServiceImpl implements CommandShellService {
    private final Path folderCommandPath;

    @Override
    public void saveProjectCommand(Project project, MultipartFile file) {
        checkContentType("octet-stream", file);

        String fileName = project.getId() + "-" + file.getOriginalFilename();

        project.setFileName(fileName);

        project.setPath(saveShell(file, fileName));
    }

    @Override
    public Boolean updateCommandShellFile(Project project, MultipartFile file) {
        boolean isDataChange = false;
        if (file != null) {
            checkContentType("octet-stream", file);
            isDataChange = true;
            deleteCommandShellFile(project.getFileName());
            saveProjectCommand(project, file);
        }

        return isDataChange;
    }

    @Override
    public void deleteCommandShellFile(String fileName) {
        deleteFile(fileName);
    }

    private String saveShell(MultipartFile file, String fileName) {
        String path;
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = folderCommandPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            path = filePath.toAbsolutePath().toString();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error save file " + file.getOriginalFilename());
        }

        return path;
    }

    private void deleteFile(String fileName) {
        try {
            Path filePath = folderCommandPath.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error delete file " + fileName);
        }
    }

    private void checkContentType(String content, MultipartFile file) {
        String type = Objects.requireNonNull(file.getContentType()).split("/")[1];
        if (!type.equalsIgnoreCase(content)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only accept " + content);
        }
    }

}
