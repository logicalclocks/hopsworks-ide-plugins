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
import io.hops.cli.action.JobCreateAction;
import io.hops.cli.config.HopsworksAPIConfig;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HopsCreateJob extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        // Set the availability if a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO : Check File Separator hardcoding
        HopsPluginUtils util = new HopsPluginUtils();
        Project proj = e.getProject();
        String hopsworksApiKey = util.getAPIKey(proj);
        String hopsworksUrl = util.getURL(proj);
        String projectName = util.getProjectName(proj);
        String jobName = util.getJobName(proj);
        String destination = util.getDestination(proj);
        String mainClass = util.getUserMainClass(proj);
        String userArgs = util.getUserArgs(proj);
        String localFilePath = e.getDataContext().getData("virtualFile").toString();

        File file = new File(localFilePath);
        String finalPath = destination + '/' + file.getName();
        String jobType = util.getUserJobType(e.getProject());
        String hopsProject = null;

        try {
            HopsworksAPIConfig hopsworksAPIConfig = new HopsworksAPIConfig(hopsworksApiKey, hopsworksUrl, projectName);
            //upload program
            FileUploadAction action = new FileUploadAction(hopsworksAPIConfig, destination, localFilePath);
            hopsProject = action.getProjectId(); //check if valid project,throws null pointer

            action.execute();
            //set program configs
            io.hops.cli.action.JobCreateAction.Args args = new io.hops.cli.action.JobCreateAction.Args();
            args.setMainClass(mainClass); //set user provides,overridden by inspect job config
            args.setAppPath(finalPath); //full app path
            args.setJobType(jobType);  // spark/pyspark/python
            args.setCommandArgs(userArgs.trim());
            if (jobType.equals(HopsPluginUtils.SPARK)) {
                args.setDriverMemInMbs(Integer.parseInt(util.getDriverMemory(proj)));
                args.setDriverVC(Integer.parseInt(util.getDriverVC(proj)));
                args.setExecutorMemInMbs(Integer.parseInt(util.getExecutorMemory(proj)));
                args.setExecutorVC(Integer.parseInt(util.getExecutorVC(proj)));
                args.setDynamic(util.isSparkDynamic(proj));
                if (!util.isSparkDynamic(proj))
                    args.setNumExecutors(Integer.parseInt(util.getNumberExecutor(proj)));
                else {
                    args.setInitExecutors(Integer.parseInt(util.getInitExec(proj)));
                    args.setMaxExecutors(Integer.parseInt(util.getMaxExec(proj)));
                    args.setMinExecutors(Integer.parseInt(util.getMinExec(proj)));
                }
                if (util.isAdvanced(proj)) {
                    args.setAdvanceConfig(true);
                    args.setArchives(util.getAdvancedArchive(proj));
                    args.setFiles(util.getAdvancedFiles(proj));
                    args.setPythonDependency(util.getPythonDependency(proj));
                    args.setJars(util.getAdvancedJars(proj));
                    args.setProperties(util.getMoreProperties(proj));
                }
            } else if (jobType.equals(HopsPluginUtils.PYTHON)) {
                args.setPythonMemory(Integer.parseInt(util.getPythonMemory(proj)));
                args.setCpusCores(Integer.parseInt(util.getPythonCpuCores(proj)));
                if (util.isAdvanced(proj)) {
                    args.setAdvanceConfig(true);
                    args.setFiles(util.getAdvancedFiles(proj));
                }
            } else if (jobType.equals(HopsPluginUtils.FLINK)) {
                args.setJobManagerMemory(Integer.parseInt(util.getJobManagerMemory(proj)));
                args.setTaskManagerMemory(Integer.parseInt(util.getTaskManagerMemory(proj)));
                args.setNumTaskManager(Integer.parseInt(util.getNumTaskManager(proj)));
                args.setNumSlots(Integer.parseInt(util.getNumberSlots(proj)));
                if (util.isAdvanced(proj)) {
                    args.setAdvanceConfig(true);
                    args.setProperties(util.getMoreProperties(proj));
                }
            }

            // create job
            io.hops.cli.action.JobCreateAction createJob = new io.hops.cli.action.JobCreateAction(hopsworksAPIConfig, jobName, args);
            int status = createJob.execute();

            if (status == 200 || status == 201) {
                PluginNoticifaction.notify(e.getProject(), "Job Created: " + jobName);
            } else PluginNoticifaction.notify(e.getProject(), "Job Creation Failed: " + jobName);

        } catch (IOException ioException) {
            PluginNoticifaction.notifyError(e.getProject(),ioException.getMessage());
            Logger.getLogger(JobCreateAction.class.getName()).log(Level.SEVERE, ioException.getMessage(), ioException);
        } catch (NullPointerException nullPointerException) {
            if (hopsProject == null) {
                PluginNoticifaction.notifyError( e.getProject(),util.INVALID_PROJECT);
                Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, nullPointerException.toString(), nullPointerException);
            } else {
                PluginNoticifaction.notifyError(e.getProject(),nullPointerException.toString());

            }
            Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, nullPointerException.toString(), nullPointerException);
        } catch (Exception exception) {
            PluginNoticifaction.notifyError(e.getProject(),exception.toString());
            Logger.getLogger(HopsCreateJob.class.getName()).log(Level.SEVERE, exception.getMessage(), exception);
        }


    }
}
