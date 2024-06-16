package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;
import org.springframework.web.multipart.MultipartFile;

public interface CommandShellService {
    void saveProjectCommand(Project project, MultipartFile file);
    void updateImageQuestion(MultipartFile file, String mediaId);
    void deleteCommandShellFile(String mediaId);
    void deleteAllImageByQuestionTitleId(String questionTitleId);
}
