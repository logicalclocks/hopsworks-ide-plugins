package com.logicalclocks.actions;

//import io.hops.cli.action.JobLogsAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.logicalclocks.HopsPluginUtils;

import io.hops.cli.action.JobLogsAction;
import io.hops.cli.config.HopsworksAPIConfig;

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
        // TODO: insert action logic here
        // TODO: Caused by: org.apache.spark.SparkException: Exception thrown in awaitResult:
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
            if(userExecId!="" || userExecId!=null){
                logsJob = new JobLogsAction(hopsworksAPIConfig, jobName, userExecId);
            }else logsJob  = new JobLogsAction(hopsworksAPIConfig, jobName);


            int status=logsJob.execute();
            if (status == 200 || status == 201) {
                StringBuilder sb=new StringBuilder(jobName).append("_id").append(logsJob.getExecutionId()).append("_stdOut.log");
                StringBuilder sb2=new StringBuilder(jobName).append("_id").append(logsJob.getExecutionId()).append("_stdErr.log");
                //write logs
                writeFile(localPathLogs,sb.toString(),logsJob.getStdOut());
                writeFile(localPathLogs,sb2.toString(),logsJob.getStdErr());
                //notify
                StringBuilder sb3=new StringBuilder().append(" Job: ").append(jobName).append(" | Execution Id: ").append(logsJob.getExecutionId()).append(" | Logs downloaded");
                PluginNoticifaction.notify(e.getProject(),sb3.toString());
            } else PluginNoticifaction.notifyError(" Job: "+jobName+" | Get Logs Failed");

        } catch (IOException ex) {
            PluginNoticifaction.notifyError(ex.getMessage());
            Logger.getLogger(JobLogsAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception ex) {
            PluginNoticifaction.notifyError(ex.getMessage());
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
