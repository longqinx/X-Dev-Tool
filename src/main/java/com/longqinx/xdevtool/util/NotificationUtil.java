package com.longqinx.xdevtool.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

/**
 * @author xiaos
 * @since 2023/10/23
 */
public class NotificationUtil {
    private static final String GROUP_ID = "x-dev-tool-notification";
    private static final Notification INFO_NOTIFICATION = new Notification(GROUP_ID, "", NotificationType.INFORMATION);
    private static final Notification WARN_NOTIFICATION = new Notification(GROUP_ID, "", NotificationType.WARNING);
    private static final Notification ERROR_NOTIFICATION = new Notification(GROUP_ID, "", NotificationType.ERROR);

    public static void info(Project project, String message) {
        INFO_NOTIFICATION.setContent(message);
        INFO_NOTIFICATION.notify(project);
    }

    public static void warn(Project project, String message) {
        WARN_NOTIFICATION.setContent(message);
        WARN_NOTIFICATION.notify(project);
    }

    public static void error(Project project, String message) {
        ERROR_NOTIFICATION.setContent(message);
        ERROR_NOTIFICATION.notify(project);
    }
}
