// Author:  Hawon Park
// Purpose: This file is used to generate intermediate code based on sample RASCL tests provided

import java.io.IOException;

public class TestCodeGen {

    public static void main (String [] args){

       //command line input
       String inputFile = args[0];
       System.out.println("Command Line Arguement is: " + inputFile);
     
        //change this to a user input, and not a hardcoded address
        String path = "C:\\Users\\hawon\\IdeaProjects\\CodeGeneration\\src\\Testing\\basic_rascl_tests";
        String inputFile = path + "\\T31_rascl_test_arrays2.rsc";

        //try to generate code output
        System.out.println("\nSTART CODE GENERATION: " + inputFile + "\n");
        try {
            CodeGeneration c = new CodeGeneration(inputFile);
//            CodeGen c = new CodeGen(inputFile);
        } catch (IOException e) {
            System.out.println("Could not find file!");
            e.printStackTrace();
        }

        System.out.println("\nPROGRAM COMPLETE!");
    }

}
