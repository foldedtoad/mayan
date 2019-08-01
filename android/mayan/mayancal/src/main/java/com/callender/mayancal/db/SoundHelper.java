package com.callender.mayancal.db;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class SoundHelper {

    final String TAG = "SoundHelper";

    private AudioTrack audioPlayer;
    private Thread mThread;
    private int bytesread = 0, ret = 0;
    private int size;
    private ByteArrayInputStream inBytes = null;
    private byte[] byteData = null;
    private int count = 512 * 1024; // 512 kb
    private boolean isPlay = true;
    private boolean isLooping = false;
    private static Handler mHandler;

    public SoundHelper() {

    }

    public void prepare(byte[] sound_bytes) {
        mHandler = new Handler();
        inBytes = new ByteArrayInputStream(sound_bytes, 0, sound_bytes.length);
        size = sound_bytes.length;
    }

    public void play() {
        stop();

        isPlay = true;
        bytesread = 0;
        ret = 0;

        audioPlayer = createAudioPlayer();
        if (audioPlayer == null) return;
        audioPlayer.play();

        mThread = new Thread(new PlayerProcess());
        mThread.start();
    }

    private final Runnable mLoopingRunnable = new Runnable() {
        @Override
        public void run() {
            play();
        }
    };

    private AudioTrack createAudioPlayer() {

        int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);

        if (audioTrack == null) {
            Log.d(TAG, "audio track not initialized");
            return null;
        }
        return  audioTrack;
    }

    private class PlayerProcess implements Runnable{

        @Override
        public void run() {
            Log.d(TAG, "PlayerProgress.run()");
            while (bytesread < size && isPlay) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                byteData = new byte[(int) count];

                ret = inBytes.read(byteData, 0, count);

                if (ret != -1) { // Write the byte array to the track
                    audioPlayer.write(byteData,0, ret);
                    bytesread += ret;
                }
                else break;
            }

            try {
                inBytes.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (audioPlayer != null) {
                if (audioPlayer.getState() != AudioTrack.PLAYSTATE_STOPPED) {
                    audioPlayer.stop();
                    audioPlayer.release();
                    mThread = null;
                }
            }

            if (isLooping && isPlay ) mHandler.postDelayed(mLoopingRunnable,100);
        }
    }

    public void setLooping(){
        isLooping = !isLooping;
    }

    public void pause(){

    }

    public void stop(){
        isPlay = false;
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    public void reset(){

    }
}