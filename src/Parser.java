//package cse304;
//
//import static cse304.Type.*;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import static java.lang.System.exit;
//import java.util.ArrayList;
//import java.util.Stack;
//
//public class Parser {
//
//    private static int register;
//    private static int label;
//    private static ArrayList<Token> tokens;
//    private static SymbolTable table;
//    private static int current;
//
//    // I/O
//    private static File result;
//    private static FileWriter writer;
//
//    // Records all the derivations used in the parsing process
//    private static String derivation;
//
//    // Initialize the parser
//    public static void executeParsing() {
//        register = 0;
//        label = 0;
//
//        // Pointer to the current input
//        current = 0;
//
//        // Get a list of tokens from lexical analysis
//        tokens = Lexical.tokens;
//
//        // Initialize the Symbol table
//        table = new SymbolTable();
//
//        // Initailize the derivation string
//        derivation = "";
//
//        // Start the derivation
//        program();
//
//        // If parsing has been successful, append it to the end of file.
//        derivation += "Parsing Successful\n";
//
//        // Create a log file
//        result = new File("./" + Lexical.filename + ".log");
//
//        try {
//            writer = new FileWriter(result);
//            writer.write(derivation);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Parsing Completed!");
//    }
//
//    // If the current token matches, move the pointer to the right
//    private static boolean match(Type type) {
//        if (tokens.get(current).type.equals(type)) {
//            current++;
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private static boolean lookahead(Type type) {
//        return (tokens.get(current).type.equals(type));
//    }
//
//    // If the parser fails to match none of the prodcution, terminate the parsing
//    private static void error() {
//        derivation += "Error: Program terminated due to an unrecognized production" + System.getProperty("line.separator");
//
//        // Create a log file
//        result = new File("./" + Lexical.filename + ".log");
//
//        try {
//            writer = new FileWriter(result);
//            writer.write(derivation);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Error: Program terminated due to an unrecognized production");
//
//        exit(0);
//    }
//
//    // program -> decllist bstatementlist DD
//    private static void program() {
//        derivation += "program -> decllist bstatementlist DD" + System.getProperty("line.separator");
//
//        // .segment[data] section.
//        System.out.println(".segment, 0, 0, .data");
//        decllist();
//        System.out.println("");
//
//        // .segment[text] section
//        System.out.println(".segment, 0, 0, .text");
//        bstatementlist();
//        if (!match(ENDFILE)) {
//            error();
//        }
//    }
//
//    // decllist -> decl decllist
//    // decllist -> ε
//    private static void decllist() {
//        if (lookahead(INT) || lookahead(FLOAT)) {
//            derivation += "decllist -> decl decllist" + System.getProperty("line.separator");
//            decl();
//            decllist();
//        } else {
//            derivation += "decllist -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // bstatementlist -> LBRACE statementlist RBRACE
//    private static void bstatementlist() {
//        if (match(LBRACE)) {
//            derivation += "bstatementlist -> LBRACE statementlist RBRACE" + System.getProperty("line.separator");
//            statementlist();
//            if (!match(RBRACE)) {
//                error();
//            }
//        } else {
//            error();
//        }
//    }
//
//    // statementlist -> statement SEMICOLON statementlist
//    // statementlist -> ε
//    private static void statementlist() {
//        if (lookahead(WHILE) || lookahead(IF) || lookahead(PRINT) || lookahead(READ) || lookahead(ID)) {
//            derivation += "statementlist -> statement SEMICOLON statementlist" + System.getProperty("line.separator");
//            statement();
//            if (!match(SEMICOLON)) {
//                error();
//            }
//            statementlist();
//        } else {
//            derivation += "statementlist -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // decl -> typespec variablelist
//    private static void decl() {
//        if (lookahead(INT) || lookahead(FLOAT)) {
//            derivation += "decl -> typespec variablelist" + System.getProperty("line.separator");
//            Type type = typespec();
//            variablelist(type);
//        } else {
//            error();
//        }
//    }
//
//    // variablelist -> variable variablelisttail
//    private static void variablelist() {
//        if (lookahead(ID)) {
//            derivation += "variablelist -> variable variablelisttail" + System.getProperty("line.separator");
//            variable();
//            variablelisttail();
//        } else {
//            error();
//        }
//    }
//
//    // NEW variablelist for declaration.
//    // variablelist -> variable variablelisttail
//    private static void variablelist(Type type) {
//        if (lookahead(ID)) {
//            derivation += "variablelist -> variable variablelisttail" + System.getProperty("line.separator");
//            variable(type);
//            variablelisttail(type);
//        } else {
//            error();
//        }
//    }
//
//    // variablelisttail -> COMMA variable variablelisttail
//    // variablelisttail -> SEMICOLON
//    private static void variablelisttail() {
//        if (match(COMMA)) {
//            derivation += "variablelisttail -> COMMA variable variablelisttail" + System.getProperty("line.separator");
//            variable();
//            variablelisttail();
//        } else if (match(SEMICOLON)) {
//            derivation += "variablelisttail -> SEMICOLON" + System.getProperty("line.separator");
//        } else {
//            error();
//        }
//    }
//
//    // NEW variablelist for declaration.
//    // variablelisttail -> COMMA variable variablelisttail
//    // variablelisttail -> SEMICOLON
//    private static void variablelisttail(Type type) {
//        if (match(COMMA)) {
//            derivation += "variablelisttail -> COMMA variable variablelisttail" + System.getProperty("line.separator");
//            variable(type);
//            variablelisttail(type);
//        } else if (match(SEMICOLON)) {
//            derivation += "variablelisttail -> SEMICOLON" + System.getProperty("line.separator");
//        } else {
//            error();
//        }
//    }
//
//    // typespec -> INT
//    // typespec -> FLOAT
//    private static Type typespec() {
//        if (match(INT)) {
//            derivation += "typespec -> INT" + System.getProperty("line.separator");
//
//            return INT;
//        } else if (match(FLOAT)) {
//            derivation += "typespec -> FLOAT" + System.getProperty("line.separator");
//
//            return FLOAT;
//        } else {
//            error();
//
//            return null;
//        }
//    }
//
//    // variable -> ID variabletail
//    private static void variable() {
//        if (match(ID)) {
//            derivation += "variable -> ID variabletail" + System.getProperty("line.separator");
//            variabletail();
//        } else {
//            error();
//        }
//    }
//
//    // NEW!
//    // variable -> ID variabletail
//    private static void variable(Type type) {
//        if (match(ID)) {
//            derivation += "variable -> ID variabletail" + System.getProperty("line.separator");
//            variabletail(type);
//        } else {
//            error();
//        }
//    }
//
//    // variabletail -> arraydim
//    // variabletail -> ε
//    private static void variabletail() {
//        if (lookahead(LBRACKET)) {
//            derivation += "variabletail -> arraydim" + System.getProperty("line.separator");
//            arraydim();
//        } else {
//            derivation += "variabletail -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // variabletail -> arraydim
//    // variabletail -> ε
//    private static void variabletail(Type type) {
//        if (lookahead(LBRACKET)) {
//            derivation += "variabletail -> arraydim" + System.getProperty("line.separator");
//            arraydim();
//
//            // INT | FLOAT
//            // .segment[data] section.
//            // Declaration
//            //
//            // ASSIGN
//            // .segment[text] section.
//            // LHS
//            if (type == INT || type == FLOAT) {
//
//                // Insert new variables into the Symbol Table.
//                Token temp = tokens.get(current - 4);
//                table.addSymbol(temp.text);
//                // Identifer: temp.text
//                // Updating its type, 1
//                // Type: type (Either INT or FLOAT)
//
//                int numOfElements = Integer.parseInt(tokens.get(current - 2).text);
//                table.addAttributeToSymbol(temp.text, 1, type.toString());
//                System.out.printf(".%s, 0, %d, %s\n", type.toString().toLowerCase(), numOfElements, temp.text);
//
//            } else if (type == ASSIGN) {
//                Token arr = tokens.get(current - 4);
//                Token index = tokens.get(current - 2);
//
//                // Array index is ? Constant : Variable
//                if (index.type == ICONST) {
//                    int i = Integer.parseInt(index.text) * 4;
//                    System.out.printf("li, %d, 0, T%d\n", i, register++);
//                } else {
//                    System.out.printf("lw, %s, 0, T%d\n", index.text, register);
//                    System.out.printf("sl, T%d, %s, T%d\n", register, 2, register++);
//                }
//                System.out.printf("la, %s, 0, T%d\n", arr.text, register);
//                System.out.printf("add, T%d, T%d, T%d\n", register - 1, register, register + 1);
//                register = register + 2;
//            }
//
//        } else {
//
//            derivation += "variabletail -> ε" + System.getProperty("line.separator");
//
//            // INT | FLOAT
//            // .segment[data] section.
//            // Declaration
//            //
//            // ASSIGN
//            // .segment[text] section.
//            // LHS
//            if (type == INT || type == FLOAT) {
//
//                // Insert new variables into the Symbol Table.
//                Token temp = tokens.get(current - 1);
//                table.addSymbol(temp.text);
//
//                // Identifer: temp.text
//                // Updating its type, 1
//                // Type: type (Either INT or FLOAT)
//                table.addAttributeToSymbol(temp.text, 1, type.toString());
//                System.out.printf(".%s, 0, 1, %s\n", type.toString().toLowerCase(), temp.text);
//
//            } else if (type == ASSIGN) {
//            }
//
//        }
//    }
//
//    // arraydim -> LBRACKET arraydimtail
//    private static void arraydim() {
//        if (match(LBRACKET)) {
//            derivation += "arraydim -> LBRACKET arraydimtail" + System.getProperty("line.separator");
//            arraydimtail();
//        } else {
//            error();
//        }
//    }
//
//    // arraydimtail -> ID RBRACKET
//    // arraydimtail -> ICONST RBRACKET
//    private static void arraydimtail() {
//        if (match(ID)) {
//            derivation += "arraydimtail -> ID RBRACKET" + System.getProperty("line.separator");
//            if (!match(RBRACKET)) {
//                error();
//            }
//        } else if (match(ICONST)) {
//            derivation += "arraydimtail -> ICONST RBRACKET" + System.getProperty("line.separator");
//            if (!match(RBRACKET)) {
//                error();
//            }
//        } else {
//            error();
//        }
//    }
//
//    // statement -> whilestatement
//    // statement -> ifstatement
//    // statement -> assignmentexpression
//    // statement -> printexpression
//    // statement -> readstatement
//    private static void statement() {
//        if (lookahead(WHILE)) {
//            derivation += "statement -> whilestatement" + System.getProperty("line.separator");
//            whilestatement();
//        } else if (lookahead(IF)) {
//            derivation += "statement -> ifstatement" + System.getProperty("line.separator");
//            ifstatement();
//        } else if (lookahead(ID)) {
//            derivation += "statement -> assignmentexpression" + System.getProperty("line.separator");
//            assignmentexpression();
//        } else if (lookahead(PRINT)) {
//            derivation += "statement -> printexpression" + System.getProperty("line.separator");
//            printexpression();
//        } else if (lookahead(READ)) {
//            derivation += "statement -> readstatement" + System.getProperty("line.separator");
//            readstatement();
//        } else {
//            error();
//        }
//    }
//
//    // assignmentexpression -> variable ASSIGN otherexpression
//    private static void assignmentexpression() {
//        if (lookahead(ID)) {
//            derivation += "assignmentexpression -> variable ASSIGN otherexpression" + System.getProperty("line.separator");
//            System.out.println("Start ASSIGN statement ---");
//
//            int position = current;
//            Token token = tokens.get(current);
//            variable(ASSIGN);
//
//            int reg = register;
//
//            if (!match(ASSIGN)) {
//                error();
//            }
//
//            otherexpression();
//
//            if (tokens.get(position + 1).type == LBRACKET) {
//                System.out.printf("sw, T%d, 0, T%d\n", register - 1, reg - 1);
//            } else {
//                System.out.printf("sw, T%d, 0, %s\n", register - 1, token.text);
//            }
//        } else {
//            error();
//        }
//    }
//
//    // printexpression -> PRINT variable
//    private static void printexpression() {
//        if (match(PRINT) && lookahead(ID)) {
//            derivation += "printexpression -> PRINT variable" + System.getProperty("line.separator");
//            System.out.println("Start PRINT statement ---");
//            variable();
//
//            Token tok = tokens.get(current - 1);
//
//            // RBRACKET ? Array : Variable
//            if (tok.type == RBRACKET) {
//                Token index = tokens.get(current - 2);
//                Token arr = tokens.get(current - 4);
//
//                int i = Integer.parseInt(index.text) * 4;
//
//                System.out.printf("li, %d, 0, T%d\n", i, register++);
//                System.out.printf("la, %s, 0, T%d\n", arr.text, register++);
//                System.out.printf("add T%d, T%d, T%d\n", register - 2, register - 1, register);
//                System.out.printf("lw, T%d, 0, T%d\n", register++, register);
//            } else {
//                System.out.printf("lw, %s, 0, T%d\n", tok.text, register);
//            }
//            System.out.printf("syscall, 2, T%d, 0\n", register);
//        } else {
//            error();
//        }
//    }
//
//    // otherexpression -> term otherexpressiontail
//    private static void otherexpression() {
//        if (lookahead(ID) || lookahead(ICONST) || lookahead(FCONST) || lookahead(LPAREN) || lookahead(MINUS)) {
//            derivation += "otherexpression -> term otherexpressiontail" + System.getProperty("line.separator");
//            term();
//            otherexpressiontail();
//        } else {
//            error();
//        }
//    }
//
//    // otherexpressiontail -> PLUS term otherexpressiontail
//    // otherexpressiontail -> MINUS term otherexpressiontail
//    // otherexpressiontail -> ε
//    private static void otherexpressiontail() {
//        if (match(PLUS)) {
//            derivation += "otherexpressiontail -> PLUS term otherexpressiontail" + System.getProperty("line.separator");
//
//            int temp = register - 1;
//            term();
//            System.out.printf("add, T%d, T%d, T%d\n", temp, register - 1, register++);
//
//            otherexpressiontail();
//        } else if (match(MINUS)) {
//            derivation += "otherexpressiontail -> MINUS term otherexpressiontail" + System.getProperty("line.separator");
//
//            int temp = register - 1;
//            term();
//            System.out.printf("sub, T%d, T%d, T%d\n", temp, register - 1, register++);
//
//            otherexpressiontail();
//        } else {
//            derivation += "otherexpressiontail -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // term -> factor termtail
//    private static void term() {
//        if (lookahead(ID) || lookahead(ICONST) || lookahead(FCONST) || lookahead(LPAREN) || lookahead(MINUS)) {
//            derivation += "term -> factor termtail" + System.getProperty("line.separator");
//            factor();
//            termtail();
//        }
//    }
//
//    // termtail -> MULT factor termtail
//    // termtail -> DIV factor termtail
//    // termtail -> ε
//    private static void termtail() {
//        if (match(MULT)) {
//            derivation += "termtail -> MULT factor termtail" + System.getProperty("line.separator");
//
//            int temp = register - 1;
//            factor();
//            System.out.printf("mul, T%d, T%d, T%d\n", temp, register - 1, register++);
//
//            termtail();
//        } else if (match(DIV)) {
//            derivation += "termtail -> DIV factor termtail" + System.getProperty("line.separator");
//
//            int temp = register - 1;
//            factor();
//            System.out.printf("div, T%d, T%d, T%d\n", temp, register - 1, register++);
//
//            termtail();
//        } else {
//            derivation += "termtail -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // factor -> variable
//    // factor -> ICONST
//    // factor -> FCONST
//    // factor -> LPAREN otherexpression RPAREN
//    // factor -> MINUS factor
//    private static void factor() {
//        if (lookahead(ID)) {
//            derivation += "factor -> variable" + System.getProperty("line.separator");
//            variable();
//
//            Token tok = tokens.get(current - 1);
//
//            // RBRACKET ? Array : Variable
//            if (tok.type == RBRACKET) {
//                Token index = tokens.get(current - 2);
//                Token arr = tokens.get(current - 4);
//
//                int i = Integer.parseInt(index.text) * 4;
//
//                System.out.printf("li, %d, 0, T%d\n", i, register++);
//                System.out.printf("la, %s, 0, T%d\n", arr.text, register++);
//                System.out.printf("add T%d, T%d, T%d\n", register - 2, register - 1, register);
//                System.out.printf("lw, T%d, 0, T%d\n", register++, register++);
//            } else {
//                System.out.printf("lw, %s, 0, T%d\n", tok.text, register++);
//            }
//
//        } else if (match(ICONST)) {
//            derivation += "factor -> ICONST" + System.getProperty("line.separator");
//            Token tok = tokens.get(current - 1);
//            System.out.printf("li, %s, 0, T%s\n", tok.text, register++);
//        } else if (match(FCONST)) {
//            derivation += "factor -> FCONST" + System.getProperty("line.separator");
//            Token tok = tokens.get(current - 1);
//            System.out.printf("li, %s, 0, T%s\n", tok.text, register++);
//        } else if (match(LPAREN)) {
//            derivation += "factor -> LPAREN otherexpression RPAREN" + System.getProperty("line.separator");
//            otherexpression();
//            if (!match(RPAREN)) {
//                error();
//            }
//        } else if (match(MINUS)) {
//            derivation += "factor -> MINUS factor" + System.getProperty("line.separator");
//            factor();
//            System.out.printf("li, 0, 0, T%d\n", register);
//            System.out.printf("sub, T%d, T%d, T%d\n", register, register - 1, register + 1);
//            register = register + 2;
//        } else {
//            error();
//        }
//    }
//
//    // whilestatement -> WHILE condexpr whiletail
//    private static void whilestatement() {
//        if (match(WHILE)) {
//            derivation += "whilestatement -> WHILE condexpr whiletail" + System.getProperty("line.separator");
//            System.out.println("Start WHILE statement ---");
//
//            label += 3;
//            int temp = label;
//
//            System.out.printf(".label, 0, 0, L%d\n", label - 1);
//
//            condexpr(WHILE);
//
//            whiletail();
//            System.out.printf("j, 0, 0, L%d\n", temp - 1);
//            System.out.printf(".label, 0, 0, L%d\n", temp - 3);
//
//        } else {
//            error();
//        }
//    }
//
//    // whiletail -> compoundstatement
//    private static void whiletail() {
//        if (lookahead(LBRACE)) {
//            compoundstatement();
//        } else {
//            error();
//        }
//    }
//
//    // compoundstatement -> LBRACE statementlist RBRACE
//    private static void compoundstatement() {
//        if (match(LBRACE)) {
//            statementlist();
//            if (!match(RBRACE)) {
//                error();
//            }
//        } else {
//            error();
//        }
//    }
//
//    // ifstatement -> IF condexpr compoundstatement istail
//    private static void ifstatement() {
//        if (match(IF)) {
//            derivation += "ifstatement -> IF condexpr compoundstatement istail" + System.getProperty("line.separator");
//            System.out.println("Start IF statement ---");
//
//            label += 3;
//
//            int temp = label;
//
//            condexpr(IF);
//
//            System.out.printf("j, 0, 0, L%d\n", temp - 2);
//            System.out.println("Start IF statement THEN part ---");
//            System.out.printf("j, 0, 0, L%d\n", temp - 1);
//
//            compoundstatement();
//
//            System.out.println("Start IF statement ELSE part ---");
//            System.out.printf(".label, 0, 0, L%d\n", temp - 2);
//
//            istail();
//
//            System.out.printf("j, 0, 0, L%d\n", temp - 3);
//            System.out.printf(".label, 0, 0, L%d\n", temp - 3);
//
//        } else {
//            error();
//        }
//    }
//
//    // istail -> ELSE compoundstatement
//    // istail -> ε
//    private static void istail() {
//        if (match(ELSE)) {
//            derivation += "istail -> ELSE compoundstatement" + System.getProperty("line.separator");
//
//            compoundstatement();
//
//        } else {
//            derivation += "istail -> ε" + System.getProperty("line.separator");
//        }
//    }
//
//    // condexpr -> LPAREN vorc condexprtail RPAREN
//    // condexpr -> vorc condexprtail
//    // condexpr -> NOT condexpr
//    private static void condexpr(Type type) {
//        if (match(LPAREN)) {
//            derivation += "condexpr -> LPAREN vorc condexprtail RPAREN" + System.getProperty("line.separator");
//            vorc();
//            condexprtail(type);
//            if (!match(RPAREN)) {
//                error();
//            }
//        } else if (lookahead(ID) || lookahead(ICONST) || lookahead(FCONST)) {
//            derivation += "condexpr -> vorc condexprtail" + System.getProperty("line.separator");
//            vorc();
//            condexprtail(type);
//        } else if (match(NOT)) {
//            derivation += "condexpr -> NOT condexpr" + System.getProperty("line.separator");
//            condexpr(type);
//        } else {
//            error();
//        }
//    }
//
//    // condexprtail -> LT vorc
//    // condexprtail -> GT vorc
//    // condexprtail -> EQUAL vorc
//    private static void condexprtail(Type type) {
//        if (match(LT)) {
//            derivation += "condexprtail -> LT vorc" + System.getProperty("line.separator");
//            vorc();
//
//            if (type == WHILE) {
//                System.out.printf("blt, T%d, T%d, L%d\n", register - 1, register, label - 2);
//                System.out.printf("j, 0, 0, L%d\n", label - 3);
//                System.out.printf(".label, 0, 0, L%d\n", label - 2);
//            } else {
//                System.out.printf("blt, T%d, T%d, L%d\n", register - 1, register, label - 1);
//            }
//
//        } else if (match(GT)) {
//            derivation += "condexprtail -> GT vorc" + System.getProperty("line.separator");
//            vorc();
//
//            if (type == WHILE) {
//                System.out.printf("bgt, T%d, T%d, L%d\n", register - 1, register, label - 2);
//                System.out.printf("j, 0, 0, L%d\n", label - 3);
//                System.out.printf(".label, 0, 0, L%d\n", label - 2);
//            } else {
//                System.out.printf("bgt, T%d, T%d, L%d\n", register - 1, register, label - 1);
//            }
//
//        } else if (match(EQ)) {
//            derivation += "condexprtail -> EQUAL vorc" + System.getProperty("line.separator");
//            vorc();
//
//            if (type == WHILE) {
//                System.out.printf("beq, T%d, T%d, L%d\n", register - 1, register, label - 2);
//                System.out.printf("j, 0, 0, L%d\n", label - 3);
//                System.out.printf(".label, 0, 0, L%d\n", label - 2);
//            } else {
//                System.out.printf("beq, T%d, T%d, L%d\n", register - 1, register, label - 1);
//            }
//
//        } else {
//            error();
//        }
//    }
//
//    // vorc -> variable
//    // vorc -> ICONST
//    // vorc -> FCONST
//    private static void vorc() {
//        if (lookahead(ID)) {
//            derivation += "vorc -> variable" + System.getProperty("line.separator");
//            variable();
//
//            Token tok = tokens.get(current - 1);
//
//            // RBRACKET ? Array : Variable
//            if (tok.type == RBRACKET) {
//                Token index = tokens.get(current - 2);
//                Token arr = tokens.get(current - 4);
//
//                int i = Integer.parseInt(index.text) * 4;
//
//                System.out.printf("li, %d, 0, T%d\n", i, register++);
//                System.out.printf("la, %s, 0, T%d\n", arr.text, register++);
//                System.out.printf("add T%d, T%d, T%d\n", register - 2, register - 1, register);
//                System.out.printf("lw, T%d, 0, T%d\n", register++, register);
//            } else {
//                System.out.printf("lw, %s, 0, T%d\n", tok.text, register++);
//            }
//
//        } else if (match(ICONST)) {
//            derivation += "vorc -> ICONST" + System.getProperty("line.separator");
//
//            Token tok = tokens.get(current - 1);
//
//            System.out.printf("li, %s, 0, T%d\n", tok.text, register++);
//
//        } else if (match(FCONST)) {
//            derivation += "vorc -> FCONST" + System.getProperty("line.separator");
//
//            Token tok = tokens.get(current - 1);
//
//            System.out.printf("li, %s, 0, T%d\n", tok.text, register++);
//
//        } else {
//            error();
//        }
//    }
//
//    // readstatement -> READ variable
//    private static void readstatement() {
//        if (match(READ)) {
//            derivation += "readstatement -> READ variable" + System.getProperty("line.separator");
//            variable();
//        } else {
//            error();
//        }
//    }
//}
