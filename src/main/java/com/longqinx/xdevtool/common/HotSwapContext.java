package com.longqinx.xdevtool.common;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.longqinx.xdevtool.bean.HostConfig;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class HotSwapContext {
    private Project project;
    private PsiClass targetClass;

    private HostConfig hostConfig;
    private String serviceName;

    private String javaPath;
    private String arthasPath;

    public HotSwapContext() {
    }

    public HotSwapContext(Project project, PsiClass targetClass) {
        this.project = project;
        this.targetClass = targetClass;
    }

    public Project getProject() {
        return project;
    }

    public PsiClass getTargetClass() {
        return targetClass;
    }


    public void setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getArthasPath() {
        return arthasPath;
    }

    public void setArthasPath(String arthasPath) {
        this.arthasPath = arthasPath;
    }
}
