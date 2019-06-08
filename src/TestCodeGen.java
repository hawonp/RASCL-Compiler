import java.io.IOException;

public class TestCodeGen {

    public static void main (String [] args){

//        //command line input
//        String inputFile = args[0];
//        System.out.println("Command Line Arguement is: " + inputFile);
        String path = "C:\\Users\\hawon\\IdeaProjects\\CodeGeneration\\src\\Testing\\basic_rascl_tests";

        String inputFile = path + "\\T30_rascl_test_arrays1.rsc";

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
