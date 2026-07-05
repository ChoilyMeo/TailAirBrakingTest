package com.thnet.tailairbrakingtest.customapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class SqliteDatabaseContext extends ContextWrapper {
    private static SqliteDatabaseContext instance = null;
    private Context mContext;

    private SqliteDatabaseContext() {
        super(WindTestApplication.getWindTestInstance());
        this.mContext = WindTestApplication.getWindTestInstance();
    }

    public static SqliteDatabaseContext getInstance(){
        if (null == instance){
            instance = new SqliteDatabaseContext();
        }
        return instance;
    }

    public boolean backupDbFile(String dbName){
        String fileNameFmt = "yyyyMMddHHmmss";
        try {
            // 获取外部文件目录，对小于当前日期2天的备份文件进行删除
            File externalFilesDir = mContext.getExternalFilesDir("databases");
            if (externalFilesDir != null) {
                // 获取目录下所有文件
                File[] files = externalFilesDir.listFiles();
                if (files != null) {
                    // 获取当前时间
                    //LocalDateTime now = LocalDateTime.now();
                    // 计算当前时间的前两天
                    //LocalDateTime twoDaysAgo = now.minus(2, ChronoUnit.DAYS);
                    Calendar twoDaysAgo = Calendar.getInstance();
                    twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);
                    Date twoDaysAgoDate = twoDaysAgo.getTime();
                    // 定义自定义日期时间格式
                    SimpleDateFormat sdf = new SimpleDateFormat(fileNameFmt);
                    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");0
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".db")) {
                            // 提取文件名中的日期部分
                            String fileName = file.getName();
                            int startIndex = fileName.lastIndexOf("_") + 1;
                            int endIndex = fileName.lastIndexOf(".");
                            String dateTimeString = fileName.substring(startIndex, endIndex);
                            try {
                                // 解析日期时间字符串为 Date 对象
                                Date fileDate = sdf.parse(dateTimeString);
                                //LocalDateTime fileDateTime = LocalDateTime.parse(dateTimeString, formatter);
                                // 判断日期时间是否小于当前时间的前两天
                                if (fileDate.before(twoDaysAgoDate)) {
                                    // 删除文件
                                    if (file.delete()) {
                                        XLog.i("删除过期的备份文件：" + fileName);
                                    } else {
                                        XLog.w("无法删除备份文件：" + fileName);
                                    }
                                }
                            } catch (Exception e) {
                                XLog.e("解析日期时间出错: " + fileName + ", 错误信息: " + e.getMessage());
                            }
                        }
                    }
                    //开始备份数据库文件
                    File sourceFile = new File(externalFilesDir, dbName);
                    if (sourceFile.exists()) {
                        // 获取当前时间并格式化
                        Calendar now = Calendar.getInstance();
                        String timestamp = sdf.format(now.getTime());
                        String backupFileName = dbName + "_backup_" + timestamp + ".db";
                        File backupFile = new File(externalFilesDir, backupFileName);

                        try (InputStream in = new FileInputStream(sourceFile);
                             OutputStream out = new FileOutputStream(backupFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                            out.flush();
                            out.close();
                            in.close();
                            XLog.i("已备份文件" + dbName + "到: " + backupFileName);
                            return true;
                        } catch (IOException e) {
                            XLog.e("备份文件时出错: " + e.getMessage());
                        }
                    } else {
                        XLog.i("试风数据库文件不存在：" + dbName);
                    }
                } else {
                    XLog.i("数据库目录下无db文件");
                }
            } else {
                XLog.w("数据库目录为空");
            }
        } catch (Exception e){
            XLog.e("备份数据库文件发生异常：" + e.getMessage());
        }
        return false;
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象
     *
     * @param dbName
     */
    @Override
    public File getDatabasePath(String dbName) {
        String dbDir = mContext.getExternalFilesDir("databases").getAbsolutePath();
        if (TextUtils.isEmpty(dbDir)){
            XLog.e("获取数据库存储路径不存在。");
            return null;
        }
        File baseFile = new File(dbDir);
        // 目录不存在则自动创建目录
        if (!baseFile.exists()){
            baseFile.mkdirs();
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(baseFile.getPath());
        buffer.append(File.separator);
        buffer.append(dbName);
        String dbPath = buffer.toString();// 数据库路径
        // 数据库文件是否创建成功
        boolean isFileCreateSuccess = false;
        // 判断文件是否存在，不存在则创建该文件
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try {
                byte[] copyBuffer = new byte[1024];
                int length;
                OutputStream outputStream = new FileOutputStream(dbFile);
                InputStream inputStream = getResources().openRawResource(R.raw.lwsf);
                while ((length = inputStream.read(copyBuffer)) > 0) {
                    outputStream.write(copyBuffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                isFileCreateSuccess = true;
                XLog.i("数据库文件不存在，初始化操作完毕！");
            } catch (IOException e) {
                XLog.e("创建数据库文件异常：" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            isFileCreateSuccess = true;
        }
        // 返回数据库文件对象
        if (isFileCreateSuccess) {
            return dbFile;
        }
        else {
            return super.getDatabasePath(dbName);
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param name
     * @param mode
     * @param factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     * android.database.sqlite.SQLiteDatabase.CursorFactory,
     * android.database.DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);

        return result;
    }
}
