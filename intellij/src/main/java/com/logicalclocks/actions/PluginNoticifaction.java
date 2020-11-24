package com.logicalclocks.actions;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

/**
 * Creates notification in IDE
 */

public class PluginNoticifaction {


private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Hopsworks Plugin", NotificationDisplayType.BALLOON, true);


public static Notification notify(Project project, String content) {

    final Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION);
    Notifications.Bus.notify(notification,project);
    return notification;
}

public static Notification notifyError(String content) {

    final Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR);
    Notifications.Bus.notify(notification,null);
    return notification;
}

public static Notification notify(String content) {
        return notify(null, content);
    }

}
