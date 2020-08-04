package cn.com.dwsoft.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.security.ss.exception.WEException;

import java.util.Date;
import java.util.Formatter;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import java.lang.management.MemoryUsage;

import cn.com.dwsoft.util.jdbc.JdbcUtil;
import cn.com.dwsoft.main.RedisMonitor;

public class TomcatMonitor{
	
	//请在tomcat catalina.sh JAVA_OPTS里加上：
	// javax.management.AttributeNotFoundException:  Cannot find attribute maxThreads for org.apache.tomcat.util.net.SocketProperties@1689910

//修改JAVA_OPTS,加一行配置（打开JMX,注意设端口要自定义下，本机多个TOMCAT的JMX端口不能冲突）
//JAVA_OPTS="-server -Xms512m -Xmx1g -XX:PermSize=64M -XX:MaxPermSize=256"
//JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9399 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "


	private static Log logger = LogFactory.getLog(TomcatMonitor.class);
	
	public static String formatTimeSpan(long span) {   
	        long minseconds = span % 1000;   
		  
				span = span / 1000;   
				long seconds = span % 60;   
		  
				span = span / 60;   
				long mins = span % 60;   
		  
				span = span / 60;   
				long hours = span % 24;   
		  
				span = span / 24;   
				long days = span;   
				return (new Formatter()).format("%1$d天 %2$02d:%3$02d:%4$02d.%5$03d",   
						days, hours, mins, seconds, minseconds).toString();   
	}   
	
