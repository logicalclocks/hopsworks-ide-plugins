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
import io.hops.cli.action.FileUploadAction;
import io.hops.cli.action.JobRunAction;
import io.hops.cli.config.HopsworksAPIConfig;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsRunJob extends AnAction {

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
        String destination=util.getDestination(proj);
        String userArgs=util.getUserArgs(proj);
        String localFilePath =e.getDataContext().getData("virtualFile").toString();
        String hopsProject=null;

        try {
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig( hopsworksApiKey, hopsworksUrl, projectName);
            //upload app first?
            FileUploadAction action = new FileUploadAction(hopsworksAPIConfig,destination,localFilePath);
            hopsProject = action.getProjectId(); //check if valid project,throws null pointer
            action.execute();
            //execute run job
            io.hops.cli.action.JobRunAction runJob=new io.hops.cli.action.JobRunAction(hopsworksAPIConfig,jobName,userArgs);
            if(!runJob.getJobExists()){ //check job exists
                PluginNoticifaction.notifyError(util.INVALID_JOBNAME+ jobName);
                return;
            }
            int status=runJob.execute();

            if (status == 200 || status == 201) {
                StringBuilder sb=new StringBuilder(" Job Started: ").append(jobName).append(" | Execution Id: ").append(runJob.getExecId());
                PluginNoticifaction.notify(e.getProject(),sb.toString());
            } else PluginNoticifaction.notify(e.getProject()," Job: "+jobName+" | Start Failed");

        } catch (IOException ex) {
            PluginNoticifaction.notifyError(ex.getMessage());
            Logger.getLogger(JobRunAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (NullPointerException nullPointerException) {
            if (hopsProject == null) {
                PluginNoticifaction.notifyError(util.INVALID_PROJECT);
                Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, nullPointerException.toString(), nullPointerException);
            } else {
                PluginNoticifaction.notifyError(nullPointerException.toString());
                Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, nullPointerException.toString(), nullPointerException);
            }
        } catch (Exception ex) {
            PluginNoticifaction.notify(ex.getMessage());
            Logger.getLogger(HopsRunJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

}
