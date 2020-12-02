Plugins for popular IDEs (IntelliJ, PyCharm) for working with Hopsworks.  

### IntelliJ/PyCharm plugin for executing common job commands on Hopsworks platform. 

Work locally on your IntelliJ or PyCharm IDE and execute job actions directly on Hopsworks (create,run,stop,etc.)

#### How to build/install
* Import project with 'Gradle' in the IDE and run the gradle task 'buildPlugin'. This will create the `hops-intellij-<version>.zip` within build/distributions folder
* Install this zip via Settings -> Plugins -> 'Install Plugin from Disk'
* Note: To avoid out of memory issues, run the gradle task 'buildPlugin' with VM options '-Xmx4096m'. The source was built with JDK 11.

#### How to use plugin
* Access the 'Hopsworks Job Preferences' UI for specifying user preferences under Settings -> Tools -> Hopsworks Job Preferences. 
* Input the Hopworks project preferences and job details you wish to work on. 
* Open a Project and within the Project Explorer right click on the program (.jar,.py,.ipynb) you wish to execute as a job on Hopsworks. Different job actions possible are available in the context menu. 
* For 'Job Execution Status' and 'Job Execution Logs' actions you have the option of retrieving a particular execution details by specifying the execution id in the 'Hopsworks Job Preferences' UI. If not specified, the last execution for the job name specified is retrieved. 
