package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions;

import javax.management.RuntimeMBeanException;

public class InvalidFileException extends RuntimeException {
    private final String resource;

    private final String field;

    private final Object value;


    public InvalidFileException(String resource, String field, Object value){
        super("Invalid file fot" + resource + "(" + field + " = " + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public InvalidFileException(String resource, String field, Object value, String detail){
        super("Invalid file fot" + resource + "(" + field + " = " + ")" + detail);
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
