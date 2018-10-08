package daohang.example.com.guanxingdaohang.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by RL on 2018/2/9.
 */
public class FileUtils {

    /**
     * 往文件里写入数据
     * @param filepathString
     * @param outputString
     */
    public static void appendText(String filepathString,String outputString) {
        FileWriter writer;
        try {
            File file=new File(filepathString);
            writer = new FileWriter(file,true);
            writer.write(outputString);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
