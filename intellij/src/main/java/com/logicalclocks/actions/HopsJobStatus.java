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
import io.hops.cli.action.JobStatusAction;
import io.hops.cli.config.HopsworksAPIConfig;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsJobStatus extends AnAction {


    @Override
    public void update(AnActionEvent e) {
        // Set the availability if whether a project is open
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
        String userExecId=util.getUserExecId(proj);

        try {
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig( hopsworksApiKey, hopsworksUrl, projectName);
            io.hops.cli.action.JobStatusAction jobStatus = new io.hops.cli.action.JobStatusAction(hopsworksAPIConfig, jobName,userExecId);
            int run=jobStatus.execute();
            if(run==0){
                String[] arr=jobStatus.getJobStatusArr();
                StringBuilder sb=new StringBuilder("Job: ").append(jobName).append(" | Execution Id: ").append(jobStatus.getExecutionId()).append(" | State: ").append(arr[0]).append( " | Final Status: ").append(arr[1]);
                PluginNoticifaction.notify(e.getProject(),sb.toString());
            }else PluginNoticifaction.notifyError("Failed to get job status");

        } catch (IOException ex) {
            Logger.getLogger(io.hops.cli.action.JobStatusAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            PluginNoticifaction.notifyError(ex.getMessage());
        }catch (Exception ex) {
            Logger.getLogger(JobStatusAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            PluginNoticifaction.notifyError(ex.getMessage());
        }
    }
}
