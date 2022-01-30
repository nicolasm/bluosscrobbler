package com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils;

import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.MapAssert;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.EnumMap;
import java.util.Map;

import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.album;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.api_key;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.api_sig;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.artist;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.format;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.method;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.sk;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.track;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class LastfmUrlParametersHelperTest {
    private static final String SHARED_SECRET = "shared-secret";
    private static final String ARTIST = "Cameron Mizell & Charlie Rauh";
    private static final String ALBUM = "Local Folklore";
    private static final String TRACK = "Arolen";
    private static final String FORMAT = "json";
    private static final String METHOD = "track.updateNowPlaying";
    private static final String API_KEY = "api-key";
    private static final String SESSION_KEY = "session-key";

    @Test
    public void signCall() {
        LastfmConfig config = new LastfmConfig();
        config.setSharedSecret(SHARED_SECRET);
        Map<LastfmUrlParameterType, String> params = buildParametersMap();

        LastfmUrlParametersHelper.signCall(config, params);
        new MapAssert<>(params).containsEntry(api_sig, getExpectedSignature());
    }

    private Map<LastfmUrlParameterType, String> buildParametersMap() {
        Map<LastfmUrlParameterType, String> params = new EnumMap<>(LastfmUrlParameterType.class);
        params.put(artist, ARTIST);
        params.put(album, ALBUM);
        params.put(track, TRACK);
        params.put(format, FORMAT);
        params.put(method, METHOD);
        params.put(api_key, API_KEY);
        params.put(sk, SESSION_KEY);
        return params;
    }

    /**
     * @see "Sign your calls section at https://www.last.fm/api/webauth"
     */
    private String getExpectedSignature() {
        return DigestUtils.md5DigestAsHex(String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s",
                album, ALBUM,
                api_key, API_KEY,
                artist, ARTIST,
                method, METHOD,
                sk, SESSION_KEY,
                track, TRACK,
                SHARED_SECRET).getBytes(UTF_8));
    }

}