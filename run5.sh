#!/bin/sh
#top process alive sniffer and send mail when found error

java -classpath /home/machinemonitor/localdata/software/monitor/systeminfoagent/bin:/home/machinemonitor/localdata/software/monitor/systeminfoagent/lib/* cn.com.dwsoft.main.Bootstrap -checktopprocess
