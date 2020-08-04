package cn.com.dwsoft.util.systeminfo;

import cn.com.dwsoft.util.file.PropertiesParser;
import cn.com.dwsoft.util.system.CommandUtil;

import java.io.IOException;

import java.util.Properties;

import java.net.InetAddress;  
 
import java.net.UnknownHostException;  
 
import java.util.Map;  
import java.util.HashMap;  
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;  
import net.sf.json.JSONObject;

import org.hyperic.sigar.OperatingSystem;  

import org.hyperic.sigar.Sigar;  
 
import org.hyperic.sigar.CpuInfo;  
import org.hyperic.sigar.CpuPerc;  

import org.hyperic.sigar.Mem;   
import org.hyperic.sigar.Swap;  
import org.hyperic.sigar.SigarException; 

import org.hyperic.sigar.FileSystem;  
import org.hyperic.sigar.FileSystemUsage;  

import org.hyperic.sigar.NetFlags;  
import org.hyperic.sigar.NetInterfaceConfig;  
import org.hyperic.sigar.NetInterfaceStat; 

public class SystemInfoUtil {

	private static Log logger = LogFactory.getLog(SystemInfoUtil.class); 

	public static JSONObject serverInfo() {
 
        JSONObject jsonObject = new JSONObject();  
        InetAddress addr = null;  
 
        try {  
			addr = InetAddress.getLocalHost(); 
			
			jsonObject.put("server.ip", addr.getHostAddress()); //本机ip  
 
            jsonObject.put("server.host.name", addr.getHostName()); //本机主机名  
			
			
		}catch (Exception e) {  
 
            System.out.println(e.getMessage());  
 
        }
		
		
		return jsonObject;
	}

	public static JSONObject systemInfo() {
 
        JSONObject jsonObject = new JSONObject(); 
		
		OperatingSystem OS = OperatingSystem.getInstance();  
        
        jsonObject.put("os.name", OS.getVendorName()); //操作系统名称  
		
		jsonObject.put("os.arch", OS.getArch()); //内核构架  
 
        jsonObject.put("os.description", OS.getDescription()); //操作系统的描述  
 
        jsonObject.put("os.version", OS.getVersion()); //操作系统的版本号            

		
		return jsonObject;
	}

	public static JSONObject cpuInfo()  {
   

		JSONObject jsonObject = new JSONObject(); 

		try {  
			Sigar sigar = new Sigar();           
 
 
			CpuInfo infos[] = sigar.getCpuInfoList();  
 
			CpuPerc cpuList[] = sigar.getCpuPercList(); 
			
			JSONArray jsonArray = new JSONArray(); 
			
			for (int i = 0, len = infos.length; i < len; i++) {// 不管是单块CPU还是多CPU都适用  
              
				CpuInfo info = infos[i];  
				  
				JSONObject jso = new JSONObject();               
	 
				jso.put("mhz", info.getMhz()); //CPU的总量MHz  
	 
				jso.put("company", info.getVendor()); //CPU的厂商  
	 
				jso.put("model", info.getModel()); //CPU型号类别  
	 
				jso.put("cache.size", info.getCacheSize()); // 缓冲缓存数量  
	 
				CpuPerc cpu = cpuList[i];  
	 
				jso.put("freq.user", CpuPerc.format(cpu.getUser())); //CPU的用户使用率  
	 
				jso.put("freq.sys", CpuPerc.format(cpu.getSys())); //CPU的系统使用率  
	 
				jso.put("freq.wait", CpuPerc.format(cpu.getWait())); //CPU的当前等待率  
	 
				jso.put("freq.nice", CpuPerc.format(cpu.getNice())); //CPU的当前错误率  
	 
				jso.put("freq.idle", CpuPerc.format(cpu.getIdle())); //CPU的当前空闲率  
	 
				jso.put("freq.combined", CpuPerc.format(cpu.getCombined())); //CPU总的使用率  
				jso.put("freq.combined.double", cpu.getCombined()*100d); //CPU总的使用率  
				
				jsonArray.add(jso);  
	 
			}  
			
			jsonObject.put("cpu", jsonArray); 
			
		}catch (Exception e) {  
 
            System.out.println(e.getMessage());  
 
        }
		
				
		return jsonObject;
	}

	
	 public static JSONObject memoryInfo() throws SigarException {         
 
		JSONObject jsonObject = new JSONObject(); 
        Sigar sigar = new Sigar();  
 
        Mem mem = sigar.getMem();           
 
          
        jsonObject.put("memory.total", mem.getTotal() / (1024 * 1024L));// 内存总量  
 
        jsonObject.put("memory.used", mem.getUsed() / (1024 * 1024L));// 当前内存使用量  
 
        jsonObject.put("memory.free", mem.getFree() / (1024 * 1024L));// 当前内存剩余量   
		
		BigDecimal usedBig = new BigDecimal(mem.getUsed());
		BigDecimal totalBig = new BigDecimal(mem.getTotal());
		BigDecimal usedRateBig = usedBig.divide(totalBig,4,BigDecimal.ROUND_HALF_UP);
		//System.out.println("usedRateBig:"+usedRateBig);
		
		jsonObject.put("memory.usedrate", usedRateBig.doubleValue()*100d );//  
 
        Swap swap = sigar.getSwap();            
 
        jsonObject.put("memory.swap.total", swap.getTotal() / (1024 * 1024L));// 交换区总量  
 
        jsonObject.put("memory.swap.used", swap.getUsed() / (1024 * 1024L));// 当前交换区使用量  
 
        jsonObject.put("memory.swap.free", swap.getFree() / (1024 * 1024L));// 当前交换区剩余量  

        return jsonObject;  
    }  

