package com.longqinx.xdevtool.common;

import com.intellij.ui.IconManager;

import javax.swing.*;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public interface Icons {
    String ICON_PATH = "icons/";

    Icon LINE_MARKER = IconManager.getInstance().getIcon(ICON_PATH + "line-marker.svg", Icons.class);
    Icon DOGE = IconManager.getInstance().getIcon(ICON_PATH + "doge.svg", Icons.class);
}
