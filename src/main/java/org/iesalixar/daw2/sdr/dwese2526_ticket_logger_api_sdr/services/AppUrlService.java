package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.services;

import java.util.Map;

public interface AppUrlService {
    String buildResetUrl(String rawToken);
    String buildUrl(String path, Map<String, String> queryParams);

}