	public static double getCentO7Free() throws Exception{
		
		String cmd = "free -m";
		
		try{
		
		logger.debug("\n Now check cmd..."+cmd);
		long startTime=System.currentTimeMillis(); 
		
		StringBuffer sb = new StringBuffer(256);
		sb.append(cmd);
		
		List<String> outputList = CommandUtil.execSystemCommon(sb.toString());

		logger.debug("打印命令输出[The output for process as following]:");            
		for(String line:outputList){
				logger.debug(line);
		}
		
		String freeLine = outputList.get(1);
		
		String[] memAs = freeLine.split(":")[1].split("\\s+");


		BigDecimal availableBig = new BigDecimal(Double.parseDouble(memAs[6]));
		BigDecimal totalBig = new BigDecimal(Double.parseDouble(memAs[1]));
		BigDecimal usedBig = totalBig.subtract(availableBig);
		BigDecimal usedRateBig = usedBig.divide(totalBig,4,BigDecimal.ROUND_HALF_UP);
		//System.out.println("usedRateBig:"+usedRateBig);
		
		logger.debug("\n check finished!");
		long endTime=System.currentTimeMillis();
		logger.debug("\n time consume:"+(endTime-startTime)+"ms");
		
		return usedRateBig.doubleValue()*100d;
		

		}catch (Exception e) {  
 
            logger.error(e.getMessage());  
			throw e;
        }
	}
	
