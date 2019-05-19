/*
    Author: Hawon Park (11083842) hawon.park@stonybrook.edu
    Prog:   PotentialToken is a class used to identify if a string contains a potential token
 */
package Lexer;

public class PotentialToken {

    //empty constructor
    public PotentialToken(){}

    //return token if text at index 0 contains a punctuation token, null if otherwise
    public tokenInfo isPunct(String text){
        //first character of text
        String x = text.substring(0,1);
        if(x.equals(";"))
            return new tokenInfo(";",tokenInfo.Type.SEMICOLON);
        else if(x.equals("("))
            return new tokenInfo("(",tokenInfo.Type.LPAREN);
        else if(x.equals(")"))
            return new tokenInfo(")",tokenInfo.Type.RPAREN);
        else if(x.equals(","))
            return new tokenInfo(",",tokenInfo.Type.COMMA);
        else if(x.equals("{"))
            return new tokenInfo("{",tokenInfo.Type.LBRACE);
        else if(x.equals("}"))
            return new tokenInfo("}",tokenInfo.Type.RBRACE);
        else if(x.equals("["))
            return new tokenInfo("[",tokenInfo.Type.LBRACKET);
        else if(x.equals("]"))
            return new tokenInfo("]",tokenInfo.Type.RBRACKET);
        return null;
    }

    //return token if text at index 0 contains an Operator token, null if otherwise
    public tokenInfo isOp(String text){
        //first character of text
        String x = text.substring(0,1);
        //differentiate between '=' and '=='
        if(x.equals("=")){
            if(text.length() > 1 && text.substring(0,2).equals("=="))
                return new tokenInfo("==", tokenInfo.Type.EQ);
            else
                return new tokenInfo("=", tokenInfo.Type.ASSIGN);
        }
        else if(x.equals("<"))
            return new tokenInfo("<", tokenInfo.Type.LT);
        else if(x.equals(">"))
            return new tokenInfo(">", tokenInfo.Type.GT);
        else if(x.equals("+"))
            return new tokenInfo("+", tokenInfo.Type.PLUS);
        else if(x.equals("-"))
            return new tokenInfo("-", tokenInfo.Type.MINUS);
        else if(x.equals("*"))
            return new tokenInfo("*", tokenInfo.Type.MULT);
        else if(x.equals("/"))
            return new tokenInfo("/", tokenInfo.Type.DIV);
        else if(x.equals("!"))
            return new tokenInfo("!", tokenInfo.Type.NOT);
        //if text is longer than one char
        else if(text.length() > 1){
            //check if text contains the '&&' token
            if(text.substring(0,2).equals("&&"))
                return new tokenInfo("&&", tokenInfo.Type.AND);
            //check if text contains the '||' token
            else if(text.substring(0,2).equals("||"))
                return new tokenInfo("||", tokenInfo.Type.OR);
        }
        return null;
    }

    //return token if text starting at index 0 contains a Keyword, null if otherwise
    public tokenInfo isKey(String text){
        //check if text contains a key word while not going out of bounds
        if(text.length()>=5 && text.substring(0,5).equals("while")) {
            return new tokenInfo(text.substring(0, 5), tokenInfo.Type.WHILE);
        } else if(text.length()>=5 && text.substring(0,5).equals("float")) {
            return new tokenInfo(text.substring(0, 5), tokenInfo.Type.FLOAT);
        } else if(text.length()>=5 && text.substring(0,5).equals("print")) {
            return new tokenInfo(text.substring(0, 5), tokenInfo.Type.PRINT);
        } else if(text.length()>=4 && text.substring(0,4).equals("else")) {
            return new tokenInfo(text.substring(0, 4), tokenInfo.Type.ELSE);
        } else if(text.length()>=4 && text.substring(0,4).equals("read")) {
            return new tokenInfo(text.substring(0, 4), tokenInfo.Type.READ);
        } else if(text.length()>=3 && text.substring(0,3).equals("int")) {
            return new tokenInfo(text.substring(0, 3), tokenInfo.Type.INT);
        } else if(text.length()>=2 && text.substring(0,2).equals("if")) {
            return new tokenInfo(text.substring(0, 2), tokenInfo.Type.IF);
        } else
            return null;
    }

    //return token if text starting at index 0 contains an identifier, null if otherwise
    public tokenInfo isID(String text){
        char first = text.charAt(0);                //first char of text
        text = text.substring(1);                   //get rid of first char
        String temp = "" + first;                   //String to hold the identifier

        //first char must be an alphabet or underscore
        if(Character.isLetter(first) || first == '_'){
            //iterate through string, one character at a time
            for (char current : text.toCharArray()){
                //following char must be alphanumeric or an underscore
                if(Character.isLetterOrDigit(current) || current == '_')
                    temp += current;
                else
                    break;
            }
            //check that identifier is not a keyword
            if (
                    temp.toLowerCase().equals("if") ||
                    temp.toLowerCase().equals("else") ||
                    temp.toLowerCase().equals("while") ||
                    temp.toLowerCase().equals("int") ||
                    temp.toLowerCase().equals("float") ||
                    temp.toLowerCase().equals("print") ||
                    temp.toLowerCase().equals("read")
            ){
                return null;
            }
            return new tokenInfo(temp, tokenInfo.Type.ID);
        }
        else
            return null;
    }

