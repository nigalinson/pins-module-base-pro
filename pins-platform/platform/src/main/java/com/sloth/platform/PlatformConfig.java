package com.sloth.platform;

public interface PlatformConfig {

    String logComponent();
    String jsonComponent();
    String imageLoaderComponent();
    String playerComponent();
    String downloaderComponent();

    class Default implements PlatformConfig {

        @Override
        public String logComponent() {
            return ComponentTypes.Log.LOGGER;
        }

        @Override
        public String jsonComponent() {
            return ComponentTypes.JSON_SERIALIZE.GSON;
        }

        @Override
        public String imageLoaderComponent() {
            return ComponentTypes.ImageLoader.GLIDE;
        }

        @Override
        public String playerComponent() {
            return ComponentTypes.Player.NATIVE_SURFACE;
        }

        @Override
        public String downloaderComponent() {
            return ComponentTypes.Downloader.FETCH;
        }

    }

}
