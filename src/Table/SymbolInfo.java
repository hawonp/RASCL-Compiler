package Table;/*
    File Created:    April 11, 2019
    Author:          Hawon Park (110983842) hawon.park@stonybrook.edu

        SymbolInfo is a record object to be used in the Symbol Table
 */
import java.util.ArrayList;

public class SymbolInfo {

    //instance variables describing the symbol and its attributes
    private String identifier;
    private ArrayList<Attribute> attributes;


    //constructor that inits identifier
    public SymbolInfo(String c){
        this.identifier = c;
        attributes = new ArrayList<Attribute>();
    }

    public String toString(){
        return "Symbol:\t" + identifier;
    }

    //getter method for attribute list
    public ArrayList<Attribute> getAttributes(){
        return this.attributes;
    }

    //helper method to print the list of attributes
    public void printAttributes(){
        for(Attribute a : attributes){
           System.out.println("\t" + a.toString());
        }
    }

    //helper method that adds an attribute and its value
    // attribute = 1 --> add a new address value
    // attribute = 2 --> add a new type (int, array, or float)
    public boolean addAttribute(int attribute, String value){
        //add a new address
        if(attribute == 1){
            attributes.add(new Attribute("address",value));
            return true;
        }
        //add a new type
        else if (attribute == 2){
            //add a new int
            if(value.toUpperCase().equals(Attribute.typeField.INT.toString())){
                attributes.add(new Attribute("type", Attribute.typeField.INT));
                return true;
            }
            //add a new array
            else if(value.toUpperCase().equals(Attribute.typeField.ARRAY.toString())){
                attributes.add(new Attribute("type", Attribute.typeField.ARRAY));
                return true;
            }
            //add a new float
            else if(value.toUpperCase().equals(Attribute.typeField.FLOAT.toString())){
                attributes.add(new Attribute("type", Attribute.typeField.FLOAT));
                return true;
            }
        }
        return false;
    }

}//class bracket
