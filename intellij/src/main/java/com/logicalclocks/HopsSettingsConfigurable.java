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

package com.logicalclocks;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;

import static com.logicalclocks.HopsPluginUtils.*;

public class HopsSettingsConfigurable implements Configurable {

    // stored values variables
    private static String storedUrl = null, storedKey = null, storedProject = null,
            storedLogFile = null, storedJob = null, storedProgram = null, storedJobType = null,
            storedUserArgs = null, storedMainClass = null, storedExecId = null;
    //advanced configs
    private static String storedAddFile = null, storedAddJar = null, storedPythonDepend = null,
            storedArchive = null, storedMoreProp = null;
    private static boolean isAdvancedConfig = false, isSparkDynamic = false;
    //spark job config params
    private static String stored_spDriverMem = null, stored_spExecMem = null, stored_spExecVC = null,
            stored_spDriverVC = null, stored_spNumExec = null, stored_spInitExec = null,
            stored_spMaxExec = null, stored_spMinExec = null;

    //python job config params
    private static String stored_pyMemory = null, stored_pyCpuCore = null;
    //flink job config params
    private static String stored_flJobManMem = null, stored_flNumTaskMan = null, stored_flTaskManMem = null,
            stored_flNumSlots = null;
    private final Project project;
    //buttons
    ButtonGroup group = new ButtonGroup();
    JRadioButton sparkBtn = new JRadioButton(), flinkBtn = new JRadioButton(), pythonBtn = new JRadioButton();
    JCheckBox advanceBtn = new JCheckBox(), dynamicBtn = new JCheckBox();
    //hashmaps to hold UI elements
    LinkedHashMap<JLabel, Component> sparkConfigMap = new LinkedHashMap<>();
    LinkedHashMap<String, Component> constantFieldsMap = new LinkedHashMap<>(), advanceFieldmap = new LinkedHashMap<>(),
            flinkConfigMap = new LinkedHashMap<>(), pythonConfigMap = new LinkedHashMap<>(),
            sparkAddInputs = new LinkedHashMap<>(), pythonAddInputs = new LinkedHashMap<>(),
            flinkAddInputs = new LinkedHashMap<>();

    int layoutVGAP = 2;
    private JTextField userKey = new JTextField();
    private JTextField userUrl = new JTextField();
    private JTextField userProject = new JTextField();
    private JTextField logFilePath = new JTextField();
    private JTextField jobName = new JTextField();
    private JTextField hdfsPath = new JTextField();
    private JTextField userArgs = new JTextField();
    private JTextField mainClass = new JTextField();
    private JTextField execId = new JTextField();
    //advance config
    private JTextField additionalFiles = new JTextField();
    private JTextField archives = new JTextField();
    private JTextField additionalJars = new JTextField();
    private JTextField pythonDepend = new JTextField();
    private JTextArea moreProps = new JTextArea();
    //job config params
    private JTextField driverMem = new JTextField(CONST_2048);
    private JTextField executorMem = new JTextField(CONST_4096);
    private JTextField execVC = new JTextField(CONST_1);
    private JTextField driverVC = new JTextField(CONST_1);
    private JTextField numExecutor = new JTextField(CONST_1);
    private JTextField initExecutor = new JTextField(CONST_1);
    private JTextField minExecutor = new JTextField(CONST_1);
    private JTextField maxExecutor = new JTextField(CONST_1);
    //python configs
    private JTextField memory = new JTextField(CONST_2048);
    private JTextField cpuCore = new JTextField(CONST_1);
    //flink configs
    private JTextField jobManagerMem = new JTextField(CONST_1024);
    private JTextField numTaskManager = new JTextField(CONST_1);
    private JTextField taskManagerMem = new JTextField(CONST_1024);
    private JTextField numSlots = new JTextField(CONST_1);
    //JLabels
    private JLabel numExecutorsLabel = new JLabel(NUM_EXEC_LBL);
    private JLabel initExecutorsLabel = new JLabel(INIT_EXEC_LBL);
    private JLabel minExecutorsLabel = new JLabel(MIN_EXEC_LBL);
    private JLabel maxExecutorLabel = new JLabel(MAX_EXEC_LBL);
    private JPanel additionalInputPanel = null, advanceInputPanel = null, superPanel = null, jobConfigPanel = null;

