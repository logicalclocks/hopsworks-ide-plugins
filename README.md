Plugins for popular IDEs (IntelliJ, PyCharm) for working with Hopsworks.  

### Intellij/PyCharm plugin for executing comman job commands on Hopsworks platform. Work locally on your Intellij or PyCharm IDE and execute job actions directly on Hopsworks (create,run,stop,etc.)

* Install the plugin with built zip 'hops-intellij-1.0-SNAPSHOT.zip' 
* Access the 'Hopsworks Job Preferences' for specifying user preferences under Settings -> Tools -> Hopsworks Job Preferences. 
* Input the Hopworks project preferences and job details you wish to work on. 
* Open a Project and within the Project Explorer right click on the program (.jar,.py,.ipynb) you wish to execute as a job on Hopsworks. Different job actions possible are available in the context menu. 
* For 'Job Execution Status' and 'Job Execution Logs' actions you have the option of retrieving a particular execution details by specifying the execution id in the 'Hopsworks Job Preferences' UI. If not specified, the last execution for the job name specified is retrieved. 
