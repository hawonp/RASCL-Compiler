package Lexer;
/*
    Author: Hawon Park (11083842) hawon.park@stonybrook.edu
    Prog:   Lexer is a class that generates a input buffer and calls getNextToken() on the buffer to parse the input file
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Lexer {

    //instance variables
    private String buffer;                          //buffer to hold the input file
    private ArrayList<tokenInfo> result;            //list to store the input file tokens

    //constructor
    public Lexer(){
        buffer = "";
        result = new ArrayList<>();
    }

    //initialize the lexer by generating an input buffer, return 1 for success, 0 if otherwise
    public int initLexer(String fileName){
        try{
            buffer = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
            return 1;
        } catch (IOException e){
            System.out.println("ERROR: file not found!");
            return 0;
        }
    }

    public ArrayList<tokenInfo> getResult() { return this.result; }

    //gets the longest possible token at the head of the buffer
    private tokenInfo getNextToken() {
        //INITIALIZE VARIABLES TO BE USED IN getNextToken()
        int temp;
        int max = 0;
        PotentialToken pt = new PotentialToken();

        //PROCESS LONGEST AVAILABLE TOKEN
        tokenInfo finalToken = null;
        tokenInfo tempToken;

        //DETECT COMMENT
        tempToken = pt.isComment(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT OPERATORS
        tempToken = pt.isOp(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT KEYWORDS
        tempToken = pt.isKey(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT PUNCTUATION
        tempToken = pt.isPunct(buffer);
        if(tempToken != null){
            temp = 1;
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT SCIENTIFIC FORM FLOATS
        tempToken = pt.isContantFloat(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT FRACTIONAL FORM FLOATS
        tempToken = pt.isNormalFloat(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT INTEGERS
        tempToken = pt.isInteger(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT IDENTIFIERS
        tempToken = pt.isID(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //DETECT SINGLE CHARACTER INVALIDS
        tempToken = pt.isInvalidChar(buffer);
        if(tempToken != null){
            temp = tempToken.getTokenText().length();
            if(temp > max) {
                max = temp;
                finalToken =  tempToken;
            }
        }

        //if buffer is empty, this means lexer successfully reached end of file and return DD
        if(buffer.isEmpty()){
            return new tokenInfo("end of input", tokenInfo.Type.DD);
        }
        //if token is a negative float/integer, add a MINUS token to result before return the new float/int token
        else if(
                finalToken.getTokenText().substring(0,1).equals("-") &&
                        (
                        finalToken.getTokenType().toString().equals("ICONST") ||
                        finalToken.getTokenType().toString().equals("FCONST")
                        )
                )
            result.add(new tokenInfo("-",tokenInfo.Type.MINUS));

        //trim input buffer (no longer need token)
        buffer = buffer.substring(max).trim();

        //if there was no token found, return the INVALID token and get rid of the first character in the buffer
        if(finalToken== null) {
            if(buffer.length()>0){
                String letter = buffer.substring(0,1);
                buffer = buffer.substring(1);
                return new tokenInfo(letter, tokenInfo.Type.INVALID);
            }
            return new tokenInfo(buffer, tokenInfo.Type.INVALID);
        }
        //return longest possible token found by checking for all possible tokens
        else
            return finalToken;

    }

    //parse the buffer by repeatedly calling getNextToken() till end of file is reached
    public void buildBuffer(){
        System.out.println("\t\t(Lexer) building token buffer with getNextToken()");

        //process buffer and add discovered token to result list
        while(!buffer.isEmpty()){
            tokenInfo current = getNextToken();
            result.add(current);
        }

        result.add(new tokenInfo("", tokenInfo.Type.DD));

        //print result list
//        for(tokenInfo x : result){
//            String type = x.getTokenType().toString();
//            if(type.equals("ICONST") || type.equals("FCONST") || type.equals("ID")){
//                System.out.println(type + ": " + x.getTokenText());
//            }
//            else{
//                System.out.println(x.getTokenType().toString());
//            }
//        }
    }//parseBuffer bracket

}//CLASS BRACKET
