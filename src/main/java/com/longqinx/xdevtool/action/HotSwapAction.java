package com.longqinx.xdevtool.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.longqinx.xdevtool.common.HotSwapContext;
import com.longqinx.xdevtool.ui.HotSwapConfirmDialog;
import com.longqinx.xdevtool.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class HotSwapAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(element instanceof PsiClass)) {
            NotificationUtil.error(e.getProject(), "Failed: not a java class file");
            return;
        }
        HotSwapContext context = new HotSwapContext(e.getProject(), (PsiClass) element);
        new HotSwapConfirmDialog(context).open();
    }
}
