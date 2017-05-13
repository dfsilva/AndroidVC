package vo.diegosilva.com.br.androidvc.file;

/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import vo.diegosilva.com.br.androidvc.Config;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static void createApplicationFolder() {
        File f = new File(Environment.getExternalStorageDirectory(), Config.VIDEOS_PATH);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), Config.VIDEOS_TEMP_PATH);
        f.mkdirs();

    }

    public static File saveTempFile(String fileName, Context context, Uri uri) {

        File mFile = null;
        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = resolver.openInputStream(uri);

            mFile = new File(Environment.getExternalStorageDirectory().getPath() + Config.VIDEOS_TEMP_PATH, fileName);
            out = new FileOutputStream(mFile, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return mFile;
    }
}
