package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;

import java.io.BufferedReader;
import java.io.IOException;

public interface BuildHistoryService {
    void addBuildHistory(BufferedReader stream, Project project) throws InterruptedException, IOException;
}
