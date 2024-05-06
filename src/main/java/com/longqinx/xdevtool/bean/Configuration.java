package com.longqinx.xdevtool.bean;

import java.util.List;

/**
 * @author xiaos
 * @since 2023/10/24
 */
public class Configuration {
    private List<HostConfig> hostList;
    private List<String> serviceList;

    private String javaPath;
    private String arthasPath;

    public List<HostConfig> getHostList() {
        return hostList;
    }

    public void setHostList(List<HostConfig> hostList) {
        this.hostList = hostList;
    }

    public List<String> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<String> serviceList) {
        this.serviceList = serviceList;
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
