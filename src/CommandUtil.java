package cn.com.dwsoft.util.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.security.ss.exception.WEException;


public class CommandUtil{
	
	private static Log logger = LogFactory.getLog(CommandUtil.class);
	
	private static String osName= System.getProperty("os.name").toUpperCase();
	private static String OS_WINDOWS = "WINDOWS";


	public static List<String> execSystemCommon(String command) throws WEException {
		
		logger.debug("-------------------当前执行指令[current command]:-------------------------");
		logger.debug("\n"+command+"\n\n");
		
		List<String> outputStrList = new ArrayList<String>();
		List<String> cmdOutputStrList = new ArrayList<String>();		
		try {

			Runtime r = Runtime.getRuntime();
			Process p = null;
			
			logger.debug("执行...[proceed...]:");
			
			p = r.exec(command);
			
			//读取命令的输出信息
			InputStream is = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
			p.waitFor();  
			
			if (p.exitValue() != 0) {
				logger.info("执行失败[process failed]!");
			}
						
			//打印输出信息
			String s = null;
			while ((s = reader.readLine()) != null) {
				cmdOutputStrList.add(s);
			}
			
			if(cmdOutputStrList.size()==0){
				logger.debug("命令输出为空[There is nothing in process output].");
				cmdOutputStrList.add("cmd process failed! exitValue = "+p.exitValue() );
			}
			
		}  catch(InterruptedException e){
			
			if(e.getMessage().length()>500){
				outputStrList.add(e.getMessage().substring(0,500)+"...");
			}else {
				outputStrList.add(e.getMessage());
			}
			throw new WEException("执行命令失败[command failed]",e);
			
		} catch (IOException e) {

			if(e.getMessage().length()>500){
				outputStrList.add(e.getMessage().substring(0,500)+"...");
			}else {
				outputStrList.add(e.getMessage());
			}

			throw new WEException("执行命令失败[command failed]",e);
		}finally{
			
			
			logger.debug("-----------------------执行完毕[command accompled]!-----------------------");
			
			outputStrList.addAll(cmdOutputStrList);
						
			// finally里的return语句,eclipse里会有warning 
			return outputStrList;
			
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
		
		//String a = "abcde";
		String a = "abcde|tomcat";
		String[] as = a.split("\\|");
		for(String t:as){
			System.out.println(t);
		}
		
	}
	
	
}





