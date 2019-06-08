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
import Table.SymbolTable;
import java.io.IOException;
import java.util.ArrayList;

public class CodeGeneration {

    //instance variables
    private ArrayList<tokenInfo> tokenBuffer;
    private SymbolTable st;
    private int lookahead;
    private String output;
    private String inter_code;
    private int reg_num;
    private int float_num;
    private int label_num;

    //constructor
    public CodeGeneration(String fileName) throws IOException{
        //initialize instance variables and token buffer
        lookahead = 0;
        output = "";
        inter_code = "";
        reg_num = 0;
        float_num = 1;

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

        //if lexical analxyzer successfully opens file, build token buffer
//        System.out.println("\t-(Parser) init Lexer with input file wew:" + check);
        if(check == 1) {
//            System.out.println("\t-(Parser) build token buffer");
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

        inter_code+=".segment, 0, 0, .data\n";
        decllist();

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
        if(curr.equals("INT") || curr.equals("FLOAT")){
            output+="decllist -> decl decllist\n";
            decl(curr, true);
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
            statement(false);
            lookahead++;
            statementlist();
        }
        //use multiple lookaheads to check for an 1-D array assignment
        else if(next.equals("LBRACKET") && threeTokenAfter.equals("RBRACKET") && fourTokenAfter.equals("ASSIGN")){
            output+="statementlist -> statement SEMICOLON statementlist\n";
            output+="statement -> assignmentexpression\n";
            //skip calling statement() and go directly to assignmentexpression()

            //declare that code is starting an assignment statement
            inter_code += "# Start ASSIGN statement ---\n";
            assignmentexpression(true);
            lookahead++;
            statementlist();
        }
        else {
            output+="statementlist -> e\n";
        }
    }

    private void decl(String type, boolean isdecllist){
        output+="decl -> typespec variablelist\n";
        typespec();
        variablelist(type, isdecllist);
    }

    private void variablelist(String type, boolean isdecllist){
        output+="variablelist -> variable variablelisttail\n";
        variable(type, isdecllist);
        variablelisttail(type, isdecllist);
    }

    private void variablelisttail(String type, boolean isdecllist){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("COMMA")){
            output+="variablelisttail -> COMMA variable variablelisttail\n";
            lookahead++;
            variable(type, isdecllist);
            variablelisttail(type, isdecllist);
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

    private void variable(String type, boolean isdecllist){
        output+="variable -> ID variabletail\n";

        st.addSymbol(tokenBuffer.get(lookahead).getTokenText());
        lookahead++;
        variabletail(type, isdecllist);

    }

    private void variabletail(String type, boolean isdecllist) {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead-1).getTokenText();

        if (curr.equals("LBRACKET")) {
            output += "variabletail -> arraydim\n";
            arraydim(type, isdecllist);

            if(!isdecllist){
                reg_num++;
            } else {
                if(type.toLowerCase().equals("float")){
                    st.addFloatArr(text);
                } else {
                    st.addIntArr(text);
                }
            }

        }
        else {
            output+="variabletail -> e\n";

            if(isdecllist){
                inter_code+="." + type.toLowerCase() + ", 0, 1, " + text + "\n";
                st.addAttributeToSymbol(text, 2, type);
            }
        }
    }

    private void arraydim(String type, boolean isdecllist){
        output+="arraydim -> LBRACKET arraydimtail\n";
        lookahead++;
        arraydimtail(type, isdecllist);
    }

    private void arraydimtail(String type, boolean isdecllist) {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead).getTokenText();

        if (curr.equals("ID")) {
            output+="arraydimtail -> ID RBRACKET\n";

            inter_code += "lw, " + text + ", 0, T" + (reg_num) + "\n";
            inter_code += "sl, T" + reg_num + ", 2, T" + (reg_num++) + "\n";
            inter_code += "la, arr, 0, T" + (reg_num++) + "\n";
            inter_code += "add, T" + (reg_num-2) + ", T" + (reg_num-1) + ", T" + (reg_num) + "\n";
            reg_num++;

        }
        else{
            output+="arraydimtail -> ICONST RBRACKET\n";

            //runs only during declaration stage
            if(isdecllist){
                String temp = tokenBuffer.get(lookahead-2).getTokenText();
                inter_code += "." + type.toLowerCase() + ", 0, " + text + ", " + temp + "\n";
            }
            // run during definition stage
            else {
                String temp = tokenBuffer.get(lookahead-2).getTokenText();
                int x = Integer.parseInt(tokenBuffer.get(lookahead).getTokenText());
                inter_code += "li, " + (x *4) + ", 0, T" + (reg_num++) + "\n";
                inter_code += "la, " + temp + ", 0, T" + (reg_num++) + "\n";
                inter_code += "add, T" + (reg_num-2) + ", T" + (reg_num-1) + ", T" + (reg_num) + "\n";
                reg_num++;

            }
        }
        lookahead+=2;
    }

