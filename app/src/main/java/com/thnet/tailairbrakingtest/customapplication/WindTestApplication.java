package com.thnet.tailairbrakingtest.customapplication;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.flattener.PatternFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.thnet.tailairbrakingtest.BuildConfig;
import com.thnet.tailairbrakingtest.dao.DaoMaster;
import com.thnet.tailairbrakingtest.dao.DaoSession;
import com.thnet.tailairbrakingtest.R;
import com.thnet.tailairbrakingtest.serialport.RF433PowerControl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class WindTestApplication extends Application {
    private static final String TAG = WindTestApplication.class.getSimpleName();
    private final static String DB_FILE_NAME = "lwsf.db";
    private static WindTestApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //为了后面逻辑处理进行Log记录，所以Log的初始化必须放在最前面
        initXlogEnv();
        initDatabase();
        //程序启动即打开433模块
        RF433PowerControl.powerOn();
    }

    private void initXlogEnv() {
        try {
            LogConfiguration config = new LogConfiguration.Builder()
                    .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                            : LogLevel.INFO)
                    .tag("WindPressureTest")                                         // 指定 TAG，默认为 "X-LOG"
                    .build();

            Printer androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
            Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                    .Builder(getExternalFilesDir("log").getAbsolutePath())                              // 指定保存日志文件的路径
                    .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                    .flattener(new PatternFlattener("{d yyyy-MM-dd HH:mm:ss} {L}/{t}:{m}"))
                    .build();

            XLog.init(                                                 // 初始化 XLog
                    config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                    androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
                    filePrinter);
        } catch (Exception ex) {
            Log.e(TAG, "初始化日志环境异常：" + ex);
        }
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initDatabase() {
        try {
            //copyDatabaseFile();
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(SqliteDatabaseContext.getInstance(), DB_FILE_NAME);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        } catch (Exception ex) {
            Log.e(TAG, "初始化数据库环境异常：" + ex);
        }
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static WindTestApplication getWindTestInstance() {
        return instance;
    }

    private void copyDatabaseFile() {
        try {
            File dbFile = getDatabasePath(DB_FILE_NAME);
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                byte[] buffer = new byte[1024];
                int length;
                OutputStream outputStream = new FileOutputStream(dbFile);
                InputStream inputStream = getResources().openRawResource(R.raw.lwsf);
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, "复制数据库文件异常：" + ex);
        }
    }
}
