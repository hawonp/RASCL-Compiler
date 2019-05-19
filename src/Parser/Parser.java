/*
    Author: Hawon Park
    SBUID: 110983842
    Email: hawon.park@stonybrook.edu

    This "Parser.java" file takes an input file and
        1) Generates a list of tokens using a lexical analyzer
        2) Parses through the token list
        3) Prints out the production output into a file

    Parser will only parse RASCL files!
 */

import Lexer.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    //instance variables
    private ArrayList<tokenInfo> tokenBuffer;       //used to store the token stream from the Lexical Analyzer
    private int lookahead;                          //index for getting the current and next tokens in the tokenBuffer
    private String output;                          //temporary string to store output

    //constructor
    public Parser(String fileName) throws IOException {
        //initialize instance variables and token buffer
        lookahead = 0;
        tokenBuffer = buildLexer(fileName);
        output = "";
        String file = "";

        //IF: command line input is file within the same directory, no need to strip excess path
        if(fileName.lastIndexOf("/") == -1)
          file = fileName + "_OUTPUT.txt";
        //ELSE: get only filename
        else
          file = fileName.substring(fileName.lastIndexOf("/") )+ "_OUTPUT.txt";

        BufferedWriter writer = new BufferedWriter(new FileWriter("./output/" +file));

        //Call program() to start the parser
        System.out.println("\t-(Parser) Start looking through productions");
        program();
        System.out.println("\t-(Parser) Finished parsing token stream");

        //write output text to file and close bufferedwriter
        System.out.println("\t-(Parser) Write to output file");
        writer.append(output);
        writer.close();
    }

    //method used to generate token stream
    private ArrayList<tokenInfo> buildLexer(String fileName){
        Lexer lex = new Lexer();
        int check = lex.initLexer(fileName);

        //if lexical analyzer successfully opens file, build token buffer
        System.out.println("\t-(Parser) init Lexer with input file: " + check);
        if(check == 1) {
            System.out.println("\t-(Parser) build token buffer");
            lex.buildBuffer();
            return lex.getResult();
        }
        //lexical analyzer could not open the file
        System.out.println("\t-(Parser) could not read file to Lexer");
        return null;
    }

    //--------------- THESE METHODS FORM A (NON TABLE DRIVEN) PREDICTIVE RECURSIVE DESCENT PARSER ---------------//
    /*
      general note: whenever "lookahead" is incremented, that means that you are moving
      to the next token in the token buffer
    */

    private void program(){
        output+="program -> decllist bstatementlist DD\n";
        decllist();
        bstatementlist();

        //if current token is "DD", parser is complete and parser can output "DONE" message
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("DD")){
            output+="Done\n";
        }
    }

    private void decllist(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        //"peer" into typespec production to decide between epsilon production and normal production
        if(curr.equals("INT") || curr.equals("FLOAT")){
            output+="decllist -> decl decllist\n";
            decl();
            decllist();
        }
        else {
            output += "decllist -> e\n";
        }
    }

    private void bstatementlist(){
        output+="bstatementlist -> LBRACE statementlist RBRACE\n";
        lookahead++;
        statementlist();
        lookahead++;

    }

    private void statementlist(){
        //get current token to differentiate between WHILE, IF, PRINT, READ
        //get next current token to check for ASSIGN statement
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String next = tokenBuffer.get(lookahead+1).getTokenType().toString();

        //used for specific 1D array assignments
        String twoTokenAfter  ="";
        String threeTokenAfter = "";
        String fourTokenAfter = "";

        //make sure lookahead not out of bounds of token stream
        if(lookahead+5 <= tokenBuffer.size()) {
            twoTokenAfter = tokenBuffer.get(lookahead+2).getTokenType().toString();
            threeTokenAfter = tokenBuffer.get(lookahead + 3).getTokenType().toString();
            fourTokenAfter = tokenBuffer.get(lookahead + 4).getTokenType().toString();
        }
        //use tokens to decide which production rule to call
        if(curr.equals("WHILE") || curr.equals("IF") || next.equals("ASSIGN") || curr.equals("PRINT") || curr.equals("READ")){
            output+="statementlist -> statement SEMICOLON statementlist\n";
            statement();
            lookahead++;
            statementlist();
        }
        //use multiple lookaheads to check for an 1-D array assignment
        else if(next.equals("LBRACKET") && threeTokenAfter.equals("RBRACKET") && fourTokenAfter.equals("ASSIGN")){
            output+="statementlist -> statement SEMICOLON statementlist\n";
            output+="statement -> assignmentexpression\n";
            //skip calling statement() and go directly to assignmentexpression()
            assignmentexpression();
            lookahead++;
            statementlist();
        }
        else {
            output+="statementlist -> e\n";
        }
    }

    private void decl(){
        output+="decl -> typespec variablelist\n";
        typespec();
        variablelist();
    }

    private void variablelist(){
        output+="variablelist -> variable variablelisttail\n";
        variable();
        variablelisttail();
    }

    private void variablelisttail(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("COMMA")){
            output+="variablelisttail -> COMMA variable variablelisttail\n";
            lookahead++;
            variable();
            variablelisttail();
        }
        else{
            output+="variablelisttail -> SEMICOLON\n";
            lookahead++;
        }
    }

    private void typespec() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("INT")) {
            output+="typespec -> INT\n";
        }
        else{
            output+="typespec -> FLOAT\n";
        }
        lookahead ++;
    }

    private void variable(){
        output+="variable -> ID variabletail\n";
        lookahead++;
        variabletail();

    }

    private void variabletail() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("LBRACKET")) {
            output += "variabletail -> arraydim\n";
            arraydim();
        }
        else {
            output+="variabletail -> e\n";
        }
    }

    private void arraydim(){
        output+="arraydim -> LBRACKET arraydimtail\n";
        lookahead++;
        arraydimtail();
    }

    private void arraydimtail() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("ID")) {
            output+="arraydimtail -> ID RBRACKET\n";
        }
        else{
            output+="arraydimtail -> ICONST RBRACKET\n";
        }
        lookahead+=2;
    }

    private void statement(){
        //get current token which would work for WHILE, IF, PRINT, READ statements
        //get next token which is requirement for assignment operators
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String next = tokenBuffer.get(lookahead+1).getTokenType().toString();

        if(curr.equals("WHILE")) {
            output+="statement -> whilestatement\n";
            whilestatement();
        }
        else if(curr.equals("IF")) {
            output+="statement -> ifstatement\n";
            ifstatement();
        }
        else if(next.equals("ASSIGN")) {
            output+="statement -> assignmentexpression\n";
            assignmentexpression();
        }
        else if(curr.equals("PRINT")) {
            output+="statement -> printexpression\n";
            printexpression();
        }
        else {
            output+="statement -> readstatement\n";
            readstatement();
        }
    }

    private void assignmentexpression(){
        output+="assignmentexpression -> variable ASSIGN otherexpression\n";
        variable();
        lookahead++;
        otherexpression();
    }

    private void printexpression(){
        output+="printexpression -> PRINT variable\n";
        lookahead++;
        variable();
    }

    private void otherexpression(){
        output+="otherexpression -> term otherexpressiontail\n";
        term();
        otherexpressiontail();
    }

    private void otherexpressiontail(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("PLUS") || curr.equals("MINUS")){
            output+="otherexpressiontail -> PLUS term otherexpressiontail\n";
            output+="   or\n";
            output+="otherexpressiontail -> MINUS term otherexpressiontail\n";
            lookahead++;
            term();
            otherexpressiontail();
        }
        else {
            output+="otherexpressiontail -> e\n";
        }
    }

    private void term(){
        output+="term -> factor termtail\n";
        factor();
        termtail();
    }

    private void termtail(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("MULT") || curr.equals("DIV")){
            output+="termtail -> MULT factor termtail\n";
            output+="   or\n";
            output+="termtail -> DIV factor termtail\n";

            lookahead++;
            factor();
            termtail();
        }
        else {
            output+="termtail -> e\n";
        }
    }

    private void factor(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("ICONST")){
            output+="factor -> ICONST\n";
            lookahead++;
        }
        else if(curr.equals("FCONST")){
            output+="factor -> FCONST\n";
            lookahead++;
        }
        else if(curr.equals("LPAREN")){
            output+="factor -> LPAREN otherexpression RPAREN\n";
            lookahead++;
            otherexpression();
            lookahead++;
        }
        else if(curr.equals("MINUS")){
            output+="factor -> MINUS factor\n";
            lookahead++;
            factor();
        }
        else{
            output+="factor -> variable\n";
            variable();
        }
    }

    private void whilestatement(){
        output+="whilestatement -> WHILE condexpr whiletail\n";
        lookahead++;
        condexpr();
        whiletail();
    }

    private void whiletail(){
        output+="whiletail -> compoundstatement\n";
        compoundstatement();
    }

    private void compoundstatement(){
        output+="compoundstatement -> LBRACE statementlist RBRACE\n";
        lookahead++;
        statementlist();
        lookahead++;
    }

    private void ifstatement(){
        output+="ifstatement -> IF condexpr compoundstatement istail\n";
        lookahead++;
        condexpr();
        compoundstatement();
        istail();
    }

    private void istail() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("ELSE")) {
            output += "istail -> ELSE compoundstatement\n";
            lookahead++;
            compoundstatement();
        }
        else {
            output+="istail -> e\n";
        }
    }

    private void condexpr(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("LPAREN")){
            output+="condexpr -> LPAREN vorc condexprtail RPAREN\n";
            lookahead++;
            vorc();
            condexprtail();
            lookahead++;
        }
        else if(curr.equals("NOT")){
            output+="condexpr -> NOT condexpr\n";
            lookahead++;
            condexpr();
        }
        else{
            output+="condexpr -> vorc condexprtail\n";
            vorc();
            condexprtail();
        }
    }

    private void condexprtail(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("LT")){
           output+="condexprtail -> LT vorc\n";
           lookahead++;
           vorc();
        }
        else if(curr.equals("GT")){
            output+="condexprtail -> GT vorc\n";
            lookahead++;
            vorc();
        }
        else {
            output+="condexprtail -> LT vorc\n";
            lookahead++;
            vorc();
        }
    }

    private void vorc(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("ICONST")){
            output+="vorc -> ICONST\n";
            lookahead++;
        }
        else if(curr.equals("FCONST")){
            output+="vorc -> FCONST\n";
            lookahead++;
        }
        else {
            output+="vorc -> variable\n";
            variable();
        }
    }

    private void readstatement(){
        output+="readstatement -> READ variable\n";
        lookahead++;
        variable();
    }

}//CLASS BRACKET
