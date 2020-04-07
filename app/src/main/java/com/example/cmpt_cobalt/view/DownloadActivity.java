package com.example.cmpt_cobalt.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.FetchAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadActivity extends AppCompatActivity {

    String[] url = {"http://data.surrey.ca/api/3/action/package_show?id=restaurants",
            "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports"};

    String[] fileList = {"restaurants_itr1.csv", "inspectionreports_itr1.csv"};

    ProgressDialog progressDialog;

    DownloadFileFromURL downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        long tDifference = 0;

        for (int i = 0; i < 2; i++) {
            String time = new FetchAPI(url[i]).getLastModified();
            File file = method(DownloadActivity.this, fileList[i]);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date date = null;
            try {
                date = df.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long epoch = date.getTime();    // lastmodified time of remote file
            epoch = epoch / 1000;
            long difference = epoch - (file.lastModified());

            if (tDifference < difference) {
                tDifference = difference;
            }
        }


            final ConstraintLayout dialogConstraint = findViewById(R.id.const_dialog);
            Button yesButton = findViewById(R.id.btn_dialogYes);
            Button noButton = findViewById(R.id.btn_dialogyNo);

            if (tDifference < 72000000) {
                TextView dialogText = findViewById(R.id.txt_dialogMsg);
                dialogText.setText(R.string.dialog_text);
                noButton.setVisibility(View.INVISIBLE);
                dialogConstraint.setVisibility(View.VISIBLE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConstraint.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(DownloadActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
            else {
                dialogConstraint.setVisibility(View.VISIBLE);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConstraint.setVisibility(View.INVISIBLE);
                        new DownloadFileFromURL(DownloadActivity.this).execute(url[0],fileList[0]);
                        new DownloadFileFromURL(DownloadActivity.this).execute(url[1],fileList[1]);
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConstraint.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent();
                        intent = MainActivity.makeLaunchIntent(DownloadActivity.this, "MainActivity");
                        intent.putExtra("Extra", "OLD");
                        startActivity(intent);
                        DownloadActivity.this.finish();
                    }
                });

            }
    }

    private class DownloadFileFromURL extends AsyncTask<String, Integer, String> {

        Context context;
        private DownloadFileFromURL(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            progressDialog = new ProgressDialog(DownloadActivity.this);
            progressDialog.setMessage("Fetching latest data from server.\nTo cancel, click anywhere outside the progress box.");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    Toast.makeText(DownloadActivity.this,"Download has been cancelled.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent = MainActivity.makeLaunchIntent(DownloadActivity.this, "MainActivity");
                    intent.putExtra("Extra", "OLD");
                    context.startActivity(intent);
                    DownloadActivity.this.finish();
                }
            });
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                FetchAPI fetch = new FetchAPI(f_url[0]);
                String downLink = fetch.getUrl();
                File file = method(DownloadActivity.this, f_url[1]);
                String time = fetch.getLastModified();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date = df.parse(time);
                long epoch = date.getTime();
                epoch = epoch / 1000;
                URL url = new URL(downLink);

                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthofFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 2048);


                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (lengthofFile > 0) // only if total length is known
                        publishProgress((int) (total * 100 / lengthofFile));
                    output.write(data, 0, count);

                }
                output.flush();
                output.close();
                input.close();

                file.setLastModified(epoch);

                return f_url[1];

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            if (file_url.equals("inspectionreports_itr1.csv")) {
            Intent intent = new Intent(DownloadActivity.this,MainActivity.class);
            startActivity(intent);
            DownloadActivity.this.finish();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }


    }

    static File method(Context obj, String filename){
        return new File (obj.getFilesDir(), filename );
    }
}