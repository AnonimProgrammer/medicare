package com.ironhack.infra.config.jackson;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Accepts standard {@link OffsetDateTime} strings, or a local ISO-8601 datetime
 * (e.g. {@code 2026-05-21T13:00}) using the JVM default zone.
 */
public class FlexibleOffsetDateTimeDeserializer extends ValueDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String text = p.getValueAsString();
        if (text == null || text.isBlank()) {
            return null;
        }
        text = text.trim();
        try {
            return OffsetDateTime.parse(text);
        } catch (DateTimeParseException ignored) {
            // fall through
        }
        try {
            LocalDateTime local = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return local.atZone(ZoneId.systemDefault()).toOffsetDateTime();
        } catch (DateTimeException e) {
            throw ctxt.weirdStringException(text, OffsetDateTime.class, e.getMessage());
        }
    }
}
