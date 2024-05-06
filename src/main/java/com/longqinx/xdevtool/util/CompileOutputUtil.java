package com.longqinx.xdevtool.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class CompileOutputUtil {

    public static String getBaseOutputPath(PsiElement element) {
        Module module = ModuleUtil.findModuleForPsiElement(element);
        if (module == null) {
            return null;
        }
        CompilerModuleExtension moduleExtension = CompilerModuleExtension.getInstance(module);
        if (moduleExtension == null) {
            return null;
        }
        VirtualFile outputPath = moduleExtension.getCompilerOutputPath();
        if (outputPath == null) {
            return null;
        }
        return outputPath.getPath() + "/";
    }

    public static PsiFile getCompiledClassFileOf(PsiClass psiClass) {
        String basePath = getBaseOutputPath(psiClass);
        String qualifiedName = psiClass.getQualifiedName();
        if (basePath == null || qualifiedName == null) {
            return null;
        }
        String compiledFilePath = basePath + qualifiedName.replaceAll("\\.", "/") + ".class";
        VirtualFile classFile = LocalFileSystem.getInstance().findFileByPath(compiledFilePath);
        return classFile == null ? null : PsiManager.getInstance(psiClass.getProject()).findFile(classFile);
    }
}
