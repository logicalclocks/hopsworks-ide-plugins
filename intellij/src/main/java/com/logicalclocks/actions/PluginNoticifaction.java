/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
