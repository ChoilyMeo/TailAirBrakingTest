package com.thnet.tailairbrakingtest.customapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.R;

import java.io.FileOutputStream;
import java.io.IOException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

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
            } catch (IOException e) {
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