    public tokenInfo isInvalidChar(String text){
        if(text.length()>=2){
            if(!Character.isLetter(text.charAt(0))){
                return new tokenInfo(text.substring(0,1), tokenInfo.Type.INVALID);
            }
        }
        return null;
    }

    //return token if text starting at index 0 contains an Integer, null if otherwise
    public tokenInfo isInteger(String text){
        char first = text.charAt(0);                    //first character of text
        text = text.substring(1);                       //get rid of first char
        String temp = "" + first;                       //string to hold the integer

        //initial character must be digit or the minus sign
        if(Character.isDigit(first) || first == '-'){
            //iterate through text, one character at a time
            for(char current : text.toCharArray()){
                //following characters must be digits
                if(Character.isDigit(current))
                    temp += current;
                else
                    break;
            }
            return new tokenInfo(temp, tokenInfo.Type.ICONST);
        }
        else
            return null;
    }

    //return token if text starting at index 0 contains a comment, null if otherwise
    public tokenInfo isComment(String text){
        String temp = "";
        //ensure text is at least 4 characters long, which is the shortest comment possible
        if(text.length()>3){
            //get first two characters of text
            String open = text.substring(0,2);
            //if first two char are '/*' and text contains closing '*/' part of the comment
            if(open.equals("/*") && text.contains("*/")){
                //return token containing comment
                int close = text.indexOf("*/")+2;
                temp = text.substring(0,close);
                return new tokenInfo(temp, tokenInfo.Type.COMMENT);
            }
        }
        return null;
    }

    //return token if text starting at index 0 contains a normal/fractional float, null if otherwise
    public tokenInfo isNormalFloat(String text){
        char first = text.charAt(0);
        String temp = "";

        //initial character is a digit
        if(Character.isDigit(first) && text.length() > 2){
            int pointIndex = text.indexOf(".");
            //ensure there is at least one digit before the decimal place
            if(pointIndex != -1 && pointIndex >= 1){
                temp = text.substring(0,pointIndex+1);
                //ensure concatenated string till far only contains numbers
                if(!isNum(temp.substring(0,temp.length()-1)))
                    return  null;
                //get decimal places of floating point
                text = text.substring(pointIndex+1);
                //ensure that text contains decimal places
                //if no decimal places found, means that this is a float without decimal places
                if(!text.substring(0,1).equals(";")){
                    tokenInfo tempInt = isInteger(text);
                    temp += tempInt.getTokenText();
                }
                return new tokenInfo(temp, tokenInfo.Type.FCONST);
            }
        //initial character is the minus sign
        } else if(first == '-' && text.length() > 3){
            int pointIndex = text.indexOf(".");
            //ensure there is at least one digit before the decimal place
            if(pointIndex != -1 && pointIndex >= 2){
                temp = text.substring(0,pointIndex+1);
                //ensure concatenated string till far only contains numbers
                if(!isNum(temp.substring(0,temp.length()-1)))
                    return  null;
                //get decimal places of floating point
                text = text.substring(pointIndex+1);
                //ensure that text contains decimal places
                //if no decimal places found, means that this is a float without decimal places
                if(!text.substring(0,1).equals(";")){
                    tokenInfo tempInt = isInteger(text);
                    temp += tempInt.getTokenText();
                }
                return new tokenInfo(temp, tokenInfo.Type.FCONST);
            }
        }
        return null;
    }

    //return token if text starting at index 0 contains a constant/scientific float, null if otherwise
    public tokenInfo isContantFloat(String text){
        //get index of exponent character in the float
        int ex = text.toLowerCase().indexOf('e');
        //return null if there is no 'e' --> no scientific float possible
        if(ex <= 0)
            return null;
        //get mantissa portion of float
        tokenInfo mantissa = isNormalFloat(text.substring(0,ex));
        //check if mantissa is in integer format
        if(mantissa == null)
            mantissa = isInteger(text.substring(0,ex));
        //check if mantissa is in fractional format
        else{
            if(mantissa == null){
                return null;
            }
            //get exponent portion of float
            tokenInfo exponent = isInteger(text.substring(ex+1));

            //ensure that proposed token follows the lexical rules
            if(exponent != null){
                String temp = mantissa.getTokenText() + "e" + exponent.getTokenText();
                return new tokenInfo(temp, tokenInfo.Type.FCONST);
            }
        }
        return null;
    }

    //helper method that ensures entire input string is an integer
    private boolean isNum(String text){
        char first = text.charAt(0);
        //if first char is a '-', get substring of string without '-'
        if(first == '-')
            text = text.substring(1);

        for(char c : text.toCharArray()){
            if(!Character.isDigit(c))
                return false;
        }
        return true;
    }

}//class bracket
