package com.longqinx.xdevtool.util;

import com.jcraft.jsch.*;
import com.longqinx.xdevtool.bean.HostConfig;

import java.util.Properties;

/**
 * @author xiaos
 * @since 2023/12/4
 */
public class SSHUtil {
    public static Session createNewSession(HostConfig hostConfig) throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(hostConfig.getUsername(), hostConfig.getIp());
        session.setPassword(hostConfig.getPassword());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(30000);
        session.connect();

        return session;
    }

    public static void exec(Session session, String cmd) throws JSchException {
        ChannelExec shell = (ChannelExec) session.openChannel("exec");
        shell.setCommand(cmd);
        shell.connect();
        shell.start();
        shell.disconnect();
    }

    public static void uploadFile(Session session, String src, String dst) throws JSchException, SftpException {
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.put(src, dst);
        sftp.disconnect();
    }

    public static void downloadFile(Session session, String src, String dst) throws JSchException, SftpException {
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        sftp.get(src, dst);
        sftp.disconnect();
    }
}
