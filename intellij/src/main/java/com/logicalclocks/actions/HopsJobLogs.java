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
import io.hops.cli.action.JobLogsAction;
import io.hops.cli.config.HopsworksAPIConfig;
import org.apache.http.HttpStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsJobLogs extends AnAction {

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
        String localPathLogs=util.getLocalFile(proj);
        String jobName=util.getJobName(proj);
        String userExecId=util.getUserExecId(proj);

        try {

            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig( hopsworksApiKey, hopsworksUrl, projectName);
            JobLogsAction logsJob;
            if(!userExecId.equals("")){
                logsJob = new JobLogsAction(hopsworksAPIConfig, jobName, userExecId);
            }else logsJob  = new JobLogsAction(hopsworksAPIConfig, jobName);


            int status=logsJob.execute();
            if (status == HttpStatus.SC_OK || status == HttpStatus.SC_CREATED) {
                StringBuilder sb=new StringBuilder(jobName).append("_id").append(logsJob.getExecutionId()).append("_stdOut.log");
                StringBuilder sb2=new StringBuilder(jobName).append("_id").append(logsJob.getExecutionId()).append("_stdErr.log");
                //write logs
                writeFile(localPathLogs,sb.toString(),logsJob.getStdOut());
                writeFile(localPathLogs,sb2.toString(),logsJob.getStdErr());
                //notify
                StringBuilder sb3=new StringBuilder().append(" Job: ").append(jobName).append(" | Execution Id: ").append(logsJob.getExecutionId()).append(" | Logs downloaded");
                PluginNoticifaction.notify(e.getProject(),sb3.toString());
            }else{
                if (logsJob.getJsonResult().containsKey("usrMsg"))
                    PluginNoticifaction.notify(e.getProject(),"Get Logs Failed | "+logsJob.getJsonResult().getString("usrMsg"));

                 else PluginNoticifaction.notifyError(e.getProject()," Job: "+jobName+" | Get Logs Failed");
            }


        } catch (IOException ex) {
            PluginNoticifaction.notifyError(e.getProject(),ex.getMessage());
            Logger.getLogger(JobLogsAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception ex) {
            PluginNoticifaction.notifyError(e.getProject(),ex.getMessage());
            Logger.getLogger(HopsJobLogs.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    public void writeFile (String logPath,String filename,String content) throws IOException{
        File out=new File(logPath,filename);
        BufferedWriter bw=new BufferedWriter(new FileWriter(out));
        bw.write(content);
        bw.close();
    }

}
