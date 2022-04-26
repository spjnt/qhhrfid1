package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


public class FileUtil {
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
//        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        try {
            File file = new File(strFilePath);
            if (file.exists()) file.delete();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strcontent.getBytes());
            raf.close();
        } catch (Exception var6) {
        }

    }

    public static boolean ifExits(String filePath, String fileName) {
        String strFilePath = filePath + fileName;
        File file = new File(strFilePath);
        return file.exists();


    }

    public static void deleteImg(String filePath) {
        File file = new File(filePath);
        if (file.exists()) file.delete();
    }

    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        String path = "";
        try {
            if (fileName.isEmpty())
                path = filePath;
            else path = filePath + fileName;
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return file;
    }


    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }

        String dir = sdDir.toString();
        return dir;
    }

    public static boolean copyAssetAndWrite(Context context, String str) {
        try {
            File cacheDir = context.getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File file = new File(cacheDir, str);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.length() > 10) {
                return true;
            }
            InputStream open = context.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    fileOutputStream.flush();
                    open.close();
                    fileOutputStream.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean fileCopy(String oldFilePath, String newFilePath) throws IOException {
        //如果原文件不存在
        if (fileExists(oldFilePath) == false) {
            return false;
        }
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[1024];
        //输出流
        FileOutputStream outputStream = new FileOutputStream(new File(newFilePath));
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}
