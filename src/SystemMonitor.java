package cn.com.dwsoft.main;


import cn.com.dwsoft.util.jdbc.JdbcUtil;
import cn.com.dwsoft.util.systeminfo.SystemInfoUtil;
import cn.com.dwsoft.util.jdbc.JdbcUtil;
import cn.com.dwsoft.util.system.CommandUtil;

import java.util.Date;
import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;


public class SystemMonitor {
	
	private static Log logger = LogFactory.getLog(SystemMonitor.class); 
	
    public static void dispCurrentInfo() throws Exception {
		
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		
		System.out.println("--------------------------serverInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.serverInfo();
		String serverName = (String)jsonObject.get("server.host.name");
		String serverIp = (String)jsonObject.get("server.ip");
		System.out.println("server name:"+serverName);
		System.out.println("server ip:"+serverIp);
		
		System.out.println("--------------------------systemInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.systemInfo();
		String osName = (String)jsonObject.get("os.name");
		String osVersion = (String)jsonObject.get("os.version");
		System.out.println("os Name:"+osName);
		System.out.println("os Version:"+osVersion);
		
		System.out.println("--------------------------cpuInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.cpuInfo();
		jsonArray = jsonObject.getJSONArray("cpu");
		for (int i = 0, len = jsonArray.size(); i < len; i++) {    
			JSONObject jso = jsonArray.getJSONObject(i);
			String company = (String)jso.get("company");
			String model = (String)jso.get("model");
			Double combine = (Double)jso.get("freq.combined.double");
 			String combinedStr = returnDoublePercent(combine);
			System.out.println("no."+i+" cpu"+": name:"+company+"|model:"+model+"|usepercent;"+combinedStr+"%");
						
		}
		
		
		System.out.println("--------------------------memoryInfo-------------------------------------------");
		if(osName.contains("Linux")&&osVersion.contains("el7")){
				System.out.println("The current os is CentOS 7 and use free -m");
				Double usedrate = SystemInfoUtil.getCentO7Free();
				String usedrateStr = returnDoublePercent(usedrate);
				
				System.out.println("memory.usedrate:"+usedrateStr+"%");
				
		}else {
			jsonObject = SystemInfoUtil.memoryInfo();
			System.out.println("memory.total:"+jsonObject.get("memory.total")+"MB");
			System.out.println("memory.used:"+jsonObject.get("memory.used")+"MB");
			System.out.println("memory.free:"+jsonObject.get("memory.free")+"MB");
			
			String usedrateStr = returnDoublePercent((Double)jsonObject.get("memory.usedrate"));
				
			System.out.println("memory.usedrate:"+usedrateStr+"%");
			
		}
		
		System.out.println("--------------------------jvmInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.jvmInfo();
		System.out.println("jvm.memory.total:"+jsonObject.get("jvm.memory.total")+"MB");
		System.out.println("jvm.memory.free:"+jsonObject.get("jvm.memory.free")+"MB");
		Double jvmusedrate = (Double)jsonObject.get("jvm.memory.usedRate");
 		String jvmusedrateStr = returnDoublePercent(jvmusedrate);
		System.out.println("jvm.memory.usedRate:"+jvmusedrateStr+"%");
		
		
		System.out.println("--------------------------fileSystemInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.fileSystemInfo();
		jsonArray = jsonObject.getJSONArray("file.system");
		for (int i = 0, len = jsonArray.size(); i < len; i++) {    
			JSONObject jso = jsonArray.getJSONObject(i);
			String diskname = (String)jso.get("dev.name");
			Integer disktype=(Integer)jso.get("type");
			Double usepercent = (Double)jso.get("usage.use.percent");
			String usepercentStr = returnDoublePercent(usepercent);
			
			if(disktype != null && disktype == 2){
				System.out.println("no."+i+" disk"+": diskname:"+diskname+"|disktype:"+disktype+"|usepercent;"+usepercentStr+"%");
			}
			
		}
		
		System.out.println("--------------------------netInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.netInfo();
		jsonArray = jsonObject.getJSONArray("net");
		for (int i = 0, len = jsonArray.size(); i < len; i++) { 
			JSONObject jso = jsonArray.getJSONObject(i);
			String intfname = (String)jso.get("name");
			String addr = (String)jso.get("address");
			if(!addr.equals("0.0.0.0")){
				System.out.println("no."+i+" intf"+": intfname:"+intfname+"|addr:"+addr);
			}
		}
			
		
		System.out.println("--------------------------ethernetInfo-------------------------------------------");
		jsonObject = SystemInfoUtil.ethernetInfo();
		jsonArray = jsonObject.getJSONArray("ethernet");
		for (int i = 0, len = jsonArray.size(); i < len; i++) { 
			JSONObject jso = jsonArray.getJSONObject(i);
			String description = (String)jso.get("description");
			String addr = (String)jso.get("address");
			if(!addr.equals("0.0.0.0")){
				System.out.println("no."+i+" intf"+": intfname:"+description+"|addr:"+addr);
			}
		}
    }

    public static JSONObject getCurrentTopInfo() throws Exception {
		
		JSONObject retObject = new JSONObject();
		
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		
		jsonObject = SystemInfoUtil.serverInfo();
		String serverName = (String)jsonObject.get("server.host.name");
		String serverIp = (String)jsonObject.get("server.ip");
		retObject.put("server.host.name",serverName);
		retObject.put("server.ip",serverIp);
		
		jsonObject = SystemInfoUtil.systemInfo();
		String osName = (String)jsonObject.get("os.name");
		String osVersion = (String)jsonObject.get("os.version");
		retObject.put("os.name",osName);
		retObject.put("os.version",osVersion);
		
		jsonObject = SystemInfoUtil.cpuInfo();
		jsonArray = jsonObject.getJSONArray("cpu");
		Double topCombine = null;
		Integer topNo = null;
		Double sumCombine = 0d;
		for (int i = 0, len = jsonArray.size(); i < len; i++) { 
			
			JSONObject jso = jsonArray.getJSONObject(i);
			String company = (String)jso.get("company");
			String model = (String)jso.get("model");
			Double combine = (Double)jso.get("freq.combined.double");
 			String combinedStr = returnDoublePercent(combine);
			
			sumCombine += Double.parseDouble(combinedStr);
			
			if(i>0){
				if(combine>topCombine){
					topCombine = combine;
					topNo = i;
				}
					
			}else {
				topCombine = combine;
				topNo = i;
			}
			//System.out.println("no."+i+" cpu"+": name:"+company+"|model:"+model+"|usepercent;"+combinedStr+"%");
						
		}
		//System.out.println("top cpu:no."+topNo+"|topusepercent;"+returnDoublePercent(topCombine));
		retObject.put("top.cpu.no",topNo+"");
		retObject.put("top.cpu.usepercent",returnDoublePercent(topCombine));	
		retObject.put("avg.cpu.usepercent",returnDoublePercent(sumCombine/jsonArray.size()));
		
		Double usedrate = 0d;
		if(osName.contains("Linux")&&osVersion.contains("el7")){
				logger.debug("The current os is CentOS 7 and use free -m");
				usedrate = SystemInfoUtil.getCentO7Free();
				//String usedrateStr = returnDoublePercent(usedrate);
				
				//System.out.println("memory.usedrate:"+usedrateStr+"%");
		}else {
			jsonObject = SystemInfoUtil.memoryInfo();
			//System.out.println("memory.total:"+jsonObject.get("memory.total")+"MB");
			//System.out.println("memory.used:"+jsonObject.get("memory.used")+"MB");
			//System.out.println("memory.free:"+jsonObject.get("memory.free")+"MB");
			
			usedrate = (Double)jsonObject.get("memory.usedrate");
		
		}
		String usedrateStr = returnDoublePercent(usedrate);
			
		//System.out.println("memory.usedrate:"+usedrateStr+"%");
		retObject.put("memory.usedrate",usedrateStr);
			
		
		jsonObject = SystemInfoUtil.jvmInfo();
		//System.out.println("jvm.memory.total:"+jsonObject.get("jvm.memory.total")+"MB");
		//System.out.println("jvm.memory.free:"+jsonObject.get("jvm.memory.free")+"MB");
		Double jvmusedrate = (Double)jsonObject.get("jvm.memory.usedRate");
 		String jvmusedrateStr = returnDoublePercent(jvmusedrate);
		//System.out.println("jvm.memory.usedRate:"+jvmusedrateStr+"%");
		retObject.put("jvm.memory.usedRate",jvmusedrateStr);
		
		jsonObject = SystemInfoUtil.fileSystemInfo();
		jsonArray = jsonObject.getJSONArray("file.system");
		Double topUsePercent = null;
		String topDiskName = null;
		for (int i = 0, len = jsonArray.size(); i < len; i++) {    
			JSONObject jso = jsonArray.getJSONObject(i);
			String diskname = (String)jso.get("dev.name");
			Integer disktype=(Integer)jso.get("type");
			Double usepercent = (Double)jso.get("usage.use.percent");
			String usepercentStr = returnDoublePercent(usepercent);
			
			if(disktype != null && disktype == 2){
				if(topUsePercent == null){
					topUsePercent = usepercent;
					topDiskName = diskname;
				}else {
					if(usepercent>topUsePercent){
						if(diskname.contains("sda2")){
							//为贵州联通定制，sda2是/boot的盘，没必要检测，跳过
						}else {
							topUsePercent = usepercent;
							topDiskName = diskname;
						}	
						
					}
				}
				//System.out.println("no."+i+" disk"+": diskname:"+diskname+"|disktype:"+disktype+"|usepercent;"+usepercentStr+"%");
			}
			
		}
		//System.out.println("top disk:"+topDiskName+"|topusepercent;"+returnDoublePercent(topUsePercent));
		retObject.put("top.disk.name",topDiskName);
		retObject.put("top.disk.usepercent",returnDoublePercent(topUsePercent));
			
		return 	retObject;
    }
	

	
	public static void checkProcessAlive() throws Exception{
		
		String processName = "tomcat";
		
		try{
		
		logger.info("\n Now check process alive...");
		long startTime=System.currentTimeMillis(); 
		
		String osName= System.getProperty("os.name").toUpperCase();
		String OS_WINDOWS = "WINDOWS";


		StringBuffer sb = new StringBuffer(256);
		if(StringUtils.contains(osName, OS_WINDOWS)){
			sb.append("cmd.exe /C windowsnull");
		}else{
			sb.append("ps -ef");
		}
		
		List<String> outputList = CommandUtil.execSystemCommon(sb.toString());
		
		System.out.println("打印命令输出[The output for process as following]:");		
		for(String line:outputList){
			//System.out.println(line);
		}
		
		System.out.println(processName+" preocess :");
		List<String> grepList = new ArrayList<String>();
        for(String line:outputList){
			if(line.contains(processName)){
				if(line.length()>200){
					System.out.println(line.substring(0,500)+"...");
				}else{
					System.out.println(line);
				}
							
			}
		}		
		
		logger.info("\n check finished!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
		
		}catch (Exception e) {  
 
            logger.error(e.getMessage());  
			throw e;
        }
	}
	
	
	public static void checkOneTime() throws Exception{
		
		try{
		
		logger.info("\n Now check system info...");
		long startTime=System.currentTimeMillis(); 
		
		JSONObject jso = getCurrentTopInfo();
		logger.debug(jso);
		
		String serverhostname =(String)jso.get("server.host.name");
		String serverip =(String)jso.get("server.ip");
		String topcpuno =(String)jso.get("top.cpu.no");
		String topcpuusepercent =(String)jso.get("top.cpu.usepercent");
		String avgcpuusepercent =(String)jso.get("avg.cpu.usepercent");
		String memoryusedrate =(String)jso.get("memory.usedrate");
		String jvmmemoryusedRate =(String)jso.get("jvm.memory.usedRate");
		String topdiskname =(String)jso.get("top.disk.name");
		String topdiskusepercent =(String)jso.get("top.disk.usepercent");
		
		String sql = "insert into guan_systeminfolog(" +
		             "serialid,checkdate,serverhostname,serverip,"+
					 "avgcpuusepercent,topcpuno,topcpuusepercent,"+
					 "memoryusedrate,jvmmemoryusedRate,"+
					 "topdiskname,topdiskusepercent)"+
					 " values("+
					 "'"+JdbcUtil.getUUId()+"'"+
					 ",sysdate"+",'"+serverhostname+"'"+",'"+serverip+"'"+
					 ","+avgcpuusepercent+""+",'"+topcpuno+"'"+","+topcpuusepercent+""+
					 ","+memoryusedrate+""+","+jvmmemoryusedRate+""+
					 ",'"+topdiskname+"'"+","+topdiskusepercent+""+
					 ")";
		logger.debug(sql);
		
		String[] sqls = {sql};
		JdbcUtil.executeSql("jdbc.properties",sqls);
		
		
		
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
		
		}catch (Exception e) {  
 
            logger.error(e.getMessage());  
			throw e;
        }
	}
	
	public static String returnDoublePercent(Double d){
			if(d!=null){
				DecimalFormat df = new DecimalFormat("#.00");
				return  df.format(d);
			}else{
				return null;
			}
			
	}
	
	
}

