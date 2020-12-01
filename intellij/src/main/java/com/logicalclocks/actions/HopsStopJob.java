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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.logicalclocks.HopsPluginUtils;
import io.hops.cli.action.JobStopAction;
import io.hops.cli.config.HopsworksAPIConfig;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsStopJob extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        HopsPluginUtils util=new HopsPluginUtils();
        Project proj=e.getProject();
        String hopsworksApiKey = util.getAPIKey(proj);
        String hopsworksUrl = util.getURL(proj);
        String projectName = util.getProjectName(proj);
        String jobName=util.getJobName(proj);

        try {
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig( hopsworksApiKey, hopsworksUrl, projectName);
            JobStopAction stopJob=new JobStopAction(hopsworksAPIConfig,jobName);
            int status=stopJob.execute();

            if (status == 200 || status == 201 || status == 202) {
                PluginNoticifaction.notify(e.getProject(),"Job: "+jobName +" | Stopped");
            }  else PluginNoticifaction.notifyError("Job: "+jobName +" | Stop failed");

        } catch (IOException ex) {
            PluginNoticifaction.notifyError(ex.getMessage());
            Logger.getLogger(HopsStopJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception ex) {
            PluginNoticifaction.notifyError(ex.getMessage());
            Logger.getLogger(HopsStopJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

    }
}
