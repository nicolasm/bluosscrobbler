package com.nicolasm.bluosscrobbler.scrobbler.lastfm.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.nicolasm.bluosscrobbler.scrobbler.lastfm.config.LastfmConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameters.*;
import static com.nicolasm.bluosscrobbler.scrobbler.lastfm.model.LastfmUrlParameters.token;

@SuppressWarnings("squid:S00115")
@RequiredArgsConstructor
public enum LastfmAuthEndpoint {
    auth_getToken(LastfmUrlType.API, ImmutableList.of(api_key, api_sig, format, method)) {
        @Override
        public Map<String, Object> buildUrlVariables(LastfmConfig config, String... token) {
            Map<LastfmUrlParameters, Object> variables = new EnumMap<>(LastfmUrlParameters.class);
            variables.put(api_key, config.getApiKey());
            variables.put(format, "json");
            variables.put(method, "auth.getToken");
            signCall(config, variables);

            return toUrlVariables(variables);
        }
    },
    request_auth(LastfmUrlType.AUTH, ImmutableList.of(api_key, token)) {
        @Override
        public Map<String, Object> buildUrlVariables(LastfmConfig config, String... tokenValue) {
            Map<LastfmUrlParameters, Object> variables = new EnumMap<>(LastfmUrlParameters.class);
            variables.put(api_key, config.getApiKey());
            variables.put(token, tokenValue[0]);
            signCall(config, variables);

            return toUrlVariables(variables);

        }
    },
    auth_getSession(LastfmUrlType.API, ImmutableList.of(api_key, api_sig, format, method, token)) {
        @Override
        public Map<String, Object> buildUrlVariables(LastfmConfig config, String... tokenValue) {
            Map<LastfmUrlParameters, Object> variables = new EnumMap<>(LastfmUrlParameters.class);
            variables.put(api_key, config.getApiKey());
            variables.put(format, "json");
            variables.put(method, "auth.getSession");
            variables.put(token, tokenValue[0]);
            signCall(config, variables);

            return toUrlVariables(variables);

        }
    };

    private final LastfmUrlType type;
    private final List<LastfmUrlParameters> parameters;

    public String getEndpoint(LastfmConfig config) {
        return String.format("%s?%s", type.getUrl(config),
                parameters.stream()
                        .map(p -> String.format("%s={%s}", p.toString(), p.toString()))
                        .collect(Collectors.joining("&")));
    }

    public abstract Map<String, Object> buildUrlVariables(LastfmConfig config, String... token);

    public static Map<String, Object> buildRequestAuthUrlVariables(LastfmConfig config, String tokenValue) {
        Map<LastfmUrlParameters, Object> variables = new EnumMap<>(LastfmUrlParameters.class);
        variables.put(api_key, config.getApiKey());
        variables.put(token, tokenValue);
        signCall(config, variables);

        return toUrlVariables(variables);
    }

    public static Map<String, Object> buildAuthGetSessionUrlVariables(LastfmConfig config, String tokenValue) {
        Map<LastfmUrlParameters, Object> variables = new EnumMap<>(LastfmUrlParameters.class);
        variables.put(api_key, config.getApiKey());
        variables.put(format, "json");
        variables.put(method, "auth.getSession");
        variables.put(token, tokenValue);
        signCall(config, variables);

        return toUrlVariables(variables);
    }

    private static void signCall(LastfmConfig config, Map<LastfmUrlParameters, Object> params) {
        StringBuilder builder = new StringBuilder();
        params.entrySet()
                .stream().filter(e -> e.getKey() != format)
                .forEach(e -> builder.append(e.getKey()).append(e.getValue()));
        builder.append(config.getSharedSecret());
        String apiSig = DigestUtils.md5DigestAsHex(builder.toString().getBytes(StandardCharsets.UTF_8));
        params.put(api_sig, apiSig);
    }

    private static ImmutableMap<String, Object> toUrlVariables(Map<LastfmUrlParameters, Object> variables) {
        Map<String, Object> urlVariables = variables.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
        return ImmutableMap.copyOf(urlVariables);
    }
}
