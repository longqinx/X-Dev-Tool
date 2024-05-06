package com.longqinx.xdevtool.action.filetrans;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.longqinx.xdevtool.ui.UploadFileDialog;
import com.longqinx.xdevtool.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaos
 * @since 2023/12/4
 */
public class FileUploadAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (vFile == null)
            return;
        new UploadFileDialog().open(e.getProject(),vFile);
    }
}