    /**
     * Listener for advanced config selected
     * resets individual panels according to selection
     */
    ActionListener advanceAction = e -> {
        if (e.getSource() == advanceBtn) {
            advanceInputPanel.removeAll();
            if (advanceBtn.isSelected()) {
                if (sparkBtn.isSelected()) {
                    advanceInputPanel = createInputPanel(advanceInputPanel, advanceFieldmap);
                } else if (pythonBtn.isSelected()) {
                    advanceInputPanel = createField(advanceInputPanel, 0, HopsPluginUtils.FILES_LBL, advanceFieldmap.get(HopsPluginUtils.FILES_LBL));
                } else if (flinkBtn.isSelected()) {
                    advanceInputPanel = createField(advanceInputPanel, 0, HopsPluginUtils.MORE_PROP_LBL, advanceFieldmap.get(HopsPluginUtils.MORE_PROP_LBL));
                }
                advanceInputPanel.setVisible(true);
            } else { //check box disable
                advanceInputPanel.setVisible(false);
            }

            superPanel.updateUI();
        }
    };

    /**
     * Listener for spark dynamic
     * resets individual panels according to selection
     */
    ActionListener dynamicAction = actionEvent -> {
        if (dynamicBtn.isSelected()) {
            initExecutor.setVisible(true);
            maxExecutor.setVisible(true);
            initExecutorsLabel.setVisible(true);
            maxExecutorLabel.setVisible(true);
            minExecutor.setVisible(true);
            minExecutorsLabel.setVisible(true);
            numExecutorsLabel.setVisible(false);
            numExecutor.setVisible(false);
        } else {
            initExecutor.setVisible(false);
            maxExecutor.setVisible(false);
            initExecutorsLabel.setVisible(false);
            maxExecutorLabel.setVisible(false);
            minExecutor.setVisible(false);
            minExecutorsLabel.setVisible(false);
            numExecutorsLabel.setVisible(true);
            numExecutor.setVisible(true);
        }
        jobConfigPanel.updateUI();
    };
    /**
     * Listener for flink selected
     * resets individual panels according to selection
     */
    ActionListener flinkAction = e -> {
        if (e.getSource() == flinkBtn) {
            additionalInputPanel.removeAll();
            additionalInputPanel = createInputPanel(additionalInputPanel, flinkAddInputs);
            jobConfigPanel.removeAll();
            jobConfigPanel = createInputPanel(jobConfigPanel, flinkConfigMap);
            advanceBtn.setSelected(false);
            advanceInputPanel.removeAll();
            advanceInputPanel.setVisible(false);
            superPanel.updateUI();
        }
    };
    /**
     * Listener for spark selected
     * resets individual panels according to selection
     */
    ActionListener sparkAction = e -> {
        if (e.getSource() == sparkBtn) {
            additionalInputPanel.removeAll();
            additionalInputPanel = createInputPanel(additionalInputPanel, sparkAddInputs);
            jobConfigPanel.removeAll();
            jobConfigPanel = createInputPanelJLabel(jobConfigPanel, sparkConfigMap);
            advanceBtn.setSelected(false);
            advanceInputPanel.removeAll();
            advanceInputPanel.setVisible(false);
            additionalInputPanel.setVisible(true);
            jobConfigPanel.setVisible(true);
            superPanel.updateUI();
        }
    };
    /**
     * Listener for python selected
     * resets individual panels according to selection
     */
    ActionListener pythonAction = e -> {
        if (e.getSource() == pythonBtn) {
            additionalInputPanel.removeAll();
            additionalInputPanel = createInputPanel(additionalInputPanel, pythonAddInputs);
            jobConfigPanel.removeAll();
            jobConfigPanel = createInputPanel(jobConfigPanel, pythonConfigMap);
            advanceBtn.setSelected(false);
            advanceInputPanel.removeAll();
            advanceInputPanel.setVisible(false);
            additionalInputPanel.setVisible(true);
            jobConfigPanel.setVisible(true);
            superPanel.updateUI();
        }
    };


    public HopsSettingsConfigurable(Project project) {
        this.project = project;
        if (project != null) {
            loadProperties(project);
        }
    }

