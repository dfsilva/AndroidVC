package vo.diegosilva.com.br.androidvc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;



import java.io.File;
import java.util.Date;

import br.com.diegosilva.vo.vclibrary.video.ConversionListener;
import br.com.diegosilva.vo.vclibrary.video.MediaController;
import vo.diegosilva.com.br.androidvc.file.FileUtils;


public class MainActivity extends Activity {

    private static final int RESULT_CODE_COMPRESS_VIDEO = 3;
    private static final String TAG = "MainActivity";
    private EditText editText;
    private ProgressBar progressBar;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText = (EditText) findViewById(R.id.editText);

        findViewById(R.id.btnSelectVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, RESULT_CODE_COMPRESS_VIDEO);
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            if (reqCode == RESULT_CODE_COMPRESS_VIDEO) {
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

                    try {
                        if (cursor != null && cursor.moveToFirst()) {

                            String displayName = cursor.getString(
                                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            Log.i(TAG, "Display Name: " + displayName);

                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            String size = null;
                            if (!cursor.isNull(sizeIndex)) {
                                size = cursor.getString(sizeIndex);
                            } else {
                                size = "Unknown";
                            }
                            Log.i(TAG, "Size: " + size);

                            tempFile = FileUtils.saveTempFile(displayName, this, uri);
                            editText.setText(tempFile.getPath());

                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        }
    }

    private void deleteTempFile(){
        if(tempFile != null && tempFile.exists()){
            tempFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteTempFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempFile();
    }

    public void compress(View v) {
//        MediaController.getInstance().scheduleVideoConvert(tempFile.getPath());
        new VideoCompressor().execute();
    }

    class VideoCompressor extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG,"Start video compression");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            File externalFile = Environment.getExternalStorageDirectory();
            String externalPath = externalFile.getAbsolutePath();
            String videosPath = externalPath + Config.VIDEOS_PATH;

            new File(videosPath).mkdirs();

            MediaController.getInstance().convertVideo(tempFile.getPath(),
                    videosPath + "video_" + new Date().getTime() + ".mp4", new ConversionListener() {
                        @Override
                        public void onError(Exception ex) {

                        }

                        @Override
                        public void onProgress(float progress) {

                        }

                        @Override
                        public void onSuccess(File compressedFile) {
                            Log.d(TAG, "Sucesso na conversao");
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void compressed) {
            super.onPostExecute(compressed);
            progressBar.setVisibility(View.GONE);
            if(compressed != null){
                Log.d(TAG,"Compression successfully!");
            }
        }
    }

}