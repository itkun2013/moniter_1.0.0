package com.konsung.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by DELL on 2017/3/16.
 */

public class DBTest {
    public static void copyDB2SD(Context cxt){
        String DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + cxt.getPackageName()+"/databases/";
        File db1 = cxt.getDatabasePath(DBManager.DB_NAME);
        Log.e("abc", "db1:"+db1.getAbsolutePath());
//        File db = new File(DB_PATH+DBManager.DB_NAME);
//        Log.e("abc", "db:"+db.getAbsolutePath());
        File out = new File(Environment.getExternalStorageDirectory()+"/"+DBManager.DB_NAME);
        try {
            FileInputStream fis = new FileInputStream(db1);
            FileOutputStream fos = new FileOutputStream(out);

            int len = 0;
            byte[] buf = new byte[1024];
            while((len = fis.read(buf)) != -1){
                fos.write(buf, 0, len);

            }
            fos.flush();
            fos.close();
            fis.close();

//            FileReader fr = new FileReader(db1);
//            FileWriter fw = new FileWriter(out);
//            int len = 0;
//            while((len = fr.read()) != -1){
//                fw.write(len);
//            }
//            fw.flush();
//            fw.close();
//            fr.close();

            Log.e("abc", "db2sd:success");

        }  catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("abc", "db2sd:failure-notfound");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("abc", "db2sd:failure-io");
        }
    }
}
