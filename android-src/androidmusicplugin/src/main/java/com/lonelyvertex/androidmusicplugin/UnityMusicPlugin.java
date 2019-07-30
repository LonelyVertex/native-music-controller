package com.lonelyvertex.androidmusicplugin;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class UnityMusicPlugin {

    private Context context;
    private static UnityMusicPlugin instance;
    private boolean mPlaybackNowAuthorized;
    private boolean mPlaybackDelayed;
    private boolean mResumeOnFocusGain;
    private AudioManager am;
    private AudioFocusRequest mFocusRequest;

    public UnityMusicPlugin() {
        this.instance = this;
    }

    public static UnityMusicPlugin instance() {
        if(instance == null) {
            instance = new UnityMusicPlugin();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    final Object mFocusLock = new Object();

    public boolean stopBacgroundMusic() {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (mPlaybackDelayed || mResumeOnFocusGain) {
                                    synchronized(mFocusLock) {
                                        mPlaybackDelayed = false;
                                        mResumeOnFocusGain = false;
                                    }
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                synchronized(mFocusLock) {
                                    mResumeOnFocusGain = false;
                                    mPlaybackDelayed = false;
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                synchronized(mFocusLock) {
                                    mResumeOnFocusGain = true;
                                    mPlaybackDelayed = false;
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                // ... pausing or ducking depends on your app
                                break;
                        }
                    }
                })
                .build();
        MediaPlayer mMediaPlayer = new MediaPlayer();
        final Object mFocusLock = new Object();

        int res = 0;

        if (am != null) {
            res = am.requestAudioFocus(mFocusRequest);
        }

        synchronized(mFocusLock) {
            if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                mPlaybackNowAuthorized = false;
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mPlaybackNowAuthorized = true;
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                mPlaybackDelayed = true;
                mPlaybackNowAuthorized = false;
            }
        }

        return false;
    }

    public void clearAudioFocus() {
        am.abandonAudioFocusRequest(mFocusRequest);
    }
}