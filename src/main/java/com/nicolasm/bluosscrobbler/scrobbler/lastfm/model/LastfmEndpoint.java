package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import com.google.common.collect.ImmutableList;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.album;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.api_key;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.api_sig;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.artist;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.cb;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.duration;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.format;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.method;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.sk;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.timestamp;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.token;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.track;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlType.API;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlType.AUTH;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils.LastfmUrlParametersHelper.signCall;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils.LastfmUrlParametersHelper.toUrlVariables;

@SuppressWarnings("squid:S00115")
@RequiredArgsConstructor
public enum LastfmEndpoint {
    request_auth(AUTH, null, ImmutableList.of(api_key, cb)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(api_key, config.getApiKey());
            variables.put(cb, parameters.getCallbackUrl());

            return toUrlVariables(variables);

        }
    },
    auth_getSession(API, "auth.getSession", ImmutableList.of(api_key, api_sig, format, method, token)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
            variables.put(api_key, config.getApiKey());
            variables.put(format, "json");
            variables.put(method, methodName);
            variables.put(token, parameters.getToken());
            signCall(config, variables);

            return toUrlVariables(variables);

        }
    },
    track_updateNowPlaying(API, "track.updateNowPlaying",
            ImmutableList.of(album, artist, duration, track, format, method, api_key, api_sig, sk)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = getTrackVariables(config, parameters);
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    },
    track_scrobble(API, "track.scrobble",
            ImmutableList.of(album, artist, duration, timestamp, track, format, method, api_key, api_sig, sk)) {
        @Override
        public Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters) {
            Map<LastfmUrlParameterType, String> variables = getTrackVariables(config, parameters);
            variables.put(timestamp, parameters.getTimestamp());
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    };

    private final LastfmUrlType type;
    protected final String methodName;
    private final List<LastfmUrlParameterType> parameters;

    public String getEndpoint(LastfmConfig config) {
        return String.format("%s?%s", type.getUrl(config),
                parameters.stream()
                        .map(p -> String.format("%s={%s}", p, p))
                        .collect(Collectors.joining("&")));
    }

    public abstract Map<String, String> buildUrlVariables(LastfmConfig config, LastfmUserParameters parameters);

    protected Map<LastfmUrlParameterType, String> getTrackVariables(LastfmConfig config, LastfmUserParameters parameters) {
        Map<LastfmUrlParameterType, String> variables = new EnumMap<>(LastfmUrlParameterType.class);
        variables.put(album, parameters.getAlbum());
        variables.put(api_key, config.getApiKey());
        variables.put(artist, parameters.getArtist());
        variables.put(duration, parameters.getDuration());
        variables.put(track, parameters.getTrack());
        variables.put(format, "json");
        variables.put(method, methodName);
        variables.put(sk, config.getSessionKey());
        return variables;
    }
}
