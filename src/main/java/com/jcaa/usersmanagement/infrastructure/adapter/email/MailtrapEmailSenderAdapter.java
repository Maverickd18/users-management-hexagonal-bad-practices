package com.jcaa.usersmanagement.infrastructure.adapter.email;

import com.jcaa.usersmanagement.application.port.out.EmailSenderPort;
import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import java.util.List;
import lombok.extern.java.Log;

@Log
public final class MailtrapEmailSenderAdapter implements EmailSenderPort {

  private final MailtrapClient client;
  private final String fromAddress;
  private final String fromName;

  public MailtrapEmailSenderAdapter(final SmtpConfig config) {
    this.fromAddress = config.fromAddress();
    this.fromName = config.fromName();
    
    final MailtrapConfig.Builder builder = new MailtrapConfig.Builder()
        .token(config.password());

    if (config.sandbox()) {
        builder.sandbox(true);
        if (config.inboxId() != null) {
            // Some SDK versions use this, or it's handled via the token
        }
    }
        
    this.client = MailtrapClientFactory.createMailtrapClient(builder.build());
  }

  @Override
  public void send(final EmailDestinationModel destination) {
    final MailtrapMail mail = MailtrapMail.builder()
        .from(new Address(fromAddress, fromName))
        .to(List.of(new Address(destination.getDestinationEmail(), destination.getDestinationName())))
        .subject(destination.getSubject())
        .html(destination.getBody())
        .build();

    try {
      client.send(mail);
      log.info("Email sent via Mailtrap SDK to: " + destination.getDestinationEmail());
    } catch (final Exception exception) {
      throw EmailSenderException.becauseSmtpFailed(
          destination.getDestinationEmail(), exception.getMessage());
    }
  }
}
