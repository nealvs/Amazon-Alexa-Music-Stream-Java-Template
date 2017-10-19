package com.spriton.echo.endpoints;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

// AWS Lambda Handler: com.spriton.echo.endpoints.MusicStreamRequestHandler
public final class MusicStreamRequestHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds = new HashSet<>();

    static {
        supportedApplicationIds.add("<Your Skill Id>");
    }

    public MusicStreamRequestHandler() {
        super(new MusicSpeechlet(
                        "CNN Live Stream",
                        "cnn-live-stream",
                        "http://tunein.streamguys1.com/cnn?aw_0_1st.playerid=RadioTime&aw_0_1st.skey=1508387952"),
                supportedApplicationIds);
    }
}
