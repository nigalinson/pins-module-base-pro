package com.sloth.pinsdemo;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.PlatformConfig;

@RouterService(interfaces = PlatformConfig.class, defaultImpl = true, singleton = true)
public class AppPlatformConfig extends PlatformConfig.Default {


}
