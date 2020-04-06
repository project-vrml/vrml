package com.ten.func.vavr.alert.actor.email;

import com.ten.func.vavr.alert.actor.AbstractAlertActor;
import com.ten.func.vavr.alert.actor.AlertMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The email alert actor.
 */
@Slf4j
@Component
public class EmailAlertActor extends AbstractAlertActor<EmailAlertActor.EmailAlertMessage> {

    /**
     * The email alert message.
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class EmailAlertMessage implements AlertMessage {
        private List<String> recipientList;
        private String title;
        private String content;
    }

    @Override
    protected void onReceive(EmailAlertMessage message) {
        this.sendEmail(message.getRecipientList(), message.getTitle(), message.getContent());
    }

    /**
     * Send email by co interface
     *
     * @param recipientList recipient email address
     * @param title         email title
     * @param content       email content
     */
    private void sendEmail(List<String> recipientList, String title, String content) {
    }
}
