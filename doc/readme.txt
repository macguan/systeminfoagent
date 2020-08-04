
1.change info level
modify bin/log4j.propertis.
as following:
log4j.rootLogger=info, RootStdOut,RootFileOut
                 ^^^^change to "debug"
				 
and you will see the debug info log.

2.find log file 
You may also find the directory named logs.
And you will find the log file which is named systeminfos.log in that directory.

				 