    /**
     * Reads variables from project scope
     * @param project
     */
    private static void loadProperties(Project project) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        storedUrl = properties.getValue(HopsPluginUtils.PATH_URL);
        storedKey = properties.getValue(HopsPluginUtils.PATH_KEY);
        storedProject = properties.getValue(HopsPluginUtils.PATH_PROJECT);
        storedLogFile = properties.getValue(HopsPluginUtils.PATH_FILE);
        storedJob = properties.getValue(HopsPluginUtils.PATH_JOB);
        storedProgram = properties.getValue(HopsPluginUtils.PATH_PROGRAM);
        storedUserArgs = properties.getValue(HopsPluginUtils.PATH_USERARGS);
        storedMainClass = properties.getValue(HopsPluginUtils.PATH_MAINCLASS);
        storedExecId = properties.getValue(HopsPluginUtils.PATH_EXECID);
        storedJobType = properties.getValue(PATH_JOBTYPE);
        if (storedJobType == null) storedJobType = SPARK;
        //spark configs
        stored_spDriverMem = properties.getValue(PATH_SP_DRVERMEM);
        stored_spNumExec = properties.getValue(PATH_SP_NUM_EXEC);
        stored_spExecVC = properties.getValue(PATH_SP_EXEC_VC);
        stored_spDriverVC = properties.getValue(PATH_SP_DRIVER_VC);
        stored_spExecMem = properties.getValue(PATH_SP_EXECMEM);
        isSparkDynamic = properties.getBoolean(PATH_IS_SPARK_DYNAMIC);
        stored_spInitExec = properties.getValue(PATH_SP_INIT_EXEC);
        stored_spMaxExec = properties.getValue(PATH_SP_MAX_EXEC);
        stored_spMinExec = properties.getValue(PATH_SP_MIN_EXEC);

        //python
        stored_pyMemory = properties.getValue(PATH_PY_MEMORY);
        stored_pyCpuCore = properties.getValue(PATH_PY_CPU_CORE);
        //flink
        storedUserArgs = properties.getValue(HopsPluginUtils.PATH_USERARGS);
        storedMainClass = properties.getValue(HopsPluginUtils.PATH_MAINCLASS);
        stored_flTaskManMem = properties.getValue(PATH_FL_TASK_MANAGER_MEM);
        stored_flNumSlots = properties.getValue(PATH_FL_NUM_SLOTS);
        stored_flJobManMem = properties.getValue(PATH_FL_JOB_MANAGER_MEM);
        stored_flNumTaskMan = properties.getValue(PATH_FL_NUM_TASK_MANGER);
        //advanced
        storedAddFile = properties.getValue(PATH_ADDFILE);
        storedAddJar = properties.getValue(PATH_ADDJAR);
        storedPythonDepend = properties.getValue(PATH_PYTHON_DEPEND);
        storedMoreProp = properties.getValue(PATH_MORE_PROP);
        storedArchive = properties.getValue(PATH_ARCHIVE);
        isAdvancedConfig = properties.getBoolean(PATH_IS_ADVANCED);

    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Hopsworks Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        initPanelMaps(); //init hash maps
        numberChecker(driverVC);
        numberChecker(executorMem);
        numberChecker(execVC);
        numberChecker(cpuCore);
        numberChecker(numExecutor);
        numberChecker(numSlots);
        numberChecker(numTaskManager);
        numberChecker(taskManagerMem);
        numberChecker(jobManagerMem);
        numberChecker(memory);
        numberChecker(initExecutor);
        numberChecker(minExecutor);
        numberChecker(maxExecutor);

        //add primary input text field panel
        GridLayoutManager layout = new GridLayoutManager(constantFieldsMap.size() + 1, 2);
        layout.setVGap(layoutVGAP);
        Insets margin = JBUI.insetsTop(5);
        layout.setMargin(margin);
        JPanel inputPanel = new JPanel(layout);
        Border br = BorderFactory.createTitledBorder("Primary Inputs");
        inputPanel.setBorder(br);
        inputPanel = HopsPluginUtils.createInputPanel(inputPanel, constantFieldsMap);

