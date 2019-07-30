package com.lonelyvertex.androidmusicpluginoldapi;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class UnityMusicPluginOldSDK {

    private Context context;
    private static UnityMusicPluginOldSDK instance;
    private boolean mPlaybackDelayed;
    private boolean mResumeOnFocusGain;
    private AudioManager am;

    public UnityMusicPluginOldSDK() {
        this.instance = this;
    }

    public static UnityMusicPluginOldSDK instance() {
        if (instance == null) {
            Log.i("NativeMusicPlugin", "Null Instance creating singleton");
            instance = new UnityMusicPluginOldSDK();
        }
        return instance;
    }

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i("NativeMusicPlugin", "onAudioFocusChange called with value: " + focusChange);
            try {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.i("NativeMusicPlugin", "onAudioFocusChange AUDIOFOCUS_GAIN");
                        if (mPlaybackDelayed || mResumeOnFocusGain) {
                            synchronized (mFocusLock) {
                                mPlaybackDelayed = false;
                                mResumeOnFocusGain = false;
                            }
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        Log.i("NativeMusicPlugin", "onAudioFocusChange AUDIOFOCUS_LOSS");
                        synchronized (mFocusLock) {
                            mResumeOnFocusGain = false;
                            mPlaybackDelayed = false;
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.i("NativeMusicPlugin", "onAudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT");
                        synchronized (mFocusLock) {
                            mResumeOnFocusGain = true;
                            mPlaybackDelayed = false;
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        Log.i("NativeMusicPlugin", "onAudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        break;
                }
            } catch (Exception e) {
                Log.e("NativeMusicPlugin","onAudioFocusChange exception: " + e.getMessage());
                throw e;
            }
        }
    };

    public void setContext(Context context) {
        Log.i("NativeMusicPlugin", "setContext Called");
        if (context == null) {
            Log.i("NativeMusicPlugin", "null Context");
        }
        this.context = context;
    }

    final Object mFocusLock = new Object();

    public boolean stopBacgroundMusic() {
        Log.i("NativeMusicPlugin", "stopBacgroundMusic called");
        try {
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (am != null) {
                int result = am.requestAudioFocus(
                        afChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN
                );

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            Log.e("NativeMusicPlugin", "stopBackgroundMusic exception: " + e.getMessage());
            throw e;
        }
    }

    public void clearAudioFocus() {
        Log.i("NativeMusicPlugin", "clearAudioFocus Called");
        try {
            am.abandonAudioFocus(afChangeListener);
        } catch (Exception e) {
            Log.e("NativeMusicPlugin", "clearAudioFocus exception: " + e.getMessage());
            throw e;
        }
    }
}