package com.callender.mayancal.db;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.ByteArrayInputStream;
import java.io.IOException;


class SoundHelper {

    private AudioTrack audioPlayer;
    private Thread mThread;
    private int size;
    private ByteArrayInputStream inBytes = null;
    private boolean isPlay = true;


    SoundHelper() {
    }

    void prepare(byte[] sound_bytes) {
        inBytes = new ByteArrayInputStream(sound_bytes, 0, sound_bytes.length);
        size = sound_bytes.length;
    }

    void play() {
        stop();

        isPlay = true;

        audioPlayer = createAudioPlayer();
        audioPlayer.play();

        mThread = new Thread(new PlayerProcess());
        mThread.start();
    }

    private AudioTrack createAudioPlayer() {

        int intSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        return  new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, intSize,
                AudioTrack.MODE_STREAM);
    }

    private class PlayerProcess implements Runnable {

        @Override
        public void run() {
            int bytesread = 0;
            int ret;
            while (bytesread < size && isPlay) {
                if (Thread.currentThread().isInterrupted()) { break; }

                int count = 512 * 1024; // 512 kb
                byte[] byteData = new byte[count];

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
        }
    }

    private void stop(){
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

}