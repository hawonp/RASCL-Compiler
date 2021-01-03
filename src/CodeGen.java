// Author:  Hawon park
// Purpose: This file generates intermediate code based on provided RASCL code

import Lexer.*;
import Table.*;

import java.io.IOException;
import java.util.ArrayList;

public class CodeGen {

    private ArrayList<tokenInfo> tokenBuffer;
    private SymbolTable st;
    private int lookahead;
    private String output;
    private String inter_code;
    private int reg_num;


    public CodeGen(String fileName) throws IOException {
        //initialize instance variables and token buffer
        lookahead = 0;
        output = "";
        inter_code = "";
        reg_num = 0;

        System.out.println("(1) Get Token Buffer from Lexer");
        tokenBuffer = buildLexer(fileName);

        System.out.println("(2) Initialize Symbol Table");
        st = new SymbolTable();
        st.initSymTab();

        System.out.println("(3) Start Code Generation");
        program();

        System.out.println("\n" + inter_code);
//        System.out.println("\n" + output);
    }

    //method used to generate token stream
    private ArrayList<tokenInfo> buildLexer(String fileName){
        Lexer lex = new Lexer();
        int check = lex.initLexer(fileName);

        //if lexical analyzer successfully opens file, build token buffer
//        System.out.println("\t-(Parser) init Lexer with input file wew:" + check);
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

        System.out.println("\tGet Declaration List & Build Symbol Table");
        inter_code+=".segment, 0, 0, .data\n";
        decllist();

        System.out.println("\tGet B-Statement List");
        inter_code+="\n\n.segment, 0, 0, .text\n";
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
        if(curr.equals("INT")){
            output+="decllist -> decl decllist\n";
            decl("int");
            decllist();
        }
        else if (curr.equals("FLOAT")){
            output+="decllist -> decl decllist\n";
            decl("float");
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
        String threeTokenAfter = "";
        String fourTokenAfter = "";

        //make sure lookahead not out of bounds of token stream
        if(lookahead+5 <= tokenBuffer.size()) {
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
            inter_code += "# Start ASSIGN statement ---\n";
            assignmentexpression(true);
            lookahead++;
            statementlist();
        }
        else {
            output+="statementlist -> e\n";
        }
    }

    private void decl(String type){
        output+="decl -> typespec variablelist\n";
        typespec();
        variablelist(type, true);
    }

    private void variablelist(String type, boolean decllist){
        output+="variablelist -> variable variablelisttail\n";
        variable(type, decllist);
        variablelisttail(type, decllist);
    }

    private void variablelisttail(String type, boolean decllist){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("COMMA")){
            output+="variablelisttail -> COMMA variable variablelisttail\n";
            lookahead++;
            variable(type, decllist);
            variablelisttail(type, decllist);
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

    private void variable(String type, boolean decllist){
        output+="variable -> ID variabletail\n";
        tokenInfo temp = tokenBuffer.get(lookahead);

        //add symbol to symbol table
        st.addSymbol(temp.getTokenText());

        lookahead++;
        variabletail(type, temp.getTokenText(), decllist);

    }

    private void variabletail(String type, String text, boolean decllist) {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        //array declaration
        if (curr.equals("LBRACKET")) {
            output += "variabletail -> arraydim\n";
            arraydim(type, text, decllist);
        }
        //primitive type declaration
        else {
            if(decllist){
                inter_code+="." + type + ", 0, 1, " + text + "\n";
                st.addAttributeToSymbol(text, 2, type);
            } else {
//                inter_code+="lw, " + text+ ", 0, T" + reg_num+ "\n";
            }

            output+="variabletail -> e\n";
        }
    }

    private void arraydim(String type, String text, boolean decllist){
        output+="arraydim -> LBRACKET arraydimtail\n";
        lookahead++;
        arraydimtail(type, text, decllist);
    }

    private void arraydimtail(String type, String text, boolean decllist) {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String num = tokenBuffer.get(lookahead).getTokenText();

        if (curr.equals("ID")) {
            output+="arraydimtail -> ID RBRACKET\n";
            inter_code += "lw, " + num + ", 0, T" + (reg_num) + "\n";
            inter_code += "sl, T" + reg_num + ", 2, T" + (reg_num++) + "\n";
            inter_code += "la, arr, 0, T" + (reg_num++) + "\n";
            inter_code += "add, T" + (reg_num-2) + ", T" + (reg_num-1) + ", T" + (reg_num) + "\n";
            reg_num++;
        }
        else{
//            TODO confirm that you put array for array types int symbol table
            if(decllist){
                st.addAttributeToSymbol(text, 2, "array");
                inter_code += "." + type + ", 0, " + num + ", " + text + "\n";
            } else {
                String temp = tokenBuffer.get(lookahead-2).getTokenText();

                int x = Integer.parseInt(tokenBuffer.get(lookahead).getTokenText());
                inter_code += "li, " + (x *4) + ", 0, T" + (reg_num++) + "\n";
                inter_code += "la, " + temp + ", 0, T" + (reg_num++) + "\n";
                inter_code += "add, T" + (reg_num-2) + ", T" + (reg_num-1) + ", T" + (reg_num) + "\n";
                reg_num++;
            }

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
            inter_code += "# Start WHILE statement ---\n";
            output+="statement -> whilestatement\n";
            whilestatement();
        }
        else if(curr.equals("IF")) {
            inter_code += "# Start iF statement ---\n";
            output+="statement -> ifstatement\n";
            ifstatement();
        }
        else if(next.equals("ASSIGN")) {
            inter_code += "# Start ASSIGN statement ---\n";
            output+="statement -> assignmentexpression\n";
            assignmentexpression(false);
        }
        else if(curr.equals("PRINT")) {
            inter_code += "# Start PRINT statement ---\n";
            output+="statement -> printexpression\n";
            printexpression();
        }
        else {
            inter_code += "# Start READ statement ---\n";
            output+="statement -> readstatement\n";
            readstatement();
        }
    }

//    TODO
    private void assignmentexpression(boolean array){
        output+="assignmentexpression -> variable ASSIGN otherexpression\n";
        String term = tokenBuffer.get(lookahead).getTokenText();

        int tempReg = reg_num;

        variable(term, false);
        String temp = term;
        lookahead++;
        otherexpression(array);

//        TODO SW for arrays
        if(array){
            inter_code += "sw, " + "T" + (reg_num - 1) + ", 0, T" + (tempReg+2) + "\n";
        } else {
            inter_code += "sw, " + "T" + (reg_num - 1) + ", 0, " + term + "\n";
        }
    }

//    TODO
    private void printexpression(){
        output+="printexpression -> PRINT variable\n";
        lookahead++;

        int temp = reg_num;
        variable("Placeholder", false);

        String text = tokenBuffer.get(lookahead-1).getTokenText();

//        inter_code += "lw, " + text+ ", 0, T" + (temp) + "\n";
//        reg_num++;
        inter_code += "syscall, 2, T" + (temp) + ", 0\n";

    }

    private void otherexpression(boolean array){
        output+="otherexpression -> term otherexpressiontail\n";
        term(array);
        otherexpressiontail(array);
    }

    private void otherexpressiontail(boolean array){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("PLUS")){
            output+="otherexpressiontail -> PLUS term otherexpressiontail\n";
            lookahead++;

            int temp = reg_num -1;
            term(array);
            inter_code += "add, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            otherexpressiontail(array);
        } else if(curr.equals("MINUS")){
            output+="otherexpressiontail -> MINUS term otherexpressiontail\n";
            lookahead++;
            int temp = reg_num -1;
            term(array);
            inter_code += "sub, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            otherexpressiontail(array);
        }
        else {
            output+="otherexpressiontail -> e\n";
        }
    }

    private void term(boolean array){
        output+="term -> factor termtail\n";
        factor(array);
        termtail(array);
    }

    private void termtail(boolean array){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("MULT")){

            output+="termtail -> MULT factor termtail\n";
            lookahead++;
            int temp = reg_num -1;
            factor(array);
            inter_code += "mul, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            termtail(array);
        } else if(curr.equals("DIV")){
            output+="termtail -> DIV factor termtail\n";

            lookahead++;
            int temp = reg_num -1;
            factor(array);
            inter_code += "div, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            termtail(array);
        }
        else {
            output+="termtail -> e\n";
        }
    }

