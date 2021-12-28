package com.nicolasm.bluosscrobbler.scrobbler.lastfm.utils;

import com.google.common.collect.ImmutableMap;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.api_sig;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameterType.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LastfmUrlParametersHelper {
    public static void signCall(LastfmConfig config, Map<LastfmUrlParameterType, String> params) {
        StringBuilder builder = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(e -> e.getKey() != format)
                .forEach(e -> builder.append(e.getKey()).append(e.getValue()));
        builder.append(config.getSharedSecret());
        String apiSig = DigestUtils.md5DigestAsHex(builder.toString().getBytes(StandardCharsets.UTF_8));
        params.put(api_sig, apiSig);
    }

    public static ImmutableMap<String, String> toUrlVariables(Map<LastfmUrlParameterType, String> variables) {
        Map<String, String> urlVariables = variables.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
        return ImmutableMap.copyOf(urlVariables);
    }
}
