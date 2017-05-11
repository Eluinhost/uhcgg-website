package com.my.test;

import org.databene.commons.ConversionException;
import org.databene.commons.converter.ThreadSafeConverter;
import org.mindrot.jbcrypt.BCrypt;

public class BcryptConverter extends ThreadSafeConverter<String, String> {
    public BcryptConverter() {
        super(String.class, String.class);
    }

    public String convert(String s) throws ConversionException {
        return BCrypt.hashpw(s, BCrypt.gensalt());
    }
}
