using UnityEditor;
using UnityEditor.Callbacks;
#if UNITY_IOS || UNITY_STANDALONE_OSX
using System;
using System.Collections.Generic;
using System.IO;
using UnityEditor.iOS.Xcode;
#endif

//public class NativeMusicControllerPostProcessBuild {
public class NativeMusicControllerPostProcessBuild : Editor
{
    [PostProcessBuildAttribute(PostProcessBuildOrder.MODIFY_MAIN_MM__MUSIC_CONTROLLER)]
    public static void OnPostProcessBuild(BuildTarget target, string path)
    {
        if (target == BuildTarget.iOS) {
#if UNITY_IOS || UNITY_STANDALONE_OSX
            string projectPath = PBXProject.GetPBXProjectPath(path);

            string mainMMPath = String.Format("{0}/Classes/main.mm", path);

            string[] mainMMFileContents = File.ReadAllLines(mainMMPath);
            List<string> newFileLines = new List<string>(mainMMFileContents.Length + 2);

            string importLine = "#import <AVFoundation/AVFoundation.h>";
            string audioSessionLine =
                "[[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryAmbient withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];";

            bool addImportLine = true;
            bool addAudioSessionLine = true;
            bool mainMethodLineFound = false;
            bool hasWritten = false;

            foreach (var fileLine in mainMMFileContents) {
                if (fileLine.Contains(audioSessionLine)) {
                    addAudioSessionLine = false;
                }
                if (fileLine.Contains(importLine)) {
                    addImportLine = false;
                }
            }

            // Add import line for the AVFoundation kit
            if (addImportLine) {
                newFileLines.Add(importLine);
            }

            foreach (var fileLine in mainMMFileContents) {
                newFileLines.Add(fileLine);

                if (fileLine.StartsWith("int main(")) {
                    mainMethodLineFound = true;
                }

                // Add AVAudioSession controller line
                if (addAudioSessionLine && fileLine.StartsWith("{") && mainMethodLineFound && !hasWritten) {
                    newFileLines.Add(audioSessionLine);
                    hasWritten = true;
                }
            }

            File.WriteAllLines(mainMMPath, newFileLines);
#endif
        }
    }
}
