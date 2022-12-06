package com.example.utillity;

public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE, POW, ABS, FACT, UNDEFINED;

    public static Operation getMatchOperation( String opStr ) {
        opStr = opStr.toUpperCase();

        switch (opStr) {
            case "PLUS":
                return PLUS;
            case "MINUS" :
                return MINUS;
            case "TIMES" :
                return TIMES;
            case "DIVIDE" :
                return DIVIDE;
            case "POW" :
                return POW;
            case "ABS" :
                return ABS;
            case "FACT" :
                return FACT;
            default :
                return  UNDEFINED;
        }
    }
}
