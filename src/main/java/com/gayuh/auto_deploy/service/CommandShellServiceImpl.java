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

        project.setName(file.getOriginalFilename());

        String fileName = project.getId() + "-" + file.getOriginalFilename();

        project.setPath(saveShell(file, fileName));
    }

    @Override
    public void updateImageQuestion(MultipartFile file, String mediaId) {
//        checkContentType("image", file);
//        Media media = getMediaById(mediaId);
//        String fileName = mediaId + "-" + media.getName();
//
//        try {
//            if (Boolean.FALSE.equals(compareTwoImage(file, fileName))) {
//                deleteFile(media);
//
//                media.setName(file.getOriginalFilename());
//                media.setSize((int) file.getSize());
//                mediaRepository.save(media);
//
//                fileName = mediaId + "-" + file.getOriginalFilename();
//                saveShell(file, fileName);
//            }
//        } catch (IOException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error update file " + file.getOriginalFilename());
//        }
    }

    @Override
    public void deleteCommandShellFile(String fileName) {
        deleteFile(fileName);
    }

    @Override
    public void deleteAllImageByQuestionTitleId(String questionTitleId) {
//        List<Media> medias = mediaRepository.findAllMediaByQuestionTitleId(questionTitleId);
//
//        if (!medias.isEmpty()) {
//            medias.forEach(this::deleteFile);
//            mediaRepository.deleteAllMediaByQuestionTitleId(questionTitleId);
//        }
    }

    public String saveShell(MultipartFile file, String fileName) {
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

    public void deleteFile(String fileName) {
        try {
            Path filePath = folderCommandPath.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error delete file " + fileName);
        }
    }

    /**
     * For compare the image input
     * Arrays.equals(multipartFile.getBytes(), Files.readAllBytes(filePath));
     */

    private /*Boolean*/ void compareTwoImage(MultipartFile file, String fileName) throws IOException {
//        Path filePath = folderImagePath.resolve(fileName);
//        return Arrays.equals(file.getBytes(), Files.readAllBytes(filePath));
    }

    private void checkContentType(String content, MultipartFile file) {
        String type = Objects.requireNonNull(file.getContentType()).split("/")[1];
        log.info("The type is {}", type);
        if (!type.equalsIgnoreCase(content)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only accept " + content);
        }
    }

    private Project getMediaById(String mediaId) {
//        return mediaRepository.findById(mediaId).orElseThrow(
//                ResponseStatusExceptionUtil::notFound
//        );

        return null;
    }

}
