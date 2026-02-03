package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    private final String resource;

    private final String field;

    private final Object value;


    public ResourceNotFoundException(String resource, String field, Object value){
        super(resource + " not found (" + field + " = " + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
