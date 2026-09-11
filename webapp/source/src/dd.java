import java.io.*;  
public class dd {  

     public static void main(String[] args) {
        try {
            Process pro = Runtime.getRuntime().exec(
                    "cmd.exe /c tasklist |find \"vina.exe\"");
            BufferedReader reader=new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str=reader.readLine();

            if (str!=null)
            {
                System.out.println("1.exe在运行");
            }else{
                System.out.println("1.exe没有在运行");
            }

            reader.close();
        } catch (IOException ex) {
        }

    }  }