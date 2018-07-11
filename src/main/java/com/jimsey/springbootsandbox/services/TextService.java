package com.jimsey.springbootsandbox.services;

import java.util.UUID;

public class TextService {

    public String generateText() {
        return UUID.randomUUID().toString();
    }

}
