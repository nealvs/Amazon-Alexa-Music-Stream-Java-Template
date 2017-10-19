package com.spriton.echo.endpoints;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.interfaces.audioplayer.*;
import com.amazon.speech.speechlet.interfaces.audioplayer.directive.*;
import com.amazon.speech.speechlet.interfaces.playbackcontroller.PlaybackController;
import com.amazon.speech.speechlet.interfaces.playbackcontroller.request.*;
import com.amazon.speech.speechlet.interfaces.system.request.ExceptionEncounteredRequest;
import com.spriton.echo.util.SpeechletUtil;
import org.slf4j.*;

import com.amazon.speech.slu.*;
import com.amazon.speech.speechlet.*;

import java.util.ArrayList;

public class MusicSpeechlet implements SpeechletV2, PlaybackController, com.amazon.speech.speechlet.interfaces.system.System {

    private static final Logger logger = LoggerFactory.getLogger(MusicSpeechlet.class);
    private String name;
    private String token;
    private String streamUrl;

    public MusicSpeechlet(String name, String token, String streamUrl) {
        this.name = name;
        this.token = token;
        this.streamUrl = streamUrl;
    }

    public Directive getPlayDirective() {
        Stream stream = new Stream();
        stream.setToken(token);
        stream.setUrl(streamUrl);

        AudioItem audioItem = new AudioItem();
        audioItem.setStream(stream);

        PlayDirective directive = new PlayDirective();
        directive.setPlayBehavior(PlayBehavior.REPLACE_ALL);
        directive.setAudioItem(audioItem);
        return directive;
    }

    // SpeechletV2 Handlers
    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        logRequestInfo("onLaunch Launch", requestEnvelope);

        String speechOutput = "This is a live " + name + " radio stream. ";
        speechOutput += " You can stop the stream at any time by saying, Alexa, stop.";
        SpeechletResponse response = SpeechletUtil.buildSpeechletResponse(name, name, speechOutput, null, true);
        response.setDirectives(new ArrayList<>());
        response.getDirectives().add(getPlayDirective());

        return response;
    }

    private SpeechletResponse getHelpResponse(Intent intent, String name, Session session) {
        String speechOutput = "This is a live " + name + " radio stream. ";
        String reprompt = "You can start the audio stream by saying, play, and stop the audio stream by saying, stop.  What would you like to do?";
        speechOutput += reprompt;
        return SpeechletUtil.buildSpeechletResponse(name, name, speechOutput, reprompt, false);
    }

    public SpeechletResponse getInfoResponse() {
        String speechOutput = "This is a live " + name + " radio stream. You can not skip, shuffle, or repeat audio.";
        return SpeechletUtil.buildSpeechletResponse(name, name, speechOutput, null, true);
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        logRequestInfo("onIntent " + intentName, requestEnvelope);

        SpeechletResponse response = null;
        try {
            switch (intentName) {
                case "PlayAudio":
                case "AMAZON.ResumeIntent":
                    response = getStartResponse();
                    break;
                case "AMAZON.NextIntent":
                case "AMAZON.PreviousIntent":
                case "AMAZON.ShuffleOnIntent":
                case "AMAZON.ShuffleOffIntent":
                case "AMAZON.LoopOnIntent":
                case "AMAZON.LoopOffIntent":
                case "AMAZON.RepeatIntent":
                case "AMAZON.StartOverIntent":
                    response = getInfoResponse();
                    break;
                case "AMAZON.PauseIntent":
                case "AMAZON.StopIntent":
                case "AMAZON.CancelIntent":
                    response = getStopResponse();
                    break;
                case "AMAZON.HelpIntent":
                    response = getHelpResponse(intent, name, session);
                    break;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            logger.error("Error handling intent=" + intentName, ex);
        }

        if(response == null) {
            logger.info("Invalid intent=" + intentName);
            return null;
        } else {
            return response;
        }
    }

    private SpeechletResponse getStopResponse() {
        StopDirective directive = new StopDirective();
        SpeechletResponse response = new SpeechletResponse();
        response.setNullableShouldEndSession(true);
        response.setDirectives(new ArrayList<>());
        response.getDirectives().add(directive);
        return response;
    }

    private SpeechletResponse getStartResponse() {
        SpeechletResponse response = new SpeechletResponse();
        response.setNullableShouldEndSession(true);
        response.setDirectives(new ArrayList<>());
        response.getDirectives().add(getPlayDirective());
        return response;
    }


    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        logRequestInfo("onSessionStarted", requestEnvelope);
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        logRequestInfo("onSessionEnded", requestEnvelope);
    }

    public void logRequestInfo(String action, SpeechletRequestEnvelope envelope) {
        logger.info("ACTION={}", action);
    }

    // PlaybackController Handlers
    @Override
    public SpeechletResponse onPauseCommandIssued(SpeechletRequestEnvelope<PauseCommandIssuedRequest> requestEnvelope) {
        logRequestInfo("onPauseCommandIssued", requestEnvelope);
        return getStopResponse();
    }
    @Override
    public SpeechletResponse onPlayCommandIssued(SpeechletRequestEnvelope<PlayCommandIssuedRequest> requestEnvelope) {
        logRequestInfo("onPlayCommandIssued", requestEnvelope);
        return getStartResponse();
    }
    @Override
    public SpeechletResponse onNextCommandIssued(SpeechletRequestEnvelope<NextCommandIssuedRequest> requestEnvelope) {
        logRequestInfo("onNextCommandIssued", requestEnvelope);
        return getStartResponse();
    }
    @Override
    public SpeechletResponse onPreviousCommandIssued(SpeechletRequestEnvelope<PreviousCommandIssuedRequest> requestEnvelope) {
        logRequestInfo("onPreviousCommandIssued", requestEnvelope);
        return getStartResponse();
    }

    // System Handlers
    @Override
    public void onExceptionEncountered(SpeechletRequestEnvelope<ExceptionEncounteredRequest> requestEnvelope) {
        logRequestInfo("onExceptionEncountered", requestEnvelope);
        logger.error(requestEnvelope.getRequest().getError().getType().name() + ": " + requestEnvelope.getRequest().getError().getMessage());
    }

}
