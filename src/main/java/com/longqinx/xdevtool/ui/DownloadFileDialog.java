package com.longqinx.xdevtool.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.jcraft.jsch.Session;
import com.longqinx.xdevtool.bean.Configuration;
import com.longqinx.xdevtool.bean.HostConfig;
import com.longqinx.xdevtool.util.CollectionUtil;
import com.longqinx.xdevtool.util.SSHUtil;
import com.longqinx.xdevtool.util.ConfigurationUtil;
import com.longqinx.xdevtool.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class DownloadFileDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField remotePath;
    private JTextField localPath;
    private JComboBox<HostConfig> hostList;

    private Project project;
    private VirtualFile targetDir;

    public DownloadFileDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        HostConfig hostConfig = (HostConfig) hostList.getSelectedItem();
        String remoteFilePath = remotePath.getText();
        String localPath = this.localPath.getText();
        VirtualFile downloadDir = targetDir;

        if (localPath != null && !localPath.isEmpty()) {
            downloadDir = LocalFileSystem.getInstance().findFileByPath(localPath);
        }
        if (downloadDir == null || !downloadDir.isDirectory()) {
            NotificationUtil.error(project, "Local directory not valid.");
        }
        if (remoteFilePath == null) {
            NotificationUtil.error(project, "Remote file path should not be null.");
        }
        if (hostConfig == null) {
            NotificationUtil.error(project, "Please select host first.");
        }

        doDownloadAction(hostConfig, remoteFilePath, downloadDir);
        dispose();
    }

    private void doDownloadAction(HostConfig hostConfig, String remoteFilePath, VirtualFile downloadDir) {
        ProgressManager.getInstance().run(new Task.Backgroundable(this.project, "Download file") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    Session session = SSHUtil.createNewSession(hostConfig);
                    SSHUtil.downloadFile(session, remoteFilePath, downloadDir.getPath());

                    NotificationUtil.info(getProject(), "File will store at " + downloadDir.getPath());
                } catch (Exception e) {
                    NotificationUtil.error(this.getProject(), "Runtime error: " + e.getMessage());
                }
            }
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void open(Project project, VirtualFile selectDir) {
        this.project = project;
        this.targetDir = selectDir;

        Configuration configuration = ConfigurationUtil.loadFromDisk(project);
        List<HostConfig> hosts = configuration.getHostList();
        if (CollectionUtil.isEmpty(hosts)) {
            NotificationUtil.warn(project, "Please config hosts first.");
            return;
        }
        hosts.forEach(h -> hostList.addItem(h));

        setTitle("Download");
        pack();
        setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
        setVisible(true);
    }

}