        //radio buttons
        sparkBtn.setText(SPARK_BTN_LBL);
        pythonBtn.setText(PYTHON_BTN_LBL);
        flinkBtn.setText(FLINK_BTN_LBL);
        group.add(sparkBtn);
        group.add(pythonBtn);
        group.add(flinkBtn);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createTitledBorder(JOB_TYPE_LBL));
        sparkBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        pythonBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        flinkBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonPanel.add(sparkBtn);
        buttonPanel.add(pythonBtn);
        buttonPanel.add(flinkBtn);
        //add listeners
        flinkBtn.addActionListener(flinkAction);
        sparkBtn.addActionListener(sparkAction);
        pythonBtn.addActionListener(pythonAction);

        //additional input fields panel
        GridLayoutManager lAdd2 = new GridLayoutManager(sparkAddInputs.size() + 1, 2);
        lAdd2.setVGap(layoutVGAP);
        lAdd2.setMargin(margin);
        additionalInputPanel = new JPanel(lAdd2);
        Border br2 = BorderFactory.createTitledBorder("Job Type Specific Inputs");
        additionalInputPanel.setBorder(br2);
        //job config parameters panel
        GridLayoutManager layout3 = new GridLayoutManager(sparkConfigMap.size() + 1, 2);
        layout3.setVGap(layoutVGAP);
        layout3.setMargin(margin);
        jobConfigPanel = new JPanel(layout3);
        Border br3 = BorderFactory.createTitledBorder("Additional Job Configurations");
        jobConfigPanel.setBorder(br3);
        // init logic for spark dynamic checkbox
        if (dynamicBtn.isSelected()) {
            initExecutor.setVisible(true);
            initExecutorsLabel.setVisible(true);
            maxExecutor.setVisible(true);
            maxExecutorLabel.setVisible(true);
            minExecutor.setVisible(true);
            minExecutorsLabel.setVisible(true);
            numExecutorsLabel.setVisible(false);
            numExecutor.setVisible(false);
        } else {
            initExecutor.setVisible(false);
            initExecutorsLabel.setVisible(false);
            maxExecutor.setVisible(false);
            maxExecutorLabel.setVisible(false);
            minExecutor.setVisible(false);
            minExecutorsLabel.setVisible(false);
            numExecutorsLabel.setVisible(true);
            numExecutor.setVisible(true);
        }

        //add advanced checkbox button
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.X_AXIS));
        advanceBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        advanceBtn.setText(HopsPluginUtils.ADVANCED_LBL);
        advanceBtn.setSelected(isAdvancedConfig);
        advanceBtn.addActionListener(advanceAction);
        buttonPanel2.add(advanceBtn);
        //advanced config panel
        GridLayoutManager layout2 = new GridLayoutManager(advanceFieldmap.size() + 1, 2);
        layout2.setVGap(layoutVGAP);
        layout2.setMargin(margin);
        advanceInputPanel = new JPanel(layout2);
        Border br4 = BorderFactory.createTitledBorder("Advanced Configurations");
        advanceInputPanel.setBorder(br4);


        // update panel fields as per current job type
        if (storedJobType == null || storedJobType.equals(HopsPluginUtils.SPARK)) {
            additionalInputPanel = createInputPanel(additionalInputPanel, sparkAddInputs);
            jobConfigPanel = createInputPanelJLabel(jobConfigPanel, sparkConfigMap);
            if (isAdvancedConfig) {
                advanceInputPanel = HopsPluginUtils.createInputPanel(advanceInputPanel, advanceFieldmap);
            }
        } else if (storedJobType.equals(HopsPluginUtils.PYTHON)) {
            additionalInputPanel = createInputPanel(additionalInputPanel, pythonAddInputs);
            jobConfigPanel = HopsPluginUtils.createInputPanel(jobConfigPanel, pythonConfigMap);
            if (isAdvancedConfig)
                advanceInputPanel = createField(advanceInputPanel, 0, HopsPluginUtils.FILES_LBL, advanceFieldmap.get(HopsPluginUtils.FILES_LBL));
        } else {
            additionalInputPanel = createInputPanel(additionalInputPanel, flinkAddInputs);
            jobConfigPanel = HopsPluginUtils.createInputPanel(jobConfigPanel, flinkConfigMap);
            if (isAdvancedConfig)
                advanceInputPanel = createField(advanceInputPanel, 0, HopsPluginUtils.MORE_PROP_LBL, advanceFieldmap.get(HopsPluginUtils.MORE_PROP_LBL));
        }


        // add all panels
        superPanel = new JPanel();
        superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.Y_AXIS));
        superPanel.setAlignmentX(BoxLayout.X_AXIS);
        superPanel.add(buttonPanel); //job type buttons
        superPanel.add(inputPanel); // primary input fields
        superPanel.add(additionalInputPanel); // job config specific inputs 2nd panel
        superPanel.add(jobConfigPanel); // job config parameters 3rd panel
        superPanel.add(buttonPanel2); // checkbox
        advanceInputPanel.setVisible(isAdvancedConfig); // advanced configs
        superPanel.add(advanceInputPanel);

        return superPanel;

    }

    /**
     * Initialise fields into hash map which is used while
     * creating JPanels
     */
    public void initPanelMaps() {

        //main input panel
        constantFieldsMap.put(HOPS_PROJECT_LBL, userProject);
        constantFieldsMap.put(HOPS_URL_LBL, userUrl);
        constantFieldsMap.put(HOPS_API_LBL, userKey);
        constantFieldsMap.put(JOB_NAME_LBL, jobName);
        constantFieldsMap.put(LOG_PATH_LBL, logFilePath);
        constantFieldsMap.put(EXEC_ID_LBL, execId);
        //spark additional input
        sparkAddInputs.put(HopsPluginUtils.HDFS_LBL, hdfsPath);
        sparkAddInputs.put(HopsPluginUtils.USER_ARGS_LBL, userArgs);
        sparkAddInputs.put(HopsPluginUtils.MAIN_CLASS_LBL, mainClass);
        //python additional input
        pythonAddInputs.put(HopsPluginUtils.HDFS_LBL, hdfsPath);
        pythonAddInputs.put(HopsPluginUtils.USER_ARGS_LBL, userArgs);
        // spark configs
        if (isSparkDynamic) dynamicBtn.setSelected(true);
        sparkConfigMap.put(new JLabel("Spark Dynamic "), dynamicBtn);
        sparkConfigMap.put(new JLabel(DRIVER_MEM_LBL), driverMem);
        sparkConfigMap.put(new JLabel(EXECUTOR_MEM_LBL), executorMem);
        sparkConfigMap.put(new JLabel(EXEC_VC_LBL), execVC);
        sparkConfigMap.put(new JLabel(DRIVER_VC_LBL), driverVC);
        sparkConfigMap.put(numExecutorsLabel, numExecutor);
        dynamicBtn.addActionListener(dynamicAction);
        sparkConfigMap.put(initExecutorsLabel, initExecutor);
        sparkConfigMap.put(minExecutorsLabel, minExecutor);
        sparkConfigMap.put(maxExecutorLabel, maxExecutor);
        // python configs
        pythonConfigMap.put(HopsPluginUtils.MEMORY_LBL, memory);
        pythonConfigMap.put(HopsPluginUtils.CPU_LBL, cpuCore);
        // flink configs
        flinkAddInputs.put(MAIN_CLASS_LBL, mainClass);
        flinkAddInputs.put(USER_ARGS_LBL, userArgs);
        flinkConfigMap.put(HopsPluginUtils.JOB_MANAGER_MM_LBL, jobManagerMem);
        flinkConfigMap.put(HopsPluginUtils.NUM_TASK_LBL, numTaskManager);
        flinkConfigMap.put(HopsPluginUtils.TASK_MANAGER_MM_LBL, taskManagerMem);
        flinkConfigMap.put(HopsPluginUtils.NUM_SLOT_LBL, numSlots);
        // advanced panel
        advanceFieldmap.put(ARCHIVES_LBL, archives);
        advanceFieldmap.put(HopsPluginUtils.JARS_LBL, additionalJars);
        advanceFieldmap.put(HopsPluginUtils.PYTHON_LBL, pythonDepend);
        advanceFieldmap.put(HopsPluginUtils.FILES_LBL, additionalFiles);
        advanceFieldmap.put(HopsPluginUtils.MORE_PROP_LBL, moreProps);

    }

    /**
     * validate if input is number
     *
     * @param field input JTextField to validate
     */
    public void numberChecker(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                field.setEditable((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9')
                        || ke.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE);
            }
        });
    }

    @Override
    public boolean isModified() {
        // TODO: insert action logic here
        return userKey != null || userProject != null || userUrl != null;
        // check for all other variables
        // check last value != current return true
    }

    @Override
    public void disposeUIResources() {
        Configurable.super.disposeUIResources();
    }

    @Override
    public @Nullable
    @NonNls String getHelpTopic() {
        return Configurable.super.getHelpTopic();
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);

        storedUrl = userUrl.getText().trim();
        storedKey = userKey.getText().trim();
        storedProject = userProject.getText().trim();
        storedLogFile = logFilePath.getText().trim();
        storedJob = jobName.getText().trim();
        storedProgram = hdfsPath.getText().trim();
        storedUserArgs = userArgs.getText().trim();
        storedMainClass = mainClass.getText().trim();
        storedExecId = execId.getText().trim();
        if (sparkBtn.isSelected()) { //job config params
            storedJobType = SPARK;
            stored_spDriverMem = driverMem.getText().trim();
            stored_spDriverVC = driverVC.getText().trim();
            stored_spExecMem = executorMem.getText().trim();
            stored_spExecVC = execVC.getText().trim();
            if (!dynamicBtn.isSelected()) {
                isSparkDynamic = false;
                stored_spNumExec = numExecutor.getText().trim();
                properties.setValue(PATH_SP_NUM_EXEC, stored_spNumExec);
            } else { // if spark dynamic
                isSparkDynamic = true;
                stored_spInitExec = initExecutor.getText().trim();
                stored_spMaxExec = maxExecutor.getText().trim();
                stored_spMinExec = minExecutor.getText().trim();
                properties.setValue(PATH_SP_INIT_EXEC, stored_spInitExec);
                properties.setValue(PATH_SP_MAX_EXEC, stored_spMaxExec);
                properties.setValue(PATH_SP_MIN_EXEC, stored_spMinExec);
            }
            //job configs
            properties.setValue(PATH_IS_SPARK_DYNAMIC, isSparkDynamic);
            properties.setValue(PATH_SP_DRIVER_VC, stored_spDriverVC);
            properties.setValue(PATH_SP_DRVERMEM, stored_spDriverMem);
            properties.setValue(PATH_SP_EXEC_VC, stored_spExecVC);
            properties.setValue(PATH_SP_EXECMEM, stored_spExecMem);
        } else if (pythonBtn.isSelected()) {
            storedJobType = PYTHON;
            stored_pyCpuCore = cpuCore.getText().trim();
            stored_pyMemory = memory.getText().trim();
            properties.setValue(PATH_PY_MEMORY, stored_pyMemory);
            properties.setValue(PATH_PY_CPU_CORE, stored_pyCpuCore);
        } else {
            storedJobType = FLINK;
            stored_flNumTaskMan = numTaskManager.getText().trim();
            stored_flJobManMem = jobManagerMem.getText().trim();
            stored_flNumSlots = numSlots.getText().trim();
            stored_flTaskManMem = taskManagerMem.getText().trim();
            properties.setValue(PATH_FL_JOB_MANAGER_MEM, stored_flJobManMem);
            properties.setValue(PATH_FL_NUM_TASK_MANGER, stored_flNumTaskMan);
            properties.setValue(PATH_FL_TASK_MANAGER_MEM, stored_flTaskManMem);
            properties.setValue(PATH_FL_NUM_SLOTS, stored_flNumSlots);
        }

        //advanced config params
        isAdvancedConfig = advanceBtn.isSelected();
        storedAddFile = additionalFiles.getText().trim();
        storedAddJar = additionalJars.getText().trim();
        storedPythonDepend = pythonDepend.getText().trim();
        storedArchive = archives.getText().trim();
        storedMoreProp = moreProps.getText().trim();

        //set into project properties
        properties.setValue(HopsPluginUtils.PATH_URL, storedUrl);
        properties.setValue(HopsPluginUtils.PATH_KEY, storedKey);
        properties.setValue(HopsPluginUtils.PATH_PROJECT, storedProject);
        properties.setValue(HopsPluginUtils.PATH_FILE, storedLogFile);
        properties.setValue(HopsPluginUtils.PATH_JOB, storedJob);
        properties.setValue(HopsPluginUtils.PATH_PROGRAM, storedProgram);
        properties.setValue(HopsPluginUtils.PATH_USERARGS, storedUserArgs);
        properties.setValue(HopsPluginUtils.PATH_MAINCLASS, storedMainClass);
        properties.setValue(HopsPluginUtils.PATH_EXECID, storedExecId);
        properties.setValue(HopsPluginUtils.PATH_JOBTYPE, storedJobType);
        //advanced config
        properties.setValue(PATH_IS_ADVANCED, isAdvancedConfig);
        properties.setValue(PATH_ADDFILE, storedAddFile);
        properties.setValue(PATH_ADDJAR, storedAddJar);
        properties.setValue(PATH_MORE_PROP, storedMoreProp);
        properties.setValue(PATH_ARCHIVE, storedArchive);
        properties.setValue(PATH_PYTHON_DEPEND, storedPythonDepend);
    }

    @Override
    public void reset() {
        resetFields(userKey != null, userKey, storedKey, userUrl != null, userUrl, storedUrl, userProject != null, userProject, storedProject, logFilePath != null, logFilePath, storedLogFile, jobName != null, jobName, storedJob);
        resetFields2(hdfsPath != null, hdfsPath, storedProgram, userArgs != null, userArgs, storedUserArgs, mainClass != null, mainClass, storedMainClass, execId != null, execId, storedExecId);

        if (storedJobType == null || storedJobType.equals(HopsPluginUtils.SPARK)) {
            sparkBtn.setSelected(true);
            resetFields(stored_spDriverMem != null, driverMem, stored_spDriverMem, stored_spDriverVC != null, driverVC, stored_spDriverVC, stored_spExecMem != null, executorMem, stored_spExecMem, stored_spExecVC != null, execVC, stored_spExecVC, stored_spNumExec != null, numExecutor, stored_spNumExec);
            resetAdvanceConfig(dynamicBtn, isSparkDynamic, stored_spInitExec != null, initExecutor, stored_spInitExec, stored_spMaxExec != null, maxExecutor, stored_spMaxExec, stored_spMinExec != null, minExecutor, stored_spMinExec);
        } else if (storedJobType.equals(HopsPluginUtils.PYTHON)) {
            pythonBtn.setSelected(true);
            if (stored_pyMemory != null) memory.setText(stored_pyMemory);
            if (stored_pyCpuCore != null) cpuCore.setText(stored_pyCpuCore);
        } else {
            flinkBtn.setSelected(true);
            resetFields2(stored_flJobManMem != null, jobManagerMem, stored_flJobManMem, stored_flNumTaskMan != null, numTaskManager, stored_flNumTaskMan, stored_flTaskManMem != null, taskManagerMem, stored_flTaskManMem, stored_flNumSlots != null, numSlots, stored_flNumSlots);
        }

        resetAdvanceConfig(advanceBtn, isAdvancedConfig, archives != null, archives, storedArchive, additionalJars != null, additionalJars, storedAddJar, additionalFiles != null, additionalFiles, storedAddFile);
        if (pythonDepend != null) {
            pythonDepend.setText(storedPythonDepend);
        }
        if (moreProps != null) {
            moreProps.setText(storedMoreProp);
        }
    }

    private void resetAdvanceConfig(JCheckBox advanceBtn, boolean isAdvancedConfig, boolean b, JTextField archives, String storedArchive, boolean b2, JTextField additionalJars, String storedAddJar, boolean b3, JTextField additionalFiles, String storedAddFile) {
        advanceBtn.setSelected(isAdvancedConfig);

        if (b) {
            archives.setText(storedArchive);
        }
        if (b2) {
            additionalJars.setText(storedAddJar);
        }
        if (b3) {
            additionalFiles.setText(storedAddFile);
        }
    }

    private void resetFields2(boolean b, JTextField hdfsPath, String storedProgram, boolean b2, JTextField userArgs, String storedUserArgs, boolean b3, JTextField mainClass, String storedMainClass, boolean b4, JTextField execId, String storedExecId) {
        if (b) {
            hdfsPath.setText(storedProgram);
        }
        if (b2) {
            userArgs.setText(storedUserArgs);
        }
        if (b3) {
            mainClass.setText(storedMainClass);
        }
        if (b4) {
            execId.setText(storedExecId);
        }
    }

    private void resetFields(boolean b, JTextField userKey, String storedKey, boolean b2, JTextField userUrl, String storedUrl, boolean b3, JTextField userProject, String storedProject, boolean b4, JTextField logFilePath, String storedLogFile, boolean b5, JTextField jobName, String storedJob) {
        if (b) {
            userKey.setText(storedKey);
        }
        if (b2) {
            userUrl.setText(storedUrl);
        }
        if (b3) {
            userProject.setText(storedProject);
        }
        if (b4) {
            logFilePath.setText(storedLogFile);
        }
        if (b5) {
            jobName.setText(storedJob);
        }
    }


}
