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
import com.logicalclocks.PluginNoticifaction;
import io.hops.cli.action.JobRemoveAction;
import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsRemoveJob extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        // Set the availability if a project is open
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
                JobRemoveAction rmJob = new JobRemoveAction(hopsworksAPIConfig, jobName);
                int status=rmJob.execute();

                if (status == HttpStatus.SC_OK || status == HttpStatus.SC_NO_CONTENT || status == HttpStatus.SC_ACCEPTED) {
                    PluginNoticifaction.notify(e.getProject(),"Job: "+jobName+" | Deleted");
                } else {
                    if (rmJob.getJsonResult().containsKey("usrMsg"))
                        PluginNoticifaction.notify(e.getProject()," Job Remove Failed | "
                                +rmJob.getJsonResult().getString("usrMsg"));

                    else PluginNoticifaction.notifyError(e.getProject(),"Job: "+jobName+" | Remove failed");
                }

            } catch (IOException ex) {
                PluginNoticifaction.notifyError(e.getProject(),ex.getMessage());
                Logger.getLogger(JobRemoveAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }catch (Exception ex) {

                Logger.getLogger(HopsRemoveJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                PluginNoticifaction.notifyError(e.getProject(),ex.getMessage());
            }

    }
}
