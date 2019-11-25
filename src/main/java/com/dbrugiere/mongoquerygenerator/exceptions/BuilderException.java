package com.dbrugiere.mongoquerygenerator.exceptions;

public class BuilderException extends RuntimeException {

    public BuilderException(){
        super("Your request is not in the right format");
    }
}
