/*
    Author: Hawon Park
    SBUID: 110983842
    Email: hawon.park@stonybrook.edu

    This 'TestParser.java' program takes in a file by command line arguement and
    parses the token stream generated from the file
*/
package Parser;

import java.io.IOException;

public class TestParser {

    public static void main(String [] args){

//        //command line input
//        String inputFile = args[0];
//        System.out.println("Command Line Arguement is: " + inputFile);
//
//        //try to parse file and print output to a textfile
        String path = "C:\\Users\\hawon\\IdeaProjects\\CodeGeneration\\src\\Testing\\basic_rascl_tests";        String inputFile = path + "\\T00_rascl_test_exprs1.rsc";

        System.out.println("\nSTART PARSING: " + inputFile);
        Parser p = new Parser(inputFile);

        System.out.println("\nPROGRAM COMPLETE!");

        // generate_RASCL_Output();
    }//main bracket

    //UNUSED METHOD
    // THIS METHOD WAS USED TO GENERATE OUTPUT FOR ALL FILES IN A SPECIFIC FOLDER
    // public static void generate_RASCL_Output(){
    //   File f = new File("/home/hawonp/classes/cse304/termproject/part3/Testing/basic_rascl_tests");
    //   ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
    //
    //   for (File temp : files){
    //     String inputFile = temp.toString();
    //     System.out.println("\nPARSING: " + temp.toString());
    //     try {
    //       Parser p = new Parser(inputFile);
    //     } catch (IOException e) {
    //       e.printStackTrace();
    //     }
    //   }
    // }//method bracket

}//class bracket
