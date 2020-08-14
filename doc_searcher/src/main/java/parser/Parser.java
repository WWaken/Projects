package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @ClassName: Parser
 * @Description: 读取文档内容，解析成行文本文件
 * @Author: Ma Yuanyuan
 */

//单独的可执行的类
public class Parser {
    private static final String INPUT_PATH = "F:\\docs\\api";
    private static final String OUTPUT_PATH = "F:\\raw_data.txt";

    public static void main(String[] args) {
        //通过main完成预处理过程
        //1.枚举出输入路径下的所有html文件（递归）。
        ArrayList<File> fileList = new ArrayList<>();
        enumFile(INPUT_PATH,fileList);
        //System.out.println(fileList.size());
        //2.针对枚举出来的html文件路径进行遍历，依次打开每个文件，并读取内容。
        //把内容转换成需要的结构化的数据（DocInfo对象)
        for(File f : fileList){
            System.out.println("converting" + f.getAbsolutePath() + "...");
            //最终输出文件是一个行文本文件，每一行对应一个html文件
            //line这个对象对应到一个文件
            String line = convertLine(f);
            System.out.println(line);
            System.out.println("===========");
        }
        //3.把DocInfo对象写入到最终的输出文件中，写成行文本文件的形式。
    }

    //除文本文档外，还可以使用json，xml等
    private static String convertLine(File f) {
        //根据f转换出标题
        String title = convertTitle(f);
        //根据f转换出url
        String url = convertUrl(f);
        //根据f转换出content,去掉html标签，去掉换行符
     //   String content = convertContent(f);
        //将三个部分拼接接出一行文本
        //"\3"起到分割三个部分的效果，在html这样的文本文件不能出现 \3 这种不可见字符（\2 \1）
       return title + "\3" + url + "\3" + content + "\n";
    }


    private static String convertTitle(File f) {
        //把文件名作为标题
        String name = f.getName();
        return name.substring(0,name.length() - ".html".length());
    }

    private static String convertUrl(File f) {
        //此处的url是线上文档对应url
        //线上文档和线下目录相结合
        //线上：https://docs.oracle.com/javase/8/docs/api/java\io\CharArrayReader.html
        //文档：F:\docs\api\java\io\CharArrayReader.html
        //结合：https://docs.oracle.com/javase/8/docs/api + \java\io\CharArrayReader.html
        String part1 = "https://docs.oracle.com/javase/8/docs/api";
        String part2 = f.getAbsolutePath().substring(INPUT_PATH.length());
        return part1 + part2;
    }

    private static String convertContent(File f) throws IOException {
        //去掉标签、去掉\n
        //先读取文件
        FileReader fileReader = new FileReader(f);
        boolean isContent = true;
        StringBuilder outpupt = new StringBuilder();
        while(true){
            int ret = fileReader.read();
            if(ret == -1) {
                //读取完毕
                break;
            }
            char c = (char) ret;
            if(isContent){
                //当前这部分是正文
                if (c == '<') {
                    isContent = false;
                    continue;
                }else
            }
        }
    }

    //当这个方法递归完成后，就在List中获取到了inputpath中以.html结尾的文件
    private static void enumFile(String inputPath, ArrayList<File> fileList) {
        //递归的把inputPatn对应的全部目录和文件都遍历一遍
        File root = new File(inputPath);
        //listFiles相当于ls命令
        File[] files = root.listFiles();
        //System.out.println(Arrays.toString(files));
        //遍历当前目录和路径，分别处理
        for(File f : files){
            if(f.isDirectory()){
                enumFile(f.getAbsolutePath(),fileList);
            }else if(f.getAbsolutePath().endsWith(".html")){
                //看文件后缀是不是.html，是的话就把文件的对象加到fileList中
                fileList.add(f);
            }
        }
    }
}
