import Parser.*;
import Lexer.*;

import java.io.IOException;

public class CodeGen {

    private String parseOutput;

    public CodeGen(String fileName) throws IOException {

        parseOutput = initParser(fileName);
        System.out.println(parseOutput);
    }

    private String initParser(String fileName){
        Parser p = new Parser(fileName);
        return p.getOutput();
    }
}
