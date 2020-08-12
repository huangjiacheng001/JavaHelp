package cn.brisk.help.common.utils;


import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


public class FileUtil {
    private static final String MP3_TYPE = "mp3";
    private static final String M4A_TYPE = "m4a";
    private static final String MP4_TYPE = "mp4";
    private static final String PDF_TYPE = "pdf";
    private static final String DS_STORE_TYPE = "DS_Store";
    private static final List<String> IGNORE_FILES = new ArrayList<>();
    static {
        IGNORE_FILES.add("百万年薪架构师");
        IGNORE_FILES.add("深入浅出云计算");
    }

    public static void main(String[] args) {
        String newFile = "G:\\个人提升、职场\\职场\\自学能力提升训练营";
        String target = "（更多课程微信910097711）";
        String replacement = "";
        File file = new File(newFile);
        delete(file,MP3_TYPE);
//        replaceFileName(file, target, replacement);
    }


    /**
     * @description: 文件改名
     * @param file
     * @param target 匹配关键字
     * @param replacement 替换后的字符
     * @author: huangjiacheng
     * @time: 2020/4/30 16:10
     * @return: void
     */
    public static void replaceFileName(File file, String target, String replacement) {
        File[] fileArr = file.listFiles();
        for (File f : fileArr) {
            if (f.isDirectory()) {
                replaceFileName(f, target, replacement);
            } else if (f.isFile()) {
                String name = f.getName();
                System.out.println("改名前：【"+name+"】");
                String replace = name.replace(target, replacement).trim();
                System.out.println("改名后：【"+replace+"】");
                String fielname = f.getParent() +File.separator+ replace;
                f.renameTo(new File(fielname));
                System.out.println("-----------------------------------------------------------------");
            }
        }
    }

    /**
     * @param filePath
     * @description: 文件大小降序排序 （key:文件名，value:mb）
     * @author: huangjiacheng
     * @time: 2020/4/30 15:19
     * @return:
     */
    public static HashMap<String, Long> sort(String filePath) {

        //读取文件
        File file = new File(filePath);
        File[] files = file.listFiles();
        Map<String, Long> wordCounts = new LinkedHashMap<>();
        //文件转换成mb
        for (File f : files) {
            String name = f.getName();
            long length = getFileLength(f);
            Long mb = (length / 1024 / 1024);
            wordCounts.put(name, mb);
        }
        //降序排序
        LinkedHashMap<String, Long> sortedByCount = wordCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(Map.Entry:: getKey, Map.Entry:: getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //打印
        sortedByCount.forEach((fileName, size) -> System.out.println(fileName + "=" + size + "Mb  | " + (new BigDecimal(size).divide(new BigDecimal(1024)).setScale(2, BigDecimal.ROUND_HALF_UP)) + "Gb"));

        return sortedByCount;
    }

    /**
     * @param file
     * @param fileSuffix 文件后缀（class、mp3、txt、html....）
     * @description: 递归删除指定文件
     * @author: huangjiacheng
     * @time: 2020/4/23 11:52
     * @return: void
     */
    public static void delete(File file, String fileSuffix) {
        //数组指向文件夹中的文件和文件夹
        File[] fileArr = file.listFiles();
        //遍历文件和文件夹
        for (File f : fileArr) {
            //如果是文件夹，递归查找
            if (f.isDirectory()) {
                delete(f, fileSuffix);
            } else if (f.isFile()) {
                //是文件的话，把文件名放到一个字符串中
                String filename = f.getName();
                //如果是“class”后缀文件，返回一个boolean型的值
                if (filename.endsWith(fileSuffix)) {
                    f.delete();
                    System.out.println("成功删除：：" + f.getName());
                }
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        try {
            int bytesum = 0;
            int byteread;
            File oldfile = new File(oldPath);
            //文件存在时
            if (oldfile.exists()) {
                //读入原文件
                inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    //字节数 文件大小
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            //如果文件夹不存在 则建立新文件夹
            new File(newPath).mkdirs();
            File a = new File(oldPath);
            String[] oldFileArr = a.list();
            File temp;
            for (int i = 0; i < oldFileArr.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + oldFileArr[i]);
                } else {
                    temp = new File(oldPath + File.separator + oldFileArr[i]);
                }

                File newFileNamePath = new File(newPath + File.separator + oldFileArr[i]);
                boolean exists = newFileNamePath.exists();
                String name = temp.getName();
                if (exists) {
                    System.out.println("文件：" + name + " 已存在！");
                    continue;
                }
                if (IGNORE_FILES.contains(name)) {
                    System.out.println("忽略文件：" + name);
                    continue;
                }

                if (temp.isFile()) {
                    input = new FileInputStream(temp);
                    output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();

                }
                //如果是e799bee5baa6e79fa5e98193e4b893e5b19e31333262353961子文件夹
                if (temp.isDirectory()) {
                    copyFolder(oldPath + "/" + oldFileArr[i], newPath + "/" + oldFileArr[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭流异常！" + e.getMessage());
            }
        }
    }

    /**
     * @param oldPath
     * @param newPath
     * @description: Java 7中,可以使用复制方法的Files类文件,从一个文件复制到另一个文件
     * @author: huangjiacheng
     * @time: 2020/4/23 12:21
     * @return:
     */
    public static void copyFileUsingJava7Files(String oldPath, String newPath)
            throws IOException {
        File source = new File(oldPath);
        File dest = new File(newPath);
        Files.copy(source.toPath(), dest.toPath());
    }


    /*
     * 统计该文件夹大小
     * 1,返回值类型long
     * 2,参数列表File dir
     */
    public static long getFileLength(File dir) {
        //1,定义一个求和变量
        long len = 0;
        //2,获取该文件夹下所有的文件和文件夹listFiles(); Demo1_Student.class Demo1_Student.java
        File[] subFiles = dir.listFiles();
        //3,遍历数组
        for (File subFile : subFiles) {
            //4,判断是文件就计算大小并累加
            if (subFile.isFile()) {
                len = len + subFile.length();
                //5,判断是文件夹,递归调用
            } else {
                len = len + getFileLength(subFile);
            }
        }
        return len;
    }
}
