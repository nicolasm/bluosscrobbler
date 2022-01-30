package com.nicolasm.bluosscrobbler.testutils;

import com.google.common.io.Resources;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class TestUtils {
    public static String readResourceToString(String path) throws IOException {
        return Resources.toString(getResource(path), Charset.defaultCharset());
    }
}
