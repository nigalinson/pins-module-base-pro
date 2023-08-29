package com.sloth.platform;

public class ComponentTypes {

    public @interface Log {
        String LOGCAT = "log_component_logcat";
        String LOGGER = "log_component_logger";
        String X_LOG = "log_component_xlog";
    }

    public @interface ImageLoader {
        String GLIDE = "image_loader_component_glide";
    }

    public @interface Player {
        String NATIVE_SURFACE = "player_component_native_surface";
        String NATIVE_TEXTURE = "player_component_native_texture";
        String EXO_SURFACE = "player_component_exo_surface";
        String EXO_TEXTURE = "player_component_exo_texture";
    }

    public @interface Downloader {
        String FETCH = "downloader_component_fetch";
        String LIU_LI_SHO = "downloader_component_liu_li_sho";
    }

    public @interface JSON_SERIALIZE {
        String GSON = "json_serialize_gson";
    }

}
