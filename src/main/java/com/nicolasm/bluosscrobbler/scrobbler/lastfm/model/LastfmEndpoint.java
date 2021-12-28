package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import com.google.common.collect.ImmutableList;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import lombok.RequiredArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.*;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlType.API;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlType.AUTH;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils.LastfmUrlParametersHelper.signCall;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils.LastfmUrlParametersHelper.toUrlVariables;

@SuppressWarnings("squid:S00115")
@RequiredArgsConstructor
public enum LastfmEndpoint {
    auth_getToken(API, ImmutableList.of(api_key, api_sig, format, method)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(api_key, config.getApiKey());
            variables.put(format, "json");
            variables.put(method, "auth.getToken");
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    },
    request_auth(AUTH, ImmutableList.of(api_key, token)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(api_key, config.getApiKey());
            variables.put(token, parameters.getToken());
            signCall(config, variables);

            return toUrlVariables(variables);

        }
    },
    auth_getSession(API, ImmutableList.of(api_key, api_sig, format, method, token)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(api_key, config.getApiKey());
            variables.put(format, "json");
            variables.put(method, "auth.getSession");
            variables.put(token, parameters.getToken());
            signCall(config, variables);

            return toUrlVariables(variables);

        }
    },
    track_updateNowPlaying(API, ImmutableList.of(album, artist, duration, track, format, method, api_key, api_sig, sk)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) throws UnsupportedEncodingException {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(album, parameters.getAlbum());
            variables.put(api_key, config.getApiKey());
            variables.put(artist, parameters.getArtist());
            variables.put(duration, parameters.getDuration());
            variables.put(track, parameters.getTrack());
            variables.put(format, "json");
            variables.put(method, "track.updateNowPlaying");
            variables.put(sk, config.getSessionKey());
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    },
    track_scrobble(API, ImmutableList.of(album, artist, duration, track, format, method, api_key, api_sig, sk)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) throws UnsupportedEncodingException {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(album, parameters.getAlbum());
            variables.put(api_key, config.getApiKey());
            variables.put(artist, parameters.getArtist());
            variables.put(duration, parameters.getDuration());
            variables.put(track, parameters.getTrack());
            variables.put(format, "json");
            variables.put(method, "track.scrobble");
            variables.put(sk, config.getSessionKey());
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    };

    private static String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private final LastfmUrlType type;
    private final List<LastfmUrlParameterType> parameters;

    public String getEndpoint(LastfmConfig config) {
        return String.format("%s?%s", type.getUrl(config),
                parameters.stream()
                        .map(p -> String.format("%s={%s}", p, p))
                        .collect(Collectors.joining("&")));
    }

    public abstract Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) throws UnsupportedEncodingException;
}
