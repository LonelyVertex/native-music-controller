using UnityEngine;

public class NativeMusicController : MonoBehaviour
{
#if PLATFORM_ANDROID
    UnityEngine.AndroidJavaObject activityContext;
    UnityEngine.AndroidJavaObject unityMusicPluginInstance;
    System.String packageName = "com.lonelyvertex.androidmusicpluginoldapi";
    System.String className = "UnityMusicPluginOldSDK";

    // If we upgrade the Android target SDK to 26+ we need to use different library and test it. This is prepared here...
//    String packageName = "com.lonelyvertex.androidmusicplugin";
//    String className = "UnityMusicPlugin";
#endif
#if PLATFORM_IOS
    [System.Runtime.InteropServices.DllImport("__Internal")]
    static extern bool _IsNativeMusicPlaying();

    [System.Runtime.InteropServices.DllImport("__Internal")]
    static extern bool _StopNativeMusicPlaying();

    [System.Runtime.InteropServices.DllImport("__Internal")]
    static extern bool _ClearAudioFocus();
#endif

    public void TryToStopMusic()
    {
        StartCoroutine(StopMusicAfterInitialization());
    }

    System.Collections.IEnumerator StopMusicAfterInitialization()
    {
        yield return new WaitForSeconds(1);
        StopNativeMusicIfNeeded();
    }

    public void ClearAudioFocus()
    {
#if UNITY_EDITOR
        Debug.Log("Clearing audio focus -- this message will be visible only in editor");
        return;
#endif
#if PLATFORM_IOS
        _ClearAudioFocus();
#endif
#if PLATFORM_ANDROID
        GetPluginInstance().Call("clearAudioFocus");
#endif
    }

    void StopNativeMusicIfNeeded()
    {
        try {
            var isGameMusicMuted = Persistence.GetMuteAudio() || Mathf.Approximately(Persistence.GetMusicVolume(), 0f);

            SinusAnalytics.SetMusicEnabled(!isGameMusicMuted);

            if (!isGameMusicMuted) {
#if UNITY_EDITOR
                Debug.Log("Stopping native music -- this message will be visible only in editor");
                return;
#endif
#if PLATFORM_IOS
                _StopNativeMusicPlaying();
#endif
#if PLATFORM_ANDROID
            GetPluginInstance().Call<bool>("stopBacgroundMusic");
#endif
            } else {
#if UNITY_EDITOR
                Debug.Log("Music will not stop, condition is not satisfied -- this message will be visible only in editor");
                return;
#endif
            }
        } catch (System.Exception e) {
            UnityEngine.Debug.LogError(e);
            throw e;
        }
    }

#if PLATFORM_ANDROID
    UnityEngine.AndroidJavaObject GetPluginInstance()
    {
        if (activityContext == null) {
            using (UnityEngine.AndroidJavaClass activityClass = new UnityEngine.AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
                activityContext = activityClass.GetStatic<UnityEngine.AndroidJavaObject>("currentActivity");
            }
        }

        using (UnityEngine.AndroidJavaClass pluginClass = new UnityEngine.AndroidJavaClass(packageName + "." + className)) {
            if (unityMusicPluginInstance == null) {
                unityMusicPluginInstance = pluginClass.CallStatic<UnityEngine.AndroidJavaObject>("instance");
                unityMusicPluginInstance.Call("setContext", activityContext);
            }
        }

        return unityMusicPluginInstance;
    }
#endif
}
