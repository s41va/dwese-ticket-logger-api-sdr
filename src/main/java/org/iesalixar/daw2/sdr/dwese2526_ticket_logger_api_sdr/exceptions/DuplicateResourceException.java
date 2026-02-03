package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException{

    private final String resource;

    private final String field;

    private final Object value;


    public DuplicateResourceException(String resource, String field, Object value){
        super("Duplicate" + resource + "(" + field + " = " + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