    private void factor(boolean array){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead).getTokenText();

        //noinspection IfCanBeSwitch
        if(curr.equals("ICONST")){
            output+="factor -> ICONST\n";
            lookahead++;

//            TODO figure out why professor incrememted here

            if(array)
                reg_num++;

            if(text.charAt(0) == '-'){
                inter_code += "li, " + text.substring(1) + ", 0, T" +reg_num + "\n";
            } else {
                inter_code += "li, " + text + ", 0, T" +reg_num + "\n";
            }
            reg_num++;
        }
        else if(curr.equals("FCONST")){

            System.out.println(curr + " " + text);
            output+="factor -> FCONST\n";
            lookahead++;

            inter_code += "li, " + text + ", 0, FT" +reg_num + "\n";
            reg_num++;
        }
        else if(curr.equals("LPAREN")){
            output+="factor -> LPAREN otherexpression RPAREN\n";
            lookahead++;
            otherexpression(array);
            lookahead++;
        }
        else if(curr.equals("MINUS")){
            output+="factor -> MINUS factor\n";
            lookahead++;
            factor(array);

            inter_code += "li, 0, 0, T" +reg_num + "\n";
            inter_code += "sub, T" + reg_num + ", T" + (reg_num-1) + ", T" + (reg_num+1) + "\n";
            reg_num+=2;
        }
        else{
            output+="factor -> variable\n";
            variable(text, false);

            inter_code += "lw, T" + (reg_num-1) + ", 0, T" +reg_num + "\n";
            reg_num += 1;
        }
    }

    private void whilestatement(){
        output+="whilestatement -> WHILE condexpr whiletail\n";
        lookahead++;
//        int temp = reg_num;
        inter_code += ".label, 0, 0, LX\n";
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
        inter_code += ".label, 0, 0, LX\n";
        condexpr();
        compoundstatement();
        istail();
    }


    private void istail() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("ELSE")) {
            output += "istail -> ELSE compoundstatement\n";
            lookahead++;
            inter_code += "Start if statement ELSE part ---\n";
            inter_code += ".label, 0, 0, LX\n";
            compoundstatement();
        }
        else {
//            inter_code += "Start if statement THEN part ---\n";
//            inter_code += ".label, 0, 0, LX\n";
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

            int temp = reg_num-1;
            vorc();
            inter_code += "blt, T" + (temp) + ", T" + (temp+1) + ", LX\n";
        }
        else if(curr.equals("GT")){
            output+="condexprtail -> GT vorc\n";
            lookahead++;

            int temp = reg_num-1;
            vorc();
            inter_code += "bgt, T" + (temp) + ", T" + (temp+1) + ", LX\n";
        }
        else {
            output+="condexprtail -> EQUAL vorc\n";
            lookahead++;
            int temp = reg_num-1;
            vorc();
            inter_code += "beq, T" + (temp) + ", T" + (temp+1) + ", LX\n";
        }
        inter_code += "j, 0, 0, LX\n";
//        inter_code += ".label, 0, 0, LX\n";

    }

    private void vorc(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead).getTokenText();

        System.out.println(curr);
        if(curr.equals("ICONST")){
            output+="vorc -> ICONST\n";
            lookahead++;

            inter_code += "li, " + text + ", 0, T" + reg_num + "\n";
            reg_num++;
        }
        else if(curr.equals("FCONST")){
            output+="vorc -> FCONST\n";
            lookahead++;
        }
        else {
//            TODO
            inter_code += "lw, " + text + ", 0, T" + reg_num + "\n";
            reg_num++;
            output+="vorc -> variable\n";
            variable(text, false);
        }
    }

//    TODO
    private void readstatement(){
        output+="readstatement -> READ variable\n";
        lookahead++;
        variable("Placeholder", false);
    }

}//CLASS BRACKET
