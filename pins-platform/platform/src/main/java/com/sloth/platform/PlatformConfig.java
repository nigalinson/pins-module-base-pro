package com.sloth.platform;

public interface PlatformConfig {

    String logComponent();
    String jsonComponent();
    String playerComponent();
    String downloaderComponent();
    String resourceManagerComponent();

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
        public String playerComponent() {
            return ComponentTypes.Player.NATIVE_SURFACE;
        }

        @Override
        public String downloaderComponent() {
            return ComponentTypes.Downloader.FETCH;
        }

        @Override
        public String resourceManagerComponent() {
            return ComponentTypes.ResourceManager.DEFAULT;
        }

    }

}