	public static JSONObject jvmInfo() {
 
        JSONObject jsonObject = new JSONObject(); 
		
		Runtime r = Runtime.getRuntime();  
		
		jsonObject.put("jvm.memory.total", r.totalMemory()/ (1024 * 1024L)); //JVM可以使用的总内存  
 
        jsonObject.put("jvm.memory.free", r.freeMemory()/ (1024 * 1024L)); //JVM可以使用的剩余内存  
 
		BigDecimal freeBig = new BigDecimal(r.freeMemory());
		BigDecimal totalBig = new BigDecimal(r.totalMemory());
		
		BigDecimal usedRateBig = totalBig.subtract(freeBig).divide(totalBig,4,BigDecimal.ROUND_HALF_UP);
		jsonObject.put("jvm.memory.usedRate", usedRateBig.doubleValue()*100d );//
		
        jsonObject.put("jvm.processor.avaliable", r.availableProcessors()); //JVM可以使用的处理器个数  
		
		
		Properties props = System.getProperties(); 
		
 
        jsonObject.put("jvm.java.version", props.getProperty("java.version")); //Java的运行环境版本  
 
        jsonObject.put("jvm.java.vendor", props.getProperty("java.vendor")); //Java的运行环境供应商  
 
        jsonObject.put("jvm.java.home", props.getProperty("java.home")); //Java的安装路径  
 
        jsonObject.put("jvm.java.specification.version", props.getProperty("java.specification.version")); //Java运行时环境规范版本  
 
        jsonObject.put("jvm.java.class.path", props.getProperty("java.class.path")); //Java的类路径  
 
        jsonObject.put("jvm.java.library.path", props.getProperty("java.library.path")); //Java加载库时搜索的路径列表  
 
        jsonObject.put("jvm.java.io.tmpdir", props.getProperty("java.io.tmpdir")); //默认的临时文件路径  
 
        jsonObject.put("jvm.java.ext.dirs", props.getProperty("java.ext.dirs")); //扩展目录的路径  
		
		
		return jsonObject;
	}
	
	
	public static JSONObject fileSystemInfo() throws SigarException {  
          
        Sigar sigar = new Sigar();          
 
        FileSystem fslist[] = sigar.getFileSystemList();            
 
        JSONObject jsonObject = new JSONObject();            
 
        JSONArray jsonArray = new JSONArray();            
 
        for (int i = 0, len = fslist.length; i < len; i++) {                
 
            FileSystem fs = fslist[i];  
              
            JSONObject jso = new JSONObject();                
 
            jso.put("dev.name", fs.getDevName()); //分区盘符名称  
 
            jso.put("dir.name", fs.getDirName()); //分区盘符名称  
 
            jso.put("flags", fs.getFlags()); //分区盘符类型  
 
            jso.put("sys.type.name", fs.getSysTypeName()); //文件系统类型  
 
            jso.put("type.name", fs.getTypeName()); //分区盘符类型名  
 
            jso.put("type", fs.getType()); //分区盘符文件系统类型  
 
            FileSystemUsage usage = null;  
              
 
            try {  
 
                usage = sigar.getFileSystemUsage(fs.getDirName());  
 
            } catch (Exception e) {  
 
                logger.error(e.getMessage());  
 
            }  
              
 
            if(usage == null) {  
 
                continue;  
 
            }  
 
              
            switch (fs.getType()) {  
 
            case 0: // TYPE_UNKNOWN ：未知  
 
                break;  
 
            case 1: // TYPE_NONE  
 
                break;  
 
            case 2: // TYPE_LOCAL_DISK : 本地硬盘  
                  
 
                jso.put("usage.totle", usage.getTotal() / 1024); // 分区总大小  
 
                jso.put("usage.free", usage.getFree() / 1024); // 分区剩余大小  
 
                jso.put("usage.avail", usage.getAvail() / 1024); // 分区可用大小  
 
                jso.put("usage.used", usage.getUsed() / 1024); // 分区已经使用量  
 
                jso.put("usage.use.percent", usage.getUsePercent() * 100D); // 分区资源的利用率  
 
                break;  
 
            case 3:// TYPE_NETWORK ：网络  
 
                break;  
 
            case 4:// TYPE_RAM_DISK ：闪存  
 
                break;  
 
            case 5:// TYPE_CDROM ：光驱  
 
                break;  
 
            case 6:// TYPE_SWAP ：页面交换  
 
                break;  
 
            }  
 
            jso.put("disk.reads", usage.getDiskReads()); // 读出  
 
            jso.put("disk.writes", usage.getDiskWrites()); // 写入  
              
            jsonArray.add(jso);  
 
        }            
 
        jsonObject.put("file.system", jsonArray);  
        
		
        return jsonObject;            
 
    }  
	
	
	public static JSONObject  netInfo() throws SigarException {  
          
        Sigar sigar = new Sigar();  
 
        String ifNames[] = sigar.getNetInterfaceList();  
          
        JSONObject jsonObject = new JSONObject();  
          
        JSONArray jsonArray = new JSONArray();           
 
        for (int i = 0, len = ifNames.length; i < len; i++) {                
 
            String name = ifNames[i];                
 
            JSONObject jso = new JSONObject();  
             
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);              
            jso.put("name", name); // 网络设备名  
 
            jso.put("address", ifconfig.getAddress()); // IP地址  
 
            jso.put("mask", ifconfig.getNetmask()); // 子网掩码  
              
 
            if ((ifconfig.getFlags() & 1L) <= 0L) {  
 
                logger.debug("!IFF_UP...skipping getNetInterfaceStat");  
 
                continue;  
 
            }  
            
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);  
 
            jso.put("rx.packets", ifstat.getRxPackets());// 接收的总包裹数  
 
            jso.put("tx.packets", ifstat.getTxPackets());// 发送的总包裹数  
 
            jso.put("rx.bytes", ifstat.getRxBytes());// 接收到的总字节数  
 
            jso.put("tx.bytes", ifstat.getTxBytes());// 发送的总字节数  
 
            jso.put("rx.errors", ifstat.getRxErrors());// 接收到的错误包数  
 
            jso.put("tx.errors", ifstat.getTxErrors());// 发送数据包时的错误数  
 
            jso.put("rx.dropped", ifstat.getRxDropped());// 接收时丢弃的包数  
 
            jso.put("tx.dropped", ifstat.getTxDropped());// 发送时丢弃的包数  
              
            jsonArray.add(jso);  
              
        }            
 
        jsonObject.put("net", jsonArray); 
		 	
          
        return jsonObject;  
          
    }  
 
 
    public static JSONObject  ethernetInfo() throws SigarException {  
 
        Sigar sigar = new Sigar();  
 
        String[] ifaces = sigar.getNetInterfaceList();  
          
        JSONObject jsonObject = new JSONObject();  
     
        JSONArray jsonArray = new JSONArray();           
 
        for (int i = 0, len = ifaces.length; i < len; i++) {               
 
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);  
 
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0 || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {  
 
                continue;  
 
            }               
 
            JSONObject jso = new JSONObject();                
 
            jso.put("address", cfg.getAddress());// IP地址  
 
            jso.put("broad.cast", cfg.getBroadcast());// 网关广播地址  
 
            jso.put("hwaddr", cfg.getHwaddr());// 网卡MAC地址  
 
            jso.put("net.mask", cfg.getNetmask());// 子网掩码  
 
            jso.put("description", cfg.getDescription());// 网卡描述信息  
 
            jso.put("type", cfg.getType());// 网卡类型               
 
            jsonArray.add(jso);  
             
        }  
 
        jsonObject.put("ethernet", jsonArray);            
 
        return jsonObject;           
 
    }        
	
	
	

}
