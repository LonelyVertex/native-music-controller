//
//  MusicNativePlugin.h
//  MusicNativePlugin
//
//  Created by koprivajakub on 14/07/2018.
//  Copyright © 2018 Lonely Vertex s.r.o. All rights reserved.
//

//import the basics.
#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

//start interface.
@interface MusicNativePlugin : NSObject

//define methods.
-(id) init;
-(bool) isNativeAudioPlaying;
-(void) stopNativeAudioPlayer;
-(void) clearAudioFocus;

//end interface.
@end

#ifdef __cplusplus
// Following code is the external API that the plugin expose
extern "C" {
#endif

    bool _IsNativeMusicPlaying();
    void _StopNativeMusicPlaying();
    void _ClearAudioFocus();

#ifdef __cplusplus
}
#endif
