package cn.com.dwsoft.main.util;


import java.io.IOException;
import java.util.HashMap;


import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.com.dwsoft.main.constants.CommonFuncConstants;

import cn.com.security.ss.exception.WEException;



public class ParameterCommonInstance {

	
	public String daemon = null;
	
	public String verify = null;
	
	public String checkall = null;
	
	public String checkonetime = null;
	
	public String checkprocessalive = null;
	
	public String checkallprocessonguizhou = null;
	
	public String checkonetimeprocessonguizhou = null;

	public String checkalltomcatonguizhou = null;
	
	public String checkonetimetomcatonguizhou = null;
	
	public String checkalluseronguizhou = null;
	
	public String checkonetimeuseronguizhou = null;
	
	public String checktopprocess = null;
	
	
	public void getParamters(String[] args) throws IOException, WEException{
		
		String currentParamName = null;
		String currentParamValue = null;
		
		Map<String,String> params = new HashMap<String,String>();
		
		// 0.预处理
		
		// 1.无参和单参检查
		if(args.length == 0){
			showHelp();
			
			System.exit(-1);
		}
		
		if(args.length == 1){
			
			if(args[0].equalsIgnoreCase("-h")
				||args[0].equalsIgnoreCase("-help")
				||args[0].equalsIgnoreCase("-?")){
				showHelp();
				
				System.exit(-1);
			}
			if(args[0].equalsIgnoreCase("-version")){
				showVersion();
				
				System.exit(-1);
			}
			if(args[0].equalsIgnoreCase("-verify")){
				this.verify = "true";
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("-checkall")){
				this.checkall = "true";
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("-checkonetime")){
				this.checkonetime = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checkallprocessonguizhou")){
				this.checkallprocessonguizhou = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checkonetimeprocessonguizhou")){
				this.checkonetimeprocessonguizhou = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checkalltomcatonguizhou")){
				this.checkalltomcatonguizhou = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checkonetimetomcatonguizhou")){
				this.checkonetimetomcatonguizhou = "true";
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("-checkalluseronguizhou")){
				this.checkalluseronguizhou = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checkonetimeuseronguizhou")){
				this.checkonetimeuseronguizhou = "true";
				
				return;
			}
			if(args[0].equalsIgnoreCase("-checktopprocess")){
				this.checktopprocess = "true";
				
				return;
			}
			
		}
		
		String finishedCmd = "java -jar xxx.jar";
		// 2.遍历命令行参数,参数入内存
		for(int i=0;i<args.length;i++){
			
			finishedCmd += " " + args[i];
			
			// 减号开头的是参数名name,如:-filename
			if (args[i].startsWith("-")) {
				
				// 针对 -xxx -yyy yyyvalue这种情况
				// args[i]是-yyy
				// currentParamName(上一个参数的名)是-xxx
				// currentParamValue(上一个参数的值)是null
				//,将(-xxx,null)入哈希表
				if (currentParamName != null && currentParamValue == null) {
					params.put(currentParamName, currentParamValue);
				}
				
				currentParamName = args[i].toLowerCase();
				
				if (!params.containsKey(currentParamName)) {
					System.out.println("格式错误[command format error]!");
					System.out.println("The command is terminated at No."
							+ finishedCmd.length() + " charactor. ["
							+ finishedCmd + "]");
					System.out.println(args[i] + " is not a valid parameter !");
					showHelp();

					System.exit(-1);
				}
			}
			// 非减号开头的是参数值value,如:/home/rasln/data/20140202112011.txt
			else{
				// 上一个参数为空时,跟着的,应该是减号打头的,不应该是无减号的
				if (currentParamName == null) {
					// 第一个参数,必须是减号打头,否则就报错
					if(i==0){
						System.out.println("格式错误command format error["
								+ args[i] + "]!");
						System.out.println("The command is terminated at No."
								+ finishedCmd.length() + " charactor. ["
								+ finishedCmd + "]");
						System.out.println("try[-" + args[i] + "]!");
					}
					// 针对 -xxx xxxvalue yyyvalue这种情况  ,要报错
					else{
						System.out.println("格式错误command format error["
								+ args[i - 1] + " " + args[i] + "]!");
						System.out.println("The command is terminated at No."
								+ finishedCmd.length() + " charactor. ["
								+ finishedCmd + "]");
						System.out.println("try[" + args[i - 1] + " -"
								+ args[i] + "]!");
					}
					
					showHelp();
					
					System.exit(-1);
				}
				// 正确,上一个参数是减号打头的,当前参数是非减号,正好是参数值
				else{					
					currentParamValue = args[i];
					params.put(currentParamName, currentParamValue);
					
					// 参数入内存后,清空它,为下一个参数值对坐准备
					currentParamName = null;
					currentParamValue = null;
				}
			}

		}
		
		// 针对结尾是 -xxx的情况
		if(currentParamName != null && currentParamValue == null){
			
			params.put(currentParamName, currentParamValue);
			
		}
		
		
		// 3.参数格式化(参数业务化)
		

		// 4.打印业务化的参数
		System.out.println("parameter is checked!");
		

	}
	
	private void showHelp(){
		System.out.println("\nUsage: java -jar /home/testjava/xxx.jar [-options -params]");
		System.out.println();
		System.out.println("where options include:");
		System.out.println("   -h -? -help print help message. This parameter is conflick with other parameter.");
		System.out.println("   -version    print version and copywrite message. This parameter is conflick with other parameter.");
		System.out.println("   -verify     verify current envirnment of database and current configuration.");
		System.out.println("   -checkall   do a full system check. For example: -checkall ");
		System.out.println("   -checkonetime   do a one-time check and insert result into db. For example: -checkonetime ");
		System.out.println("   -checkallprocessonguizhou   do a full process check");
		System.out.println("   -checkonetimeprocessonguizhou   do a one-time check and insert result into db. For example: -checkonetime ");
		System.out.println("   -checkalltomcatonguizhou   do a full tomcat check ");
		System.out.println("   -checkonetimetomcatonguizhou   do a one-time check and insert result into db. For example: -checkonetime ");
		System.out.println("   -checkalluseronguizhou   do a full user check ");
		System.out.println("   -checkonetimeuseronguizhou   do a one-time check and insert result into db. For example: -checkonetime ");
		
		System.out.println();
		System.out.println("sample:");
		System.out.println("   java -jar xxx.jar -h");
		System.out.println("   java -jar xxx.jar -verify");
		System.out.println("   java -jar xxx.jar -checkall");
		System.out.println("   java -jar xxx.jar -checkonetime");
		System.out.println("   java -jar xxx.jar -checkallprocessonguizhou");
		System.out.println("   java -jar xxx.jar -checkonetimeprocessonguizhou");
		
	}
	
	private void showVersion(){
		
		System.out.println(CommonFuncConstants.PRODUCT_NAME+" [version: "+CommonFuncConstants.PRODUCT_VERSION+"]");
		System.out.println("北京道隆华尔软件股份有限公司 版权所有 @ 2007-2013 Dwsoft.com.cn - All rights reserved.");
		
	}
	
	


}
