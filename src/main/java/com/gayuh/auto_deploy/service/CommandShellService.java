package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;
import org.springframework.web.multipart.MultipartFile;

public interface CommandShellService {
    void saveProjectCommand(Project project, MultipartFile file);

    Boolean updateCommandShellFile(Project project, MultipartFile file);

    void deleteCommandShellFile(String fileName);
}
