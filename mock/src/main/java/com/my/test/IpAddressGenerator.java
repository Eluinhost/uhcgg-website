package com.my.test;

import org.databene.benerator.distribution.sequence.RandomIntegerGenerator;
import org.databene.benerator.util.AbstractNonNullGenerator;

import java.net.Inet6Address;

public class IpAddressGenerator extends AbstractNonNullGenerator<String> {
    private final RandomIntegerGenerator integerGenerator;

    public IpAddressGenerator() {
        this.integerGenerator = new RandomIntegerGenerator(0, 255, 1);
    }

    public String generate() {
        return integerGenerator.generate() + "." + integerGenerator.generate() + "." + integerGenerator.generate() + "." + integerGenerator.generate();
    }


    public Class<String> getGeneratedType() {
        return String.class;
    }

    public boolean isParallelizable() {
        return integerGenerator.isParallelizable();
    }

    public boolean isThreadSafe() {
        return integerGenerator.isThreadSafe();
    }
}
