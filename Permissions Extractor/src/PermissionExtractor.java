import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
* 
* PermissionExtractor.java
* @author Trushank
* Date Nov 2, 2012
* Version 1.0
*
 */
 /**
 * @author Trushank
 *
 */
 public class PermissionExtractor {

    /**
     * 
     */
    public PermissionExtractor() {
	// TODO Auto-generated constructor stub
    }

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
    // TODO Auto-generated method stub
	
    File input=new File("androidmanifest.txt");
    File output=new File("permissions.txt");
    try {
	Runtime.getRuntime().exec("cmd /C java -jar AXMLPrinter2.jar AndroidManifest.xml > androidmanifest.txt");
	
	Thread.sleep(1000);
	 PrintWriter out=new PrintWriter(output);
	
	BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(input)));
	String s;
	while((s=in.readLine())!=null){
	if(s.contains("android.permission.")){
	    int a=s.indexOf("android.permission.");
	    a=a+19;
	   
		out.println( s.substring(a, s.indexOf("\"",a))+"\n");
	}
	}
	in.close();
	out.close();
	
	
	
    } catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
    }
    
    }

}

