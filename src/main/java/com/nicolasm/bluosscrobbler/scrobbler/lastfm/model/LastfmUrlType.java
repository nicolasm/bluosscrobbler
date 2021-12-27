package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;

public enum LastfmUrlType {
    API {
        @Override
        public String getUrl(LastfmConfig config) {
            return config.getApiUrl();
        }
    },
    AUTH {
        @Override
        public String getUrl(LastfmConfig config) {
            return config.getAuthUrl();
        }
    };

    public abstract String getUrl(LastfmConfig config);
}
