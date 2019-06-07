package Table;/*
    File Created:    April 11, 2019
    Author:          Hawon Park (110983842) hawon.park@stonybrook.edu

        SymbolTable tracks symbols and allows for quick and easy access
 */
import Table.SymbolInfo;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    //symbols stored in a HashMap that maps a string to its SymbolInfo object
    private Map<String, SymbolInfo> map;

    //empty constructor
    public SymbolTable (){}

    //initialize symbol by declaring the HashMap
    public void initSymTab(){
        map = new HashMap<String, SymbolInfo>();
    }

    //add a unique symbol to the table
    public boolean addSymbol(String s){
        //add a symbol to the table, if it is unique
        if(!map.containsKey(s)){
            map.put(s, new SymbolInfo(s));
            return true;
        }
        //could not add symbol to table as symbol already exists
        return false;
    }

    //add an attribute to an existing symbol
    public boolean addAttributeToSymbol(String identifier, int attribute, String value){
        //add specific values to symbol based on the attribute provided
        if(map.containsKey(identifier)){
            SymbolInfo si = map.get(identifier);
            return si.addAttribute(attribute,value);
        }
        //could not add attribute to symbol as symbol does not exist
        return false;
    }

    //helper method that checks if a symbol is in the symbol table
    public boolean symbolInTable(String identifer){
        return map.containsKey(identifer);
    }

    //helper method that gets the SymbolInfo object associated with a string
    public SymbolInfo getSymbol (String identifier){
        return map.get(identifier);
    }

}//class bracket
