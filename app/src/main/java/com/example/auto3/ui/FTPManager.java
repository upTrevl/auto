package com.example.auto3.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.auto3.ActiveRoute;
import com.google.android.gms.common.util.IOUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Executor;

public class FTPManager {
    Context mContex;
    FTPClient ftpClient = new FTPClient();
    File downloadFile2 = null;
    File defaultFile = null;

    public FTPManager(Context context) {
        this.mContex = context;
        defaultFile = new File(mContex.getFilesDir() + "/default/obelisk.wav"); // Создаем обьект файла файл

    }

    public File download(int route_num, String audio_name) {
        int count;
        try {
            ftpClient.connect("vh304.timeweb.ru", 21);
            ftpClient.enterLocalPassiveMode();
            ftpClient.login("cr55940_ftpuser", "01234321");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory("/routes/" + route_num + "/sounds");


            downloadFile2 = new File(mContex.getFilesDir() + "/" + route_num + "/" + audio_name); // Создаем обьект файла файл

            boolean checkFile = downloadFile2.createNewFile();

            if (!checkFile) {
                return downloadFile2;
            }

            String dirPath = mContex.getFilesDir().getAbsolutePath() + File.separator + route_num;
            File projDir = new File(dirPath);

            if (!projDir.exists()) {
                projDir.mkdirs();
            }

            InputStream input = ftpClient.retrieveFileStream(audio_name); // получаем файл с фтп
            OutputStream output = new BufferedOutputStream(new FileOutputStream(downloadFile2));

            byte[] data = new byte[8192];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadFile2;
    }


}