	public static String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");	
		return sdf.format(date);
	}
		
	public static JSONObject  getTomcatRMIInfo(String tomcatname,String ipaddress,String listen_port,String jmx_port)  {
		
		
		if(StringUtils.isBlank(ipaddress)){
				ipaddress = "localhost";
		}
		
		if(StringUtils.isBlank(listen_port)){
				listen_port = "8080";
		}
		
		if(StringUtils.isBlank(jmx_port)){
				jmx_port = "8999";
		}
		
		JSONObject jsonObject = null;
		
		Format form = new DecimalFormat("#.##");
		
		try{
			jsonObject = new JSONObject();
			jsonObject.put("tomcatname", tomcatname);
			jsonObject.put("ipaddress", ipaddress);
			jsonObject.put("listen_port", listen_port);
			jsonObject.put("jmx_port", jmx_port);
		
			// 1.初始化RMI连接
			String jmxURL = "service:jmx:rmi:///jndi/rmi://" +ipaddress + ":"+jmx_port+"/jmxrmi";
				
			JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);  
			
			Map<String,String[]> map = new HashMap<String,String[]>();   
			String[] credentials = new String[] { "monitorRole", "tomcat" };   
			map.put("jmx.remote.credentials", credentials);
				
			JMXConnector connector = JMXConnectorFactory.connect(serviceURL,map); 

			// 2.获取MBean连接
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();  

			// ------------------------- system ----------------------   
			ObjectName runtimeObjName = new ObjectName("java.lang:type=Runtime");   
			jsonObject.put("VmVendor", (String) mbsc.getAttribute(runtimeObjName, "VmVendor"));
			jsonObject.put("VmName", (String) mbsc.getAttribute(runtimeObjName, "VmName"));
			jsonObject.put("VmVersion", (String) mbsc.getAttribute(runtimeObjName, "VmVersion"));
			
			Date starttime = new Date((Long) mbsc.getAttribute(runtimeObjName, "StartTime"));   
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
			
			Long timespan = (Long) mbsc.getAttribute(runtimeObjName, "Uptime");   
			
			jsonObject.put("starttime", df.format(starttime));
			jsonObject.put("timespan", formatTimeSpan(timespan));
			
			// ------------------------ JVM -------------------------   
/*
jvm区域总体分两类，
heap区
  Eden Space（伊甸园）
  Survivor Space(幸存者区)
  Tenured Gen（老年代-养老区）
非heap区
   Code Cache(代码缓存区)
   Perm Gen（永久代）
   Jvm Stack(java虚拟机栈)
   Local Method Statck(本地方法栈)
*/   
			//1.TOMCAT内存
			//堆,使用内存,运行时数据区域，
			//所有类实例和数组的内存均从此处分配)   
			ObjectName heapObjName = new ObjectName("java.lang:type=Memory");   
			MemoryUsage heapMemoryUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(
														heapObjName,"HeapMemoryUsage"));

			long maxMemory = heapMemoryUsage.getMax();// 堆最大(上限）   Xmx
			long commitMemory = heapMemoryUsage.getCommitted();// 堆当前分配    
			long usedMemory = heapMemoryUsage.getUsed();   
			
			
			jsonObject.put("maxMemory", form.format((double)maxMemory/1000000));
			jsonObject.put("commitMemory", form.format((double)commitMemory/1000000));
			jsonObject.put("usedMemory", form.format((double)usedMemory/1000000));
			jsonObject.put("memUsage_basedoncommit", form.format((double) usedMemory * 100 / commitMemory));
			
			
			// 2.非堆(程序内存)
			ObjectName nonheapObjName = new ObjectName("java.lang:type=Memory"); 
			MemoryUsage nonheapMemoryUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(
													nonheapObjName,"NonHeapMemoryUsage"));   
			
			long commitNonMemory = nonheapMemoryUsage.getCommitted();   
			long usedNonMemory = nonheapMemoryUsage.getUsed();   
			
			jsonObject.put("commitNonMemory", form.format((double)commitNonMemory/1000000));
			jsonObject.put("usedNonMemory", form.format((double)usedNonMemory/1000000) );
			jsonObject.put("nonmemUsage", form.format((double) usedNonMemory * 100 / commitNonMemory));
			
			
			// 持久堆(classLoader 内存)
			/*
			ObjectName permObjName = new ObjectName( "java.lang:type=MemoryPool,name=\"Perm Gen\"");   
			MemoryUsage permGenUsage = MemoryUsage.from((CompositeDataSupport) mbsc.getAttribute(
													permObjName,"Usage"));   
			long committedPermgen = permGenUsage.getCommitted();// 当前分配持久堆   
			long usedPermgen = permGenUsage.getUsed();//   当前使用持久堆            
			
			System.out.println("usedPermgen:"+form.format((double)usedPermgen/1000000)+"m");
			System.out.println("commitPermgen:"+form.format((double)committedPermgen/1000000)+"m");
			System.out.println("permgenUsage:"+form.format((double) usedPermgen * 100 / committedPermgen) + "%");
			
			经过下面的查找，发现MemoryPool中不再有Perm Gen
			ObjectName threadpoolObjName = new ObjectName("java.lang:type=MemoryPool,*");
			Set<ObjectName> s2 = mbsc.queryNames(threadpoolObjName, null);  
			for (ObjectName obj : s2) {   
				System.out.println("端口名:" + obj.getKeyProperty("name")); 
			}
			端口名:Metaspace
			端口名:Tenured Gen
			端口名:Eden Space
			端口名:Survivor Space
			端口名:Code Cache
			*/
			
			
			// 3.获取TOMCAT线程池信息
			// 注：tomcat7以后，name=要加双引号，还要加转义符号\
			//     http-8080这个name也不支持了，要改成http-nio-8080
			ObjectName threadObjName = new ObjectName("Catalina:type=ThreadPool,name=\"http-nio-"+listen_port+"\"");//"+port);   
			//String attrName = "currentThreadCount";// tomcat的线程数对应的属性值   
			String maxThreads = mbsc.getAttribute(threadObjName, "maxThreads")+"";
			String currentThreadCount = mbsc.getAttribute(threadObjName, "currentThreadCount")+"";
			String currentThreadsBusy = mbsc.getAttribute(threadObjName, "currentThreadsBusy")+"";
			String acceptCount = mbsc.getAttribute(threadObjName, "acceptCount")+"";
