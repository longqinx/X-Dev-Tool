package com.longqinx.xdevtool.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
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

public class UploadFileDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<HostConfig> hostList;
    private JTextArea commandText;
    private JTextField remoteDirectory;
    private Project project;
    private VirtualFile targetFile;

    public UploadFileDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        HostConfig hostConfig = (HostConfig) hostList.getSelectedItem();
        String remoteDir = remoteDirectory.getText();
        String commandsText = commandText.getText();
        if (hostConfig == null || remoteDir == null) {
            NotificationUtil.warn(this.project, "Unknown host or remote directory.");
            return;
        }
        String[] commandList = commandsText.split("\n");
        doUpload(hostConfig, targetFile, remoteDir, commandList);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void open(Project project, VirtualFile targetFile) {
        this.project = project;
        this.targetFile = targetFile;

        Configuration configuration = ConfigurationUtil.loadFromDisk(project);
        List<HostConfig> hosts = configuration.getHostList();
        if (CollectionUtil.isEmpty(hosts)) {
            NotificationUtil.warn(project, "Please config hosts first");
            return;
        }
        hosts.forEach(h -> hostList.addItem(h));

        setTitle("Upload");
        pack();
        setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
        setVisible(true);
    }

    private void doUpload(HostConfig hostConfig, VirtualFile srcFile, String targetDir, String[] commandList) {
        ProgressManager.getInstance().run(new Task.Backgroundable(this.project, "Upload file") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setText("Uploading...");
                    indicator.setFraction(0.3);

                    Session session = SSHUtil.createNewSession(hostConfig);
                    SSHUtil.exec(session, "mkdir -p " + targetDir);
                    SSHUtil.uploadFile(session, srcFile.getPath(), targetDir);


                    if (commandList != null) {
                        double inc = 0.7 / commandList.length;
                        for (int i = 0; i < commandList.length; i++) {
                            indicator.setText(String.format("Exec command %d", i + 1));
                            indicator.setFraction(indicator.getFraction() + inc);
                            SSHUtil.exec(session, commandList[i]);
                        }
                    }
                } catch (Exception e) {
                    NotificationUtil.error(getProject(), "Runtime error : " + e.getMessage());
                }
            }
        });
    }
}
