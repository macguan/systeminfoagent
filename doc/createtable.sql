create table guan_systeminfolog(
  serialid varchar2(32),
  checkdate date,
  serverhostname varchar2(32),
  serverip varchar2(20),
  
  avgcpuusepercent number,
  topcpuno varchar2(2),
  topcpuusepercent  number,
  
  memoryusedrate  number,
  jvmmemoryusedRate  number,
  
  topdiskname  varchar2(32),
  topdiskusepercent  number
)

---example insert sql:
--insert into guan_systeminfolog(checkdate,serverhostname,serverip,avgcpuusepercent,topcpuno,topcpuusepercent,memoryusedrate,jvmmemoryusedRate,topdiskname,topdiskusepercent) values(sysdate,'DESKTOP-20SGRER','192.168.0.106',57.02,'0',100.00,63.24,22.83,'C:\',72.00)

create table guan_systemProcesslog(
		serialid varchar2(32),
		checkdate varchar2(32),
		serverip varchar2(32),
		tomcatname  varchar2(2000),
		catalinahome varchar2(2000),
		processnum number(4)
)

create table guan_tomcatProcesslog(
		serialid varchar2(32),
		checkdate varchar2(32),
		serverip varchar2(32),
		tomcatname  varchar2(50),
		listen_port varchar2(32),
		jmx_port varchar2(32),
		
		starttime varchar2(32),
		timespan varchar2(50),
		
		maxMemory number,
		commitMemory number,
		usedMemory number,
		memUsage_basedoncommit number,
		
		commitNonMemory number,
		usedNonMemory number,
		nonmemUsage number,
		
		maxThreads number,
		acceptCount number,
		currentThreadCount number,
		currentThreadsBusy number
		
)

create table guan_systemUserlog(
		serialid varchar2(32),
		checkdate varchar2(32),
		serverip varchar2(32),
		username varchar2(2000)
)



