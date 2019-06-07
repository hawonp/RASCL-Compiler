package Table;

/*
    File Created:    April 11, 2019
    Author:          Hawon Park (110983842) hawon.park@stonybrook.edu

        Attribute is an object that defines an attribute of a symbol
 */
public class Attribute {

    //possible types of the attribute
    public enum typeField{
        INT, FLOAT, ARRAY;
    }

    //instance variables
    private String attributeType;
    private String address;               //attribute object tracks either address or the type
    private typeField type;

    //constructor that takes in an address as an attribute
    public Attribute(String a, String v){
        this.attributeType = a;
        this.address = v;
    }

    //constructor that takes in a type as an attribute
    public Attribute(String a, typeField t){
        this.attributeType = a;
        this.type = t;
    }

    @Override
    public String toString(){
        if(address == null ) return attributeType + ": " + type;
        return attributeType + ": " + address;

    }

}//class bracket