package com.spriton.echo.util;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;


public class SpeechletUtil {

    /**
     * Creates and returns the visual and spoken response with shouldEndSession flag.
     *
     * @param title
     *            title for the companion application home card
     * @param output
     *            output content for speech and companion application home card
     * @param shouldEndSession
     *            should the session be closed
     * @return SpeechletResponse spoken and visual response for the given input
     */
    public static SpeechletResponse buildSpeechletResponse(final String type, final String title,
                                                           final String output,
                                                           final String reprompt,
                                                     final boolean shouldEndSession) {

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(output);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);

        // Create the speechlet response.
        SpeechletResponse response = new SpeechletResponse();
        response.setNullableShouldEndSession(shouldEndSession);
        if(reprompt != null && !reprompt.isEmpty()) {
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            speech.setText(reprompt);
            Reprompt repromptResponse = new Reprompt();
            repromptResponse.setOutputSpeech(repromptSpeech);
            response.setReprompt(repromptResponse);
        }
        response.setOutputSpeech(speech);
        response.setCard(card);
        return response;
    }

    public static String getSlot(Intent intent, String slotName) {
        Map<String, Slot> slots = intent.getSlots();
        Slot book = slots.get(slotName);
        String value = null;
        if (book != null && book.getValue() != null) {
            value = book.getValue();
        }
        return value;
    }

    public static Integer getNumberSlot(Intent intent, String slotName) {
        String value = getSlot(intent, slotName);
        if(StringUtils.isNumeric(value)) {
            try {
                return Integer.parseInt(value);
            } catch(Exception ex) {}
        }
        return null;
    }
}
