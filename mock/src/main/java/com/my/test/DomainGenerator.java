package com.my.test;

import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import org.databene.benerator.util.AbstractNonNullGenerator;

public class DomainGenerator extends AbstractNonNullGenerator<String> {
    private final Internet internet;

    public DomainGenerator() {
        internet = new Faker().internet();
    }

    public String generate() {
        return internet.domainName();
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    public boolean isParallelizable() {
        return true;
    }

    public boolean isThreadSafe() {
        return true;
    }
}
