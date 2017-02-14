//
//  NatScreen.h
//
//  Created by huangyake on 17/1/7.
//  Copyright © 2017 Nat. All rights reserved.
//


#import <Foundation/Foundation.h>

@interface NatScreen : NSObject
typedef void (^NatCallback)(id error,id result);
+ (id)singletonManger;
- (void)info:(NatCallback)callback;
- (void)getBrightness:(NatCallback)callback;
- (void)getOrientation:(NatCallback)callback;

- (void)setBrightness:(NSNumber *)brightness :(NatCallback)callback;
@end