/*		
1.对tomcat来说，每一个进来的请求(request)都需要一个线程，直到该请求结束。
2.currentThreadCount 和 maxThreads的关系
如果同时进来的请求多于当前可用的请求处理线程数（currentThreadCount），
额外的线程就会被创建，直到到达配置的最大线程数(maxThreads)
如果仍就同时接收到更多请求，这些来不及处理的请求就会在Connector创建的ServerSocket中堆积起来，直到到达最大的配置值(acceptCount属性值)。至此，任何再来的请求将会收到connection refused错误，直到有可用的资源来处理它们。
显然Connector元素的maxThreads和acceptCount属性对其有直接的影响。无论acceptCount值为多少，maxThreads直接决定了实际可同时处理的请求数。而不管maxThreads如何，acceptCount则决定了有多少请求可等待处理。
3.ajp,bio,nio
在windows上，所以tomcat默认使用的是aprconnector。
在linux上，默认使用的是bio connector。
与nio相比，bio性能较低
*/		
 			
			
			jsonObject.put("maxThreads", maxThreads);
			jsonObject.put("acceptCount", acceptCount);
			jsonObject.put("currentThreadCount",currentThreadCount);
			jsonObject.put("currentThreadsBusy", currentThreadsBusy);
			
			
			// ----------------- Thread Pool ----------------   
			/*
			ObjectName threadpoolObjName = new ObjectName("Catalina:type=ThreadPool,*");   
			Set<ObjectName> s2 = mbsc.queryNames(threadpoolObjName, null);  
			
			for (ObjectName obj : s2) {   
				System.out.println("端口名:" + obj.getKeyProperty("name"));   
				
				//ObjectName objname = new ObjectName(obj.getCanonicalName());   
				//System.out.println("最大线程数:"+ mbsc.getAttribute(objname, "maxThreads"));   
				//System.out.println("当前线程数:" + mbsc.getAttribute(objname, "currentThreadCount"));   
				//System.out.println("繁忙线程数:"+ mbsc.getAttribute(objname, "currentThreadsBusy"));   
			} 	
			*/
		
		}catch (Exception e){
			e.printStackTrace(System.err);
			jsonObject = null;
			
		}
			
		return jsonObject;
	}
	
	public static void checkAllTomcatOnGuiZhou(Boolean isInsertDB) 
	{
		logger.info("\n Now check tomcat info...");
		long startTime=System.currentTimeMillis(); 
		
		JSONObject jsonObject = null;
		
		String checkdate = dateToString(new Date());
		logger.info("\n checkdate:"+checkdate);
		
		/*
		jsonObject = getTomcatRMIInfo("localhost8190","localhost","8190","8999");
		if(jsonObject!=null){
			jsonObject.put("checkdate",checkdate);
			checkOneTomcatOnGuiZhou(jsonObject,isInsertDB);
		}		
		*/
		
		jsonObject = getTomcatRMIInfo("tomcat_guan","192.168.10.191","9380","9399");
		if(jsonObject!=null){
			jsonObject.put("checkdate",checkdate);
			checkOneTomcatOnGuiZhou(jsonObject,isInsertDB);
		}
		
		
		logger.info("\n check success!");
		long endTime=System.currentTimeMillis();
		logger.info("\n time consume:"+(endTime-startTime)+"ms");
		
	}
	
	public static void checkOneTomcatOnGuiZhou(JSONObject jsonObject,Boolean isInsertDB) 
	{
		

		if(isInsertDB){
			
			String sql = "insert into guan_tomcatProcesslog(" +
		             "serialid,"+
					 "checkdate,"+
					 
					 "serverip,"+
					 "tomcatname,"+
					 "listen_port,"+
					 "jmx_port,"+
					 
					 "starttime,"+
					 "timespan,"+
					 
					 "maxMemory,"+
					 "commitMemory,"+
					 "usedMemory,"+
					 "memUsage_basedoncommit,"+
					 
					 "commitNonMemory,"+
					 "usedNonMemory,"+
					 "nonmemUsage,"+
					 
					 "maxThreads,"+
					 "acceptCount,"+
					 "currentThreadCount,"+
					 "currentThreadsBusy)"+
					 
					 " values("+
					 
					 "'"+JdbcUtil.getUUId()+"'"+
					 ","+"'"+jsonObject.get("checkdate")+"'"+
					 
					 ","+"'"+jsonObject.get("ipaddress")+"'"+
					 ","+"'"+jsonObject.get("tomcatname")+"'"+
					 ","+"'"+jsonObject.get("listen_port")+"'"+
					 ","+"'"+jsonObject.get("jmx_port")+"'"+
					 
					 ","+"'"+jsonObject.get("starttime")+"'"+
					 ","+"'"+jsonObject.get("timespan")+"'"+
					 
					 ","+jsonObject.get("maxMemory")+
					 ","+jsonObject.get("commitMemory")+
					 ","+jsonObject.get("usedMemory")+
					 ","+jsonObject.get("memUsage_basedoncommit")+
					 
					 ","+jsonObject.get("commitNonMemory")+
					 ","+jsonObject.get("usedNonMemory")+
					 ","+jsonObject.get("nonmemUsage")+
					 
					 ","+jsonObject.get("maxThreads")+
					 ","+jsonObject.get("acceptCount")+
					 ","+jsonObject.get("currentThreadCount")+
					 ","+jsonObject.get("currentThreadsBusy")+

					 ")";
			logger.debug(sql);
			//System.out.println(sql);
		
			String[] sqls = {sql};
			try{
				JdbcUtil.executeSql("jdbc.properties",sqls);
			}catch (Exception e) {  
	 			logger.info(e.getMessage());  
	 		}
			
			
		
		
			
		}else {
		System.out.println("---------------------------------------------------");
		System.out.println("采集时间:" + jsonObject.get("checkdate"));  
		System.out.println("ipaddress:" + jsonObject.get("ipaddress"));  
		System.out.println("tomcatname:" + jsonObject.get("tomcatname"));  
		System.out.println("厂商:" + jsonObject.get("VmVendor"));   
		System.out.println("程序:" + jsonObject.get("VmName"));   
		System.out.println("版本:" + jsonObject.get("VmVersion"));   
		
		System.out.println("TOMCAT启动时间:" + jsonObject.get("starttime"));   
		System.out.println("TOMCAT连续工作时间:" + jsonObject.get("timespan"));
		
		System.out.println("maxMemory:"+jsonObject.get("maxMemory"));
		System.out.println("commitMemory:"+jsonObject.get("commitMemory"));
		System.out.println("usedMemory:"+jsonObject.get("usedMemory"));
		System.out.println("memUsage_basedoncommit:"+jsonObject.get("memUsage_basedoncommit"));
		
		
		System.out.println("commitNonMemory:"+jsonObject.get("commitNonMemory"));
		System.out.println("usedNonMemory:"+jsonObject.get("usedNonMemory"));
		System.out.println("nonmemUsage:"+jsonObject.get("nonmemUsage"));
		
		System.out.println("最大线程数:" + jsonObject.get("maxThreads"));  
		System.out.println("最大排队线程数:" + jsonObject.get("acceptCount"));  
		System.out.println("当前线程池线程数:" + jsonObject.get("currentThreadCount"));  
		System.out.println("繁忙线程数:" + jsonObject.get("currentThreadsBusy"));
		
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		/*
		StringBuffer sb = new StringBuffer(256);
		if(StringUtils.contains(osName, OS_WINDOWS)){
			sb.append("cmd.exe /C ipconfig");
		}else{
			sb.append("ifconfig ");
		}
		
		List<String> outputList = execSystemCommon(sb.toString());
		
		System.out.println("打印命令输出[The output for process as following]:");		
		for(String line:outputList){
			System.out.println(line);
		}
		*/
		
		checkAllTomcatOnGuiZhou(true);	
	}
	
	
}





