package com.longqinx.xdevtool.action;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.longqinx.xdevtool.common.Icons;
import com.longqinx.xdevtool.util.CompileOutputUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class CompiledResultLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiClass) || element.getContainingFile() == null) {
            return;
        }
        PsiFile compiledFile = CompileOutputUtil.getCompiledClassFileOf((PsiClass) element);
        if (compiledFile == null) {
            return;
        }
        List<PsiElement> navTargets = new ArrayList<>();
        navTargets.add(compiledFile);

        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                .create(Icons.LINE_MARKER)
                .setTargets(navTargets)
                .setTooltipText("Go to .class file ")
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setCellRenderer(() -> new DefaultPsiElementCellRenderer() {
                    @Override
                    protected Icon getIcon(PsiElement element) {
                        return Icons.LINE_MARKER;
                    }

                    @Override
                    public String getElementText(PsiElement element) {
                        return "Go to .class File : " + super.getElementText(element);
                    }
                });
        if (((PsiClass) element).getNameIdentifier() != null) {
            result.add(builder.createLineMarkerInfo(((PsiClass) element).getNameIdentifier()));
        }
    }
}