    private void statement(boolean isarray){
        //get current token which would work for WHILE, IF, PRINT, READ statements
        //get next token which is requirement for assignment operators
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String next = tokenBuffer.get(lookahead+1).getTokenType().toString();

        if(curr.equals("WHILE")) {
            output+="statement -> whilestatement\n";

            inter_code += "# Start WHILE statement ---\n";
            whilestatement();
        }
        else if(curr.equals("IF")) {
            output+="statement -> ifstatement\n";

            inter_code += "# Start if statement ---\n";
            ifstatement();
        }
        else if(next.equals("ASSIGN")) {
            output+="statement -> assignmentexpression\n";

            inter_code += "# Start ASSIGN statement ---\n";
            assignmentexpression(isarray);
        }
        else if(curr.equals("PRINT")) {
            output+="statement -> printexpression\n";

            inter_code += "# Start PRINT statement ---\n";
            printexpression();
        }
        else {
            output+="statement -> readstatement\n";

            inter_code += "# Start READ statement ---\n";
            readstatement();
        }
    }

    private void assignmentexpression(boolean isarray){
        String text = tokenBuffer.get(lookahead).getTokenText();
        int tempReg = reg_num;

        output+="assignmentexpression -> variable ASSIGN otherexpression\n";
        variable("", false);
        lookahead++;
        otherexpression();

        if(isarray){
            if(st.isFloat(text)){
//                inter_code += "toFloat, T" + (reg_num-1) + ", 0, FT" + (float_num) + "\n";
                inter_code += "sw, " + "FT" + float_num + ", 0, T" + (tempReg+2) + "\n";
//                float_num++;
            } else {
                inter_code += "sw, " + "T" + (reg_num - 1) + ", 0, T" + (tempReg+2) + "\n";
            }
        } else {
            if(st.isFloat(text)){
//                inter_code += "toFloat, T" + (reg_num-1) + ", 0, FT" + (float_num) + "\n";
                inter_code += "sw, " + "FT" + (float_num) + ", 0, " + text + "\n";
//                float_num++;
            } else {
                inter_code += "sw, " + "T" + (reg_num - 1) + ", 0, " + text + "\n";
            }
        }
    }

    private void printexpression(){
        String text = tokenBuffer.get(lookahead+1).getTokenText();
        int temp = reg_num;

        output+="printexpression -> PRINT variable\n";
        lookahead++;
        variable("", false);

        String prev = tokenBuffer.get(lookahead-1).getTokenText();
        if(prev.equals("]")){
            inter_code += "lw, T" + (reg_num-2)+ ", 0, T" + (reg_num-1) + "\n";
            inter_code += "syscall, 2, T" + (reg_num-1) + ", 0\n";

        } else {
            inter_code += "lw, " + text+ ", 0, T" + (temp) + "\n";
            inter_code += "syscall, 2, T" + (temp) + ", 0\n";
        }
    }

    private void otherexpression(){
        output+="otherexpression -> term otherexpressiontail\n";
        term();
        otherexpressiontail();
    }

    private void otherexpressiontail(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if(curr.equals("PLUS")){
            output+="otherexpressiontail -> PLUS term otherexpressiontail\n";
            lookahead++;

            int temp = reg_num - 1;
            term();
            inter_code += "add, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";

            otherexpressiontail();
        } else if(curr.equals("MINUS")){
            output+="otherexpressiontail -> MINUS term otherexpressiontail\n";
            lookahead++;

            int temp = reg_num - 1;
            term();
            inter_code += "sub, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";

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

        if(curr.equals("MULT")){
            output+="termtail -> MULT factor termtail\n";
            lookahead++;

            int temp = reg_num -1;
            factor();
            inter_code += "mul, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            termtail();
        } else if(curr.equals("DIV")){
            output+="termtail -> DIV factor termtail\n";
            lookahead++;

            int temp = reg_num -1;
            factor();
            inter_code += "div, T" + temp + ", T" + (reg_num-1) + ", T" + (reg_num++) + "\n";
            termtail();
        }
        else {
            output+="termtail -> e\n";
        }
    }

    private void factor(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead).getTokenText();

        if(curr.equals("ICONST")){
            output+="factor -> ICONST\n";
            lookahead++;

//            TODO PROFESSOR INCREMENETS BY ONE MORE IF ARRAY
            if(text.charAt(0) == '-'){
                inter_code += "li, " + text.substring(1) + ", 0, T" +reg_num + "\n";
            } else {
                inter_code += "li, " + text + ", 0, T" +reg_num + "\n";
            }
            reg_num++;
        }
        else if(curr.equals("FCONST")){
            output+="factor -> FCONST\n";
            lookahead++;

            inter_code += "li, " + text + ", 0, FT" +reg_num + "\n";
            reg_num++;
        }
        else if(curr.equals("LPAREN")){
            //todo unsure about this shit
            output+="factor -> LPAREN otherexpression RPAREN\n";
            lookahead++;
            otherexpression();
            lookahead++;
        }
        else if(curr.equals("MINUS")){
            output+="factor -> MINUS factor\n";
            lookahead++;
            factor();

            inter_code += "li, 0, 0, T" +reg_num + "\n";
            inter_code += "sub, T" + reg_num + ", T" + (reg_num-1) + ", T" + (reg_num+1) + "\n";
            reg_num+=2;
        }
        else{
            output+="factor -> variable\n";
            int temp = reg_num;

            variable("", false);

            String prev = tokenBuffer.get(lookahead-1).getTokenText();

            if(prev.equals("]")){
                inter_code += "lw, T" + (reg_num-2) + ", 0, T" + (reg_num-1) + "\n";
            } else {
                inter_code += "lw, " + text + ", 0, T" +reg_num + "\n";
                reg_num++;
            }
        }
    }

