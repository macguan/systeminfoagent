#!/bin/sh
#java -classpath /home/machinemonitor/localdata/software/monitor/systeminfoagent/bin:/home/machinemonitor/localdata/software/monitor/systeminfoagent/lib/* cn.com.dwsoft.main.Bootstrap -h
#java -classpath /home/machinemonitor/localdata/software/monitor/systeminfoagent/bin:/home/machinemonitor/localdata/software/monitor/systeminfoagent/lib/* cn.com.dwsoft.main.Bootstrap -verify
#java -classpath /home/machinemonitor/localdata/software/monitor/systeminfoagent/bin:/home/machinemonitor/localdata/software/monitor/systeminfoagent/lib/* cn.com.dwsoft.main.Bootstrap -checkall
java -classpath /home/machinemonitor/localdata/software/monitor/systeminfoagent/bin:/home/machinemonitor/localdata/software/monitor/systeminfoagent/lib/* cn.com.dwsoft.main.Bootstrap  -checkonetime
