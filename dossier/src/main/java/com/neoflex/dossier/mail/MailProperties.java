package com.neoflex.dossier.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.mail")
public record MailProperties(
        String host,
        Integer port,
        String username,
        String password,
        String transportProtocol,
        String smtpAuthEnabled,
        String smtpStartTlsEnabled,
        String debugEnabled
) {}
