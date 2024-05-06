package com.longqinx.xdevtool.action;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.longqinx.xdevtool.common.Icons;
import com.longqinx.xdevtool.ui.XDevToolWindowUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class ConfigToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(Icons.DOGE);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(createPanel(project), null, false);
        contentManager.addContent(content);
    }

    private JComponent createPanel(Project project) {
        return new XDevToolWindowUI(project).getContent();
    }
}
