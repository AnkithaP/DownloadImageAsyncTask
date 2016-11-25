package com.solutions.aryaan.downloadimageasynctask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText selectionText;
    ListView chooseImageList;
    String[] listOfImages;
    ProgressBar downloadImagesProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionText = (EditText)findViewById(R.id.urlSelectionText);
        chooseImageList = (ListView)findViewById(R.id.chooseImageList);
        listOfImages = getResources().getStringArray(R.array.imageUrls);
        downloadImagesProgress = (ProgressBar)findViewById(R.id.downloadProgress);

        chooseImageList.setOnItemClickListener(this);
    }

    public void downloadImage(View view){
        if (selectionText.getText().toString()!= null
                && selectionText.getText().toString().length()> 0){
            MyTask myTask = new MyTask();
            myTask.execute(selectionText.getText().toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        selectionText.setText(listOfImages[position]);
    }

    class MyTask extends AsyncTask<String,Integer,Boolean>{

        private int contentLength = -1;
        private int counter;
        private int calculatedProgress = 0;
        @Override
        protected void onPreExecute() {
            downloadImagesProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean successful = false;
            URL downloadURL = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            File file = null;
            FileOutputStream fileOutputStream = null;
            try {
                downloadURL = new URL(params[0]);
                connection = (HttpURLConnection) downloadURL.openConnection();
                contentLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                int read = -1;
                file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"
                        + Uri.parse(params[0]).getLastPathSegment());
                //storage/0/emulated/downloads/9-credit-0.jpg
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while((read = inputStream.read(buffer)) != -1){
                    fileOutputStream.write(buffer,0,read);
                    counter = counter+read;
                    //Message.logMessage("counter"+counter+"length"+contentLength);
                    publishProgress(counter);
                }
                successful=true;
            } catch (MalformedURLException e) {
                Message.logMessage(""+e);

            } catch (IOException e) {
                Message.logMessage(""+e);

            }finally {
                if (connection != null){
                    connection.disconnect();
                }
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Message.logMessage(""+e);
                    }
                }
                if (fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Message.logMessage(""+e);
                    }
                }
            }
            return successful;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            calculatedProgress = (int)(((double)values[0]/contentLength)*100);
            downloadImagesProgress.setProgress(calculatedProgress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            downloadImagesProgress.setVisibility(View.GONE);
        }
    }

}
