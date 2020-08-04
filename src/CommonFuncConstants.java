package cn.com.dwsoft.main.constants;




public class CommonFuncConstants {
	
	//-----------------------------PRODUCT CONFIG-----------------------------------------------
	public static String PRODUCT_NAME = "Dwsoft System Info checktool";
	public static String PRODUCT_VERSION = "2.0";
	
	//-----------------------------WORK DIR CONFIG-----------------------------------------------
	
	//值为null的常�?会在PropertisConfigLoader.java这个过滤器里通过读取system_intial.properties被赋�?
	public static String ROOTDIR = null;
	
	//系统配置文件�?��的环境变量名,在ApplicationLoader里会通过读取system_intial.properties文件里的system.env.name变量而修�?
	public static String ENVNAME1 				= null ; //"NEZA_ROOT";

	
	//--------------------------------------安全�?用于�?��关键操作的确�?-----------------------------------------------------
	public static int timeInterVal = 2000;
	public static int threadNums = 1;
	public static int serverNo = 1;
	public static int retryNum = 5;
	public static int getUndoJobTopN = 5;
	public static boolean retryMode = true;
	
	//--------------------------------------安全�?用于�?��关键操作的确�?-----------------------------------------------------
	
	public static String SECURITYCODE = "Dwsoft415_GIN";		
		
	//-----------------------------COMMON CONFIG-----------------------------------------------
	public static String OS = null;	
	public static String DBTYPE = null;
	
	//值为null的常�?会在PropertisConfigLoader.java这个过滤器里通过读取system_intial.properties被赋�?
	public static String DEBUGMODE = null;
	public static String DBDELETEMODE = null;	
	
	
	//--------------------------------当前系统(归属�?-----------------------------------------------
	public static String CURRENT_LOCAL_CODE = ""; // CQDX,BJDX,XJDX,GXDX
	

	//-----------------------------activeMG CONFIG-----------------------------------------------
	
/*	public static String ACTIVEMG_DAEMON_ADDRESS = "tcp://localhost:61616";
	
	public static String ACTIVEMG_UMS_REPLY_TOPIC = "dwsoft_ra_ums_exchange_topic";

	public static String ACTIVEMG_CURRENT_SYS = "YCJH";*/
	
}
