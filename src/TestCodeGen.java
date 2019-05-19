import java.io.IOException;

public class TestCodeGen {

    public static void main (String [] args){

//        //command line input
//        String inputFile = args[0];
//        System.out.println("Command Line Arguement is: " + inputFile);
        String path = "C:\\Users\\hawon\\IdeaProjects\\CodeGeneration\\src\\Testing\\basic_rascl_tests";

        String inputFile = path + "\\T00_rascl_test_exprs1.rsc";

        //try to generate code output
        System.out.println("\nSTART CODE GENERATION: " + inputFile);
        try {
            CodeGen c = new CodeGen(inputFile);
        } catch (IOException e) {
            System.out.println("Could not find fuke!");
            e.printStackTrace();
        }

        System.out.println("\nPROGRAM COMPLETE!");
    }

}