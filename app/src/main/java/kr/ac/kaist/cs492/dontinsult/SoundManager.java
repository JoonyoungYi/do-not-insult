package kr.ac.kaist.cs492.dontinsult;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by JoonyoungYi on 15. 7. 3..
 */
public class SoundManager {
    private final static String TAG = "SoundManager";

    /**
     *
     */
    private RecordTask mRecordTask = null;
    private MainActivity.LvAdapter mLvAdapter;

    /**
     *
     */
    public SoundManager(final Button mRecordBtn, MainActivity.LvAdapter mLvAdapter) {
        this.mLvAdapter = mLvAdapter;

        /**
         *
         */
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;

            @Override
            public void onClick(View v) {
                if (mStartRecording) {
                    ((Button) v).setText("STOP RECORDING");
                    mRecordBtn.setBackgroundColor(0xFFF44336);
                    startRecording();
                } else {
                    ((Button) v).setText("START RECORDING");
                    mRecordBtn.setBackgroundResource(R.color.primary_dark_material_dark);
                    stopRecording();
                }
                mStartRecording = !mStartRecording;
            }
        });
    }

    /**
     *
     */
    public void startRecording() {
        mRecordTask = new RecordTask();
        mRecordTask.execute();
    }

    /**
     *
     */
    public void stopRecording() {
        mRecordTask.isRun = false;
    }

    /**
     *
     */
    public class RecordTask extends AsyncTask<Void, String, ArrayList<Insult>> {

        private int minSize;
        private AudioRecord ar = null;
        public boolean isRun;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Insult> doInBackground(Void... params) {

            /**
             *
             */
            minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
            ar.startRecording();

            /**
             *
             */
            String header_name = generateHeaderName();
            ArrayList<Insult> insults = new ArrayList<>();
            insults.add(Insult.newInstance(header_name, null, true));

            /**
             *
             */
            FileOutputStream os = null;
            short[] buffer = new short[minSize];
            isRun = true;
            boolean isWritten = false;
            while (isRun) {

                ar.read(buffer, 0, minSize);

                /**
                 *
                 */
                int max = getAmplitude(buffer);

                /**
                 *
                 */
                if (max > 10000) {

                    if (!isWritten) {
                        try {
                            String name = generateName();
                            String filePath = generateFileName(name);
                            os = new FileOutputStream(filePath);
                            insults.add(Insult.newInstance(name, filePath, false));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        isWritten = true;
                    }

                    try {
                        byte bData[] = short2byte(buffer);
                        os.write(bData, 0, minSize * 2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    if (isWritten) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isWritten = false;
                    }

                }
            }


            /**
             *
             */
            ar.stop();

            return insults;
        }

        @Override
        protected void onPostExecute(ArrayList<Insult> insults) {
            mLvAdapter.insults.addAll(insults);
            mLvAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private int getAmplitude(short[] buffer) {
        int max = 0;
        for (short s : buffer) {
            if (Math.abs(s) > max) {
                max = Math.abs(s);
            }
        }
        return max;
    }

    /**
     * @return
     */
    private String generateName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());


        return date;
    }

    private String generateFileName(String name) {
        String mFileName = "/sdcard/" + name + ".pcm";
        return mFileName;
    }

    private String generateHeaderName() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        String mFileName = date + " Meeting";
        return mFileName;
    }


    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }


    /**
     * @param filePath
     * @throws IOException
     */
    public void startPlaying(String filePath) {

        try {
            // We keep temporarily filePath globally as we have only two sample sounds now..
            if (filePath == null)
                return;

            //Reading the file..
            File file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
            byte[] byteData = new byte[(int) file.length()];
            Log.d(TAG, (int) file.length() + "");

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                in.read(byteData);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // Set and push to audio track..
            int intSize = android.media.AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            Log.d(TAG, intSize + "");

            if (intSize < 0) {
                intSize = 100;
            }

            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
            if (at != null) {
                at.play();
                // Write the byte array to the track
                at.write(byteData, 0, byteData.length);
                at.stop();
                at.release();
            } else
                Log.d(TAG, "audio track is not initialised ");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
