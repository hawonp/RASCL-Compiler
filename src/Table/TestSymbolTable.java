package Table;

/*
    File Created:    April 11, 2019
    Author:          Hawon Park (110983842) hawon.park@stonybrook.edu

        TestSymbolTable is a short program that tests out the SymbolTable
 */
public class TestSymbolTable {

    public static void main(String [] args){
        SymbolTable st = new SymbolTable();

        //1) initialize symbol table
        System.out.println("(1) Initialize Symbol Table");
        st.initSymTab();

        //2) add 3 symbols to table
        System.out.println("\n(2) Test addSymbol()");
        System.out.println("\tadd \'temperature\' symbol:\t" + st.addSymbol("temperature"));
        System.out.println("\tadd \'velocity\' symbol:\t" + st.addSymbol("velocity"));
        System.out.println("\tadd \'temp\' symbol:\t" + st.addSymbol("temp"));

        //3) test if temperature is in the table
        System.out.println("\n(3) Test symbolInTable() with \"temperature\"");
        System.out.println("\t\"temperature\" in the symbol table:\t" + st.symbolInTable("temperature"));

        //4) test if bang is in the table (it should return null)
        System.out.println("\n(4) Test symbolInTable() with \"bang\"");
        System.out.println("\t\"bang\" in the symbol table:\t" + st.symbolInTable("bang"));

        //5) add attributes to various symbols
        System.out.println("\n(5) Test addAttributeToSymbol()");
        System.out.println("\ttemperature");
        st.addAttributeToSymbol("temperature",1,"0x800000");
        st.addAttributeToSymbol("temperature",2,"int");
        System.out.println("\tvelocity");
        st.addAttributeToSymbol("velocity",1,"0x800020");
        st.addAttributeToSymbol("velocity",2,"float");
        System.out.println("\ttemp");
        st.addAttributeToSymbol("temp",1,"0x800040");
        st.addAttributeToSymbol("temp",2,"array");

        //6) look up temperature in the symbolt able
        System.out.println("\n(6) Test getSymbol() with \"temperature\"");
        SymbolInfo temp = st.getSymbol("temperature");
        System.out.println("\t" + temp);
        temp.printAttributes();

        //7) look up velocity in the symbol table
        System.out.println("\n(7) Test getSymbol() with \"velocity\"");
        temp = st.getSymbol("velocity");
        System.out.println("\t" + temp);
        temp.printAttributes();

        //8) look up bang in the symbol table (should not be able to find find bang)
        System.out.println("\n(8) Test getSymbol() with \"bang\"");
        temp = st.getSymbol("bang");
        System.out.println("\t" + st.getSymbol("bang"));
    }
}//class bracket
