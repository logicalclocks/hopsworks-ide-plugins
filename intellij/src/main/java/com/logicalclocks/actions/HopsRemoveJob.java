package com.logicalclocks.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.logicalclocks.HopsPluginUtils;
//import io.hops.cli.action.JobRemoveAction;
import io.hops.cli.action.JobRemoveAction;
import io.hops.cli.config.HopsworksAPIConfig;

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

                if (status == 200 || status == 204 || status == 202) {
                    PluginNoticifaction.notify("Job: "+jobName+" | Deleted");
                } else PluginNoticifaction.notifyError("Job: "+jobName+" | Remove failed");

            } catch (IOException ex) {
                PluginNoticifaction.notifyError(ex.getMessage());
                Logger.getLogger(JobRemoveAction.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }catch (Exception ex) {

                Logger.getLogger(HopsRemoveJob.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                PluginNoticifaction.notifyError(ex.getMessage());
            }

    }
}