    private void whilestatement(){
        output+="whilestatement -> WHILE condexpr whiletail\n";
        lookahead++;

        label_num+=3;
        int temp = label_num;

        inter_code += ".label, 0, 0, L" + (temp-1) + "\n";
        condexpr();

        whiletail();
        inter_code += "j, 0, 0, L" + (temp-1) + "\n";
        inter_code += ".label, 0, 0, L" + (temp-3) + "\n";
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
        reg_num++;
        label_num+=3;
        int temp = label_num;
        condexpr();
        inter_code += "j, 0, 0, L" + (temp-2) + "\n";
        inter_code += "# Start if statement THEN part ---\n";
        inter_code += ".label, 0, 0, L" + (temp-1) + "\n";
        compoundstatement();

        istail();

        inter_code += "j, 0, 0, L" + (temp-3) + "\n";
        inter_code += ".label, 0, 0, L" + (temp-3) + "\n";

    }

    private void istail() {
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();

        if (curr.equals("ELSE")) {
            output += "istail -> ELSE compoundstatement\n";

            inter_code += "# Start if statement ELSE part ---\n";
            inter_code += ".label, 0, 0, L" + (label_num-2) + "\n";
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
        String prev = tokenBuffer.get(lookahead-3).getTokenText().toLowerCase();

        if(curr.equals("LT")){
           output+="condexprtail -> LT vorc\n";
           lookahead++;

            int temp = reg_num;
            int label = label_num;
            vorc();

            if(prev.equals("while")){
                inter_code += "blt, T" + (temp) + ", T" + (temp+1) + ", L" + (label_num-2) + "\n";
                inter_code += "j, 0, 0, L" + (label_num-3) + "\n";
                inter_code += ".label, 0, 0, L" + (label_num-2) + "\n";

            } else {
                inter_code += "blt, T" + (temp) + ", T" + (temp+1) + ", L" + (label_num-1) + "\n";
            }

        }
        else if(curr.equals("GT")){
            output+="condexprtail -> GT vorc\n";
            lookahead++;

            int temp = reg_num;
            vorc();

            if(prev.equals("while")){
                inter_code += "bgt, T" + (temp) + ", T" + (temp + 1) + ", L" + (label_num - 2) + "\n";
                inter_code += "j, 0, 0, L" + (label_num-3) + "\n";
                inter_code += ".label, 0, 0, L" + (label_num-2) + "\n";
            } else {
                inter_code += "bgt, T" + (temp) + ", T" + (temp + 1) + ", L" + (label_num - 1) + "\n";
            }
        }
        else {
            output+="condexprtail -> LT vorc\n";
            lookahead++;

            int temp = reg_num;
            vorc();

            if(prev.equals("while")){
                inter_code += "beq, T" + (temp) + ", T" + (temp+1) + ", L" + (label_num-2) + "\n";
                inter_code += "j, 0, 0, L" + (label_num-3) + "\n";
                inter_code += ".label, 0, 0, L" + (label_num-2) + "\n";
            } else {
                inter_code += "beq, T" + (temp) + ", T" + (temp+1) + ", L" + (label_num-1) + "\n";
            }
        }
        reg_num++;
    }

    private void vorc(){
        String curr = tokenBuffer.get(lookahead).getTokenType().toString();
        String text = tokenBuffer.get(lookahead).getTokenText();

        if(curr.equals("ICONST")){
            output+="vorc -> ICONST\n";
            lookahead++;

            inter_code += "li, " + text + ", 0, T" + (reg_num+1) + "\n";
            reg_num++;
        }
        else if(curr.equals("FCONST")){
            output+="vorc -> FCONST\n";
            lookahead++;

            inter_code += "li, " + text + ", 0, FT" + (reg_num+1) + "\n";
            reg_num++;
        }
        else {
            int temp = reg_num;
            output+="vorc -> variable\n";
            variable("", false);

            inter_code += "lw, " + text + ", 0, T" + temp + "\n";
        }
    }

    private void readstatement(){
        output+="readstatement -> READ variable\n";
        lookahead++;
        variable("", false);
    }

}//CLASS BRACKET
