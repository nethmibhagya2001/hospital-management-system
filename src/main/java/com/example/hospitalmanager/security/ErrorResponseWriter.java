package com.example.hospitalmanager.security;

import com.example.hospitalmanager.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseWriter {
    public void write(
            HttpServletResponse response, HttpStatus status, String message, String path)
            throws IOException {
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"timestamp\":" + jsonString(error.timestamp().toString())
                        + ",\"status\":" + error.status()
                        + ",\"error\":" + jsonString(error.error())
                        + ",\"message\":" + jsonString(error.message())
                        + ",\"path\":" + jsonString(error.path())
                        + "}");
    }

    private String jsonString(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder escaped = new StringBuilder(value.length() + 2).append('"');
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append("\\u%04x".formatted((int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.append('"').toString();
    }
}
