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
import io.hops.cli.action.FileUploadAction;
import io.hops.cli.action.JobRunAction;
import io.hops.cli.action.SubmitFlinkJob;
import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Objects;
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
        String localFilePath = Objects.requireNonNull(e.getDataContext().getData("virtualFile")).toString();
        String hopsProject=null;
        String jobType = util.getUserJobType(e.getProject());
        String userExecutionId=util.getUserExecId(proj);
        int executionId=0;
        if(!userExecutionId.equals("")){
            try{
                executionId=Integer.parseInt(userExecutionId);
            } catch (NumberFormatException ex){
                PluginNoticifaction.notify(e.getProject(),"Not a valid number execution id; Skipped");
            }

        }

        try {
            int status;
            JobRunAction runJob;
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig( hopsworksApiKey, hopsworksUrl, projectName);
            FileUploadAction uploadAction = new FileUploadAction(hopsworksAPIConfig,destination,localFilePath);
            hopsProject = uploadAction.getProjectId(); //check if valid project,throws null pointer
            if (!jobType.equals(HopsPluginUtils.FLINK)){
                uploadAction.execute(); //upload app first if not flink
                runJob=new JobRunAction(hopsworksAPIConfig,jobName,userArgs);
                if(!runJob.getJobExists()){ //check job name exists
                    PluginNoticifaction.notifyError(e.getProject(), HopsPluginUtils.INVALID_JOBNAME + jobName);
                    return;
                }
                //execute run job
                status=runJob.execute();
                if (status == HttpStatus.SC_OK || status == HttpStatus.SC_CREATED) {
                    StringBuilder sb=new StringBuilder(" Job Submitted: ").append(jobName)
                            .append(" | Execution Id: ").append(runJob.getJsonResult().getInt("id"));
                    PluginNoticifaction.notify(e.getProject(),sb.toString());
                } else {
                    if(runJob.getJsonResult().containsKey("usrMsg"))
                        PluginNoticifaction.notify(e.getProject()," Job Submit Failed | "
                                +runJob.getJsonResult().getString("usrMsg"));
                    else PluginNoticifaction.notify(e.getProject()," Job: "+jobName+" | Submit Failed");
                }
            } else {
                SubmitFlinkJob submitFlinkJob = new SubmitFlinkJob(hopsworksAPIConfig, jobName);
                submitFlinkJob.setLocal_file_path(localFilePath);
                submitFlinkJob.setMainClass(util.getUserMainClass(proj));
                submitFlinkJob.setUserArgs(userArgs);
                submitFlinkJob.setUserExecId(executionId);
                 status=submitFlinkJob.execute();
                if (status== HttpStatus.SC_OK){
                    PluginNoticifaction.notify(e.getProject(),
                            "Flink job was submitted successfully, please check Hopsworks UI for progress.");
                }
            }
        } catch (IOException ex) {
            PluginNoticifaction.notifyError(e.getProject(),ex.getMessage());
            Logger.getLogger(JobRunAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (NullPointerException nullPointerException) {
            if (hopsProject == null) {
                PluginNoticifaction.notifyError(e.getProject(), HopsPluginUtils.INVALID_PROJECT);
            } else {
                PluginNoticifaction.notifyError(e.getProject(),nullPointerException.toString());
            }
            Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, nullPointerException.toString(), nullPointerException);
        } catch (Exception ex) {
            PluginNoticifaction.notify(e.getProject(),ex.getMessage());
            Logger.getLogger(HopsRunJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }


    }

}
