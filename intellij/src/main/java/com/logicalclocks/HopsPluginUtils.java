package com.logicalclocks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;

import net.minidev.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class HopsPluginUtils {
    public static final String SPARK="SPARK";
    public static final String PYSPARK="PYSPARK";
    public static final String FLINK="FLINK";
    public static final String PYTHON="PYTHON";
    public static final String PYTHON_MAIN="org.apache.spark.deploy.PythonRunner";
    public static final String PYTHON_CONFIG="pythonJobConfiguration";
    public static final String SPARK_CONFIG="sparkJobConfiguration";
    public static final String INVALID_PROJECT="Invalid Hopsworks Project Preferences";
    public static final String INVALID_JOBNAME="Invalid Job Name ";

    public static final String PATH_URL = "hops.url";
    public static final String PATH_KEY = "hops.key";
    public static final String PATH_PROJECT = "hops.project";
    public static final String PATH_FILE = "hops.file";
    public static final String PATH_JOB = "hops.job";
    public static final String PATH_PROGRAM = "hops.program";
    public static final String PATH_USERARGS = "hops.userAgrs";
    public static final String PATH_MAINCLASS = "hops.mainClass";
    public static final String PATH_EXECID = "hops.executionId";
    public static final String PATH_JOBTYPE="hops.jobType";
    //jobConfigs params
    public static final String PATH_SP_DRVERMEM = "hops.driverMem";
    public static final String PATH_SP_EXECMEM = "hops.executorMem";
    public static final String PATH_SP_EXEC_VC = "hops.executorVC";
    public static final String PATH_SP_DRIVER_VC = "hops.driverVC";
    public static final String PATH_SP_NUM_EXEC = "hops.numberExec";
    public static final String PATH_IS_SPARK_DYNAMIC = "hops.sparkDynamic";
    public static final String PATH_SP_INIT_EXEC = "hops.initialExecutor";
    public static final String PATH_SP_MAX_EXEC = "hops.maxExecutor";
    public static final String PATH_SP_MIN_EXEC = "hops.minExecutor";

    //python
    public static final String PATH_PY_MEMORY = "hops.memory";
    public static final String PATH_PY_CPU_CORE = "hops.cpuCore";
    //flink
    public static final String PATH_FL_JOB_MANAGER_MEM = "hops.jobManagerMem";
    public static final String PATH_FL_NUM_TASK_MANGER = "hops.numTaskManger";
    public static final String PATH_FL_TASK_MANAGER_MEM = "hops.taskManagerMem";
    public static final String PATH_FL_NUM_SLOTS = "hops.numSlots";
    //advance config
    public static final String PATH_IS_ADVANCED="hops.isAdvanced";
    public static final String PATH_ADDFILE = "hops.additionalFiles";
    public static final String PATH_PYTHON_DEPEND = "hops.pythonDependency";
    public static final String PATH_ARCHIVE = "hops.archive";
    public static final String PATH_ADDJAR = "hops.addJar";
    public static final String PATH_MORE_PROP = "hops.moreProp";
    //LABELS//
    public static final String ARCHIVES_LBL = "Archives";
    public static final String JARS_LBL = "Jars";
    public static final String FILES_LBL = "Files";
    public static final String PYTHON_LBL = "Python";
    public static final String MORE_PROP_LBL = "Properties";
    public static final String HDFS_LBL = "HDFS destination path";
    public static final String USER_ARGS_LBL = "User Arguments" ;
    public static final String MAIN_CLASS_LBL ="Main Class";
    public static final String DRIVER_MEM_LBL = "Driver Memory (MB)";
    public static final String EXECUTOR_MEM_LBL = "Executor Memory (MB)";
    public static final String EXEC_VC_LBL = "Executor Vrtual Cores";
    public static final String DRIVER_VC_LBL = "Driver Virtual Cores";
    public static final String NUM_EXEC_LBL = "Number of Executors";
    public static final String MEMORY_LBL = "Memory (MB)";
    public static final String CPU_LBL = "CPU Cores";
    public static final String JOB_MANAGER_MM_LBL = "Job Manager memory (MB)";
    public static final String NUM_TASK_LBL = "Number of Task Managers";
    public static final String TASK_MANAGER_MM_LBL = "Task Manager memory (MB)";
    public static final String NUM_SLOT_LBL = "Number of slots";
    public static final String ADVANCED_LBL = "Advanced Configurations";
    public static final String HOPS_PROJECT_LBL= "Hopsworks Project";
    public static final String HOPS_URL_LBL= "Hopsworks URL";
    public static final String HOPS_API_LBL= "Hopsworks API Key";
    public static final String JOB_NAME_LBL= "Job Name";
    public static final String LOG_PATH_LBL= "Local Path for Logs";
    public static final String EXEC_ID_LBL= "Execution Id";
    public static final String SPARK_BTN_LBL= "Spark ";
    public static final String PYTHON_BTN_LBL= "Python ";
    public static final String FLINK_BTN_LBL= "Flink ";
    public static final String JOB_TYPE_LBL= "Job Type ";
    public static final String MAX_EXEC_LBL = "Maximum Executors";
    public static final String INIT_EXEC_LBL = "Initial Executors";
    public static final String MIN_EXEC_LBL = "Minimum Executors";

    public static final String CONST_2048="2048";
    public static final String CONST_4096="4096";
    public static final String CONST_1024="1024";
    public static final String CONST_1="1";




    private final HashMap<String,Integer> jobTypeCode=new HashMap<String,Integer>();

    public HopsPluginUtils(){
        // Setting JOB TYPE //TO DO configurable job type
        /*
        UNUSED NOW
        jobTypeCode.put("jar",1);
        jobTypeCode.put("py",2);
        jobTypeCode.put("ipynb",2);
        */

    }

    public String getURL(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_URL);
    }

    public String getAPIKey(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_KEY);
    }

    public String getProjectName(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_PROJECT);
    }

    public String getLocalFile(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_FILE);
    }
    public String getJobName(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_JOB);
    }
    public String getDestination(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_PROGRAM);
    }

    public String getUserArgs(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_USERARGS);
    }

    public String getUserMainClass(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_MAINCLASS);
    }


    public String getUserExecId(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_EXECID);
    }

    public String getUserJobType(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_JOBTYPE);
    }
    //advance configs getters//
    public String getAdvancedArchive(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_ARCHIVE);
    }

    public String getAdvancedFiles(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_ADDFILE);
    }

    public String getPythonDependency(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_PYTHON_DEPEND);
    }

    public String getMoreProperties(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_MORE_PROP);
    }
    public String getAdvancedJars(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_ADDJAR);
    }
    public boolean isAdvanced(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getBoolean(PATH_IS_ADVANCED);
    }

    //advance config getters//

    //job config parameters getters//
    public String getDriverMemory(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_DRVERMEM);
    }
    public String getDriverVC(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_DRIVER_VC);
    }
    public String getExecutorMemory(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_EXECMEM);
    }
    public String getExecutorVC(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_EXEC_VC);
    }
    public String getNumberExecutor(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_NUM_EXEC);
    }
    public String getPythonMemory(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_PY_MEMORY);
    }
    public String getPythonCpuCores(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_PY_CPU_CORE);
    }
    public String getJobManagerMemory(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_FL_JOB_MANAGER_MEM);
    }
    public String getNumTaskManager(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_FL_NUM_TASK_MANGER);
    }
    public String getTaskManagerMemory(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_FL_TASK_MANAGER_MEM);
    }
    public String getNumberSlots(Project project){
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_FL_NUM_SLOTS);
    }


    public static boolean isSparkDynamic(Project project) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getBoolean(PATH_IS_SPARK_DYNAMIC);
    }

    public static String getInitExec(Project project) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_INIT_EXEC);
    }

    public static String getMaxExec(Project project) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_MAX_EXEC);
    }

    public static String getMinExec(Project project) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(PATH_SP_MIN_EXEC);
    }
    // job config parameters getters //



    /*
        UNUSED NOW
         */
    public String getJobType(String fileName){
        String ext= FilenameUtils.getExtension(fileName);
        Integer c=jobTypeCode.get(ext);
            switch (c.toString()) {
                case "1":
                    return SPARK;
                case "2":
                    return PYSPARK;
                case "3":
                    return FLINK;
                case "4":
                    return PYTHON;
                default:
                    return null;
            }
    }

        /*
        UNUSED NOW
         */
    public String getJobConfigType(String jobType){

        switch(jobType){
            case SPARK:
                return SPARK_CONFIG;
            case PYSPARK:
                return SPARK_CONFIG;
            case PYTHON:
                return PYTHON_CONFIG;
            default:
                return SPARK_CONFIG;
        }
    }


    public static JPanel createInputPanel(JPanel panel, LinkedHashMap<String, Component> map) {

        Set<Map.Entry<String, Component>> e2 = map.entrySet();
        Iterator<Map.Entry<String, Component>> it2 = e2.iterator();
        int j=0;
        while(it2.hasNext()){
            Map.Entry<String, Component> pair = it2.next();
            panel=createField(panel,j,pair.getKey(),pair.getValue());
            j++;
        }

        /*JPanel spacer = new JPanel();
        GridConstraints spacerConstraints = new GridConstraints();
        spacerConstraints.setRow(j);
        spacerConstraints.setFill(GridConstraints.FILL_BOTH);
        panel.add(spacer, spacerConstraints);
        */


        return panel;

    }

    public static JPanel createInputPanelJLabel(JPanel panel, LinkedHashMap<JLabel, Component> map) {

        Set<Map.Entry<JLabel, Component>> e2 = map.entrySet();
        Iterator<Map.Entry<JLabel, Component>> it2 = e2.iterator();
        int j = 0;
        while (it2.hasNext()) {
            Map.Entry<JLabel, Component> pair = it2.next();
            panel = createField(panel, j, pair.getKey(), pair.getValue());
            j++;
        }
        return panel;
    }

    public static JPanel createField(JPanel container, int row, String label, Component field ){

        if (container!=null){
            GridConstraints pathLabelConstraint = new GridConstraints();
            pathLabelConstraint.setRow(row);
            pathLabelConstraint.setColumn(0);
            pathLabelConstraint.setFill(GridConstraints.FILL_HORIZONTAL);
            pathLabelConstraint.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK);
            JLabel lbl = new JLabel(label,JLabel.LEFT);
            pathLabelConstraint.setIndent(5);
            lbl.setPreferredSize(new Dimension(190,1) );

          //  lbl.setHorizontalAlignment(JLabel.LEADING);
            container.add(lbl, pathLabelConstraint);

            GridConstraints pathFieldConstraint = new GridConstraints();
            pathFieldConstraint.setHSizePolicy(GridConstraints.SIZEPOLICY_WANT_GROW);
            pathFieldConstraint.setFill(GridConstraints.FILL_HORIZONTAL);
            pathFieldConstraint.setAnchor(GridConstraints.ALIGN_LEFT);
            pathFieldConstraint.setRow(row);
            pathFieldConstraint.setColumn(1);
            pathFieldConstraint.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK);
            //pathFieldConstraint.setIndent(10);

            container.add(field, pathFieldConstraint);

        }
        return container;

    }

    public static JPanel createField(JPanel container, int row, JLabel label, Component field ){

        if (container!=null){
            GridConstraints pathLabelConstraint = new GridConstraints();
            pathLabelConstraint.setRow(row);
            pathLabelConstraint.setColumn(0);
            pathLabelConstraint.setFill(GridConstraints.FILL_HORIZONTAL);
            pathLabelConstraint.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK);

            pathLabelConstraint.setIndent(5);
            label.setPreferredSize(new Dimension(190,1) );

            //  lbl.setHorizontalAlignment(JLabel.LEADING);
            container.add(label, pathLabelConstraint);

            GridConstraints pathFieldConstraint = new GridConstraints();
            pathFieldConstraint.setHSizePolicy(GridConstraints.SIZEPOLICY_WANT_GROW);
            pathFieldConstraint.setFill(GridConstraints.FILL_HORIZONTAL);
            pathFieldConstraint.setAnchor(GridConstraints.ALIGN_LEFT);
            pathFieldConstraint.setRow(row);
            pathFieldConstraint.setColumn(1);
            pathFieldConstraint.setVSizePolicy(GridConstraints.SIZEPOLICY_CAN_SHRINK);
            //pathFieldConstraint.setIndent(10);

            container.add(field, pathFieldConstraint);

        }
        return container;

    }






}
