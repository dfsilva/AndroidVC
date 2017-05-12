package vo.diegosilva.com.br.androidvc;/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.app.Application;

import vo.diegosilva.com.br.androidvc.file.FileUtils;


public class ConverterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        System.loadLibrary("vclibrary");

        FileUtils.createApplicationFolder();
    }

}