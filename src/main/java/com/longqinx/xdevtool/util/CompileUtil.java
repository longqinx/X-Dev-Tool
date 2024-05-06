package com.longqinx.xdevtool.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.task.ProjectTaskManager;
import org.jetbrains.concurrency.Promise;

import java.util.function.Consumer;

/**
 * @author xiaos
 * @since 2023/10/25
 */
public class CompileUtil {
    public static void compile(Project project, VirtualFile virtualFile, Consumer<ProjectTaskManager.Result> successAction, String failedMsg) {
        ProjectTaskManager instance = ProjectTaskManager.getInstance(project);
        Promise<ProjectTaskManager.Result> compileResult = instance.compile(virtualFile);
        compileResult.onSuccess(successAction);
        compileResult.onError(e -> NotificationUtil.warn(project, failedMsg));
    }
}
