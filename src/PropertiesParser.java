package cn.com.dwsoft.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;
import java.util.Set;

public class PropertiesParser
{
    private String propertiesFileName;

    private Properties m_configuration;
   
    private InputStream is;
    
    public PropertiesParser(String propertiesFileName) throws IOException {
        this.propertiesFileName = propertiesFileName;
        getConfig();
    }
 
    /**
     * 读取文件入句柄
     * @throws IOException
     */
    private void getConfig() throws IOException {
      is = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
      

      //is.
        if (is == null){
        	
        	// 用绝对PATH方式打开文件
        	is = new FileInputStream(new File(propertiesFileName));  
           
        	if( is == null)
        	   throw new FileNotFoundException("Cannot find " + propertiesFileName + " file in classpath");
        }
       
        Properties p = new Properties();
        p.load(is);

        synchronized (this)
        {
        m_configuration = p;
        }
        
    }
	
	public Properties getProperties()
   {
      synchronized (this)
      {
         return m_configuration;
      }
   }
   
 
   /**
    * 获取对应的属性
    * @param key
    * @return
    */
   public String getInfoFromConfiguration(String key)
   {
      synchronized (this)
      {
         return m_configuration.getProperty(key);
      }
   }
   
   public Set<Object> getKeySetConfiguration()
   {
      synchronized (this)
      {
         return m_configuration.keySet();
      }
   }
 
   public void putInfoToConfiguration(String key,String value)
   {
      synchronized (this)
      {
         m_configuration.put(key, value);
      }
   }
   
   public void closeConfiguration()
   {
      synchronized (this)
      {
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
   }
   
   public void saveConfiguration(String filePath,String operateUserId)
   {
      synchronized (this)
      {
    	  OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(filePath);
			 m_configuration.store(outputStream, "modified by "+operateUserId);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	 
      }
   }
   


	public static void main(String[] args) throws Exception
    {

    	/*PropertiesParser paser = new PropertiesParser("c:/testjava/dataimport/result/1111.properties");
    	System.out.println(paser.getInfoFromConfiguration("a"));
    	paser.closeConfiguration();
    	*/
    	
    	
    	/* PropertiesParser paser = new PropertiesParser("classpath*:jdbc.properties");
      
      System.out.println(paser.m_configuration.size());
      Iterator keyIter = paser.m_configuration.keySet().iterator();
      
      while(keyIter.hasNext()){
    	String _key = (String) keyIter.next();
    	System.out.println(_key+":"+paser.m_configuration.getProperty(_key));
      }
      
      paser.m_configuration.setProperty("a", "a_value");
      
      OutputStream outputStream = new FileOutputStream("classpath*:jdbc.properties"); 
      
      paser.m_configuration.store(outputStream, "aaa comment");*/
      
      /*System.out.println(
              paser.getInfoFromConfiguration("jdbc.driverClassName")
              );
      System.out.println(
              paser.getInfoFromConfiguration("jdbc.url")
                  );
      System.out.println(
              paser.getInfoFromConfiguration("jdbc.username")
                  );
      System.out.println(
              paser.getInfoFromConfiguration("jdbc.password")
                  );
      paser.closeConfiguration();
                  */
    }


 
 }   
 
