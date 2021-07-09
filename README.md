Plugins for popular IDEs (IntelliJ, PyCharm) for working with Hopsworks.  

### IntelliJ/PyCharm plugin for executing common job commands on Hopsworks platform. 

Work locally on your IntelliJ or PyCharm IDE and execute job actions directly on Hopsworks (create,run,stop,etc.)

#### How to build/install
* Import project with 'Gradle' in the IDE and run the gradle task `:buildPlugin` from gradle tool-bar. To run from command line, run `gradle build` to build source and `gradle buildPlugin` to build the plugin. This will create the `hops-intellij-<version>.zip` within build/distributions folder
* Install this zip via **Settings -> Plugins -> 'Install Plugin from Disk'**
* **Note:** Use Gradle 6.7 or later and JDK 8 or later. To run the plugin within IDE and to avoid out of memory issues, run the gradle task `:runIde` with VM options `-Xmx4096m`. 

#### How to use plugin
* Access the 'Hopsworks Job Preferences' UI for specifying user preferences under Settings -> Tools -> Hopsworks Job Preferences. 
* Input the Hopworks project preferences and job details you wish to work on. 
* Open a Project and within the Project Explorer right click on the program (.jar,.py,.ipynb) you wish to execute as a job on Hopsworks. Different job actions possible are available in the context menu. 
* **Note:** The Job Type `Python` only supports Hopsworks-EE 

##### Actions
* **Create:** Create or update job as specified in Hopsworks Job Preferences
* **Run:** Uploads the program first to the HDFS path as specficied and runs job 
* **Stop:** Stops a job
* **Delete:** Deletes a job
* **Job Execution Status / Job Execution Logs:** Get the job status or logs respectively. You have the option of retrieving a particular job execution by specifying the execution id in the 'Hopsworks Job Preferences' UI, otherwise default is the last execution for the job name specified. 

##### Support for running Flink jobs

* You can also submit your local program as a flink job using the plugin. Follow the steps to `Create Job` to first create a flink job in Hopsworks.
* Then click on `Run Job`. This will first start a flink cluster if there is no active running flink job with same job name. Otherwise it will use an active running flink cluster with same job name.
Next it will upload and submit your program to a running flink cluster.
* Set your program main class using the `Main Class` field in preferences. To pass arguments, simply fill it in the `User Arguments`, multiple arguments separated by space. e.g. --arg1 a1 --arg2 a2