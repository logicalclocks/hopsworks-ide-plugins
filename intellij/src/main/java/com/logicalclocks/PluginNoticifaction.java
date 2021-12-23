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

package com.logicalclocks;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.sun.istack.Nullable;

import static com.intellij.notification.NotificationType.*;


/**
 * Creates notification in IDE
 */

public class PluginNoticifaction {

    private static final String TITLE = "Hopsworks plugin";
    private static final String GROUP_ID = "Hopsworks Notification";

    public static void notifyError(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(GROUP_ID)
                .createNotification(TITLE, content, ERROR)
                .notify(project);
    }

    public static void notify(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(GROUP_ID)
                .createNotification(TITLE, content, INFORMATION)
                .notify(project);
    }


}
