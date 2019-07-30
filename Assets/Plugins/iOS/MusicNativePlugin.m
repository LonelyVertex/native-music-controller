//
//  MusicNativePlugin.m
//  MusicNativePlugin
//
//  Created by koprivajakub on 14/07/2018.
//  Copyright © 2018 Lonely Vertex s.r.o. All rights reserved.
//

#import "MusicNativePlugin.h"

@implementation MusicNativePlugin
    //define methods.
    AVAudioSession* audioSession;

    -(id) init {
        audioSession = [AVAudioSession sharedInstance];
        return self;
    }

    -(void) stopNativeAudioPlayer {
        [audioSession setCategory:AVAudioSessionCategorySoloAmbient withOptions:AVAudioSessionCategoryOptionDuckOthers error:nil];
    }

    -(void) clearAudioFocus {
        [audioSession setCategory:AVAudioSessionCategoryAmbient withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
    }

    -(bool) isNativeAudioPlaying {
        bool isOtherAudioPlaying = [audioSession isOtherAudioPlaying];
        return isOtherAudioPlaying;
    }
@end

MusicNativePlugin* musicNativePlugin = nil;

MusicNativePlugin* getMusicNativePlugin() {
    if (musicNativePlugin == nil) {
        musicNativePlugin = [[MusicNativePlugin alloc] init];
    }

    return musicNativePlugin;
}

bool _IsNativeMusicPlaying() {
    return [getMusicNativePlugin() isNativeAudioPlaying];
}

void _StopNativeMusicPlaying() {
    [getMusicNativePlugin() stopNativeAudioPlayer];
}

void _ClearAudioFocus() {
    [getMusicNativePlugin() clearAudioFocus];
}

