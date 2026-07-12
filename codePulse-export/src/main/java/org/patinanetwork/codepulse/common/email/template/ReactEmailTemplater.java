package org.patinanetwork.codepulse.common.email.template;

import java.io.IOException;

public interface ReactEmailTemplater {
    /**
     * Load the generated HTML from ClassPathResources as a String then injects variables using Jsoup and renders HTML
     * as a string.
     *
     * @param recipientName
     * @param verifyUrl
     * @param supportEmail
     * @return the rendered HTML as a string
     * @throws IOException
     */
    String createExampleTemplate(String recipientName, String verifyUrl, String supportEmail) throws IOException;

    /**
     * Load the verifyUrl into the school email template.
     *
     * @param verifyUrl
     * @return rendered HTML as a string
     * @throws IOException
     */
    String schoolEmailTemplate(String verifyUrl) throws IOException;

    /**
     * Load the resetUrl into the password reset email template.
     *
     * @param resetUrl
     * @return rendered HTML as a string
     * @throws IOException
     */
    String passwordResetEmailTemplate(String resetUrl) throws IOException;
}
