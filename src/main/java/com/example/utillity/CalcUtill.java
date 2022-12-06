package com.example.utillity;

import java.util.List;

public class CalcUtill {

    public static final int INVALID = -1;

    public static CalcResult performOp (String operationStr, List<Integer> parameters) {

        Operation operation = Operation.getMatchOperation(operationStr);

        switch (operation) {
            case PLUS :
                if (parameters.size() > 2) { return new CalcResult(false, "Error: Too many arguments to perform the operation Plus", INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Plus", INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)+parameters.get(1)); }

            case MINUS :
                if (parameters.size() > 2) { return new CalcResult(false, "Error: Too many arguments to perform the operation Minus", INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Minus", INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)-parameters.get(1));}

            case TIMES:
                if (parameters.size() > 2) { return new CalcResult(false, "Error: Too many arguments to perform the operation Times", INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Times", INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)*parameters.get(1)); }

            case DIVIDE:
                if (parameters.size() > 2) { return new CalcResult(false, "Error: Too many arguments to perform the operation Divide", INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Divide", INVALID); }
                else if (parameters.get(1)==0) { return new CalcResult(false, "Error while performing operation Divide: division by 0", INVALID);}
                else { return new CalcResult(true, "", parameters.get(0)/parameters.get(1)); }

            case POW :
                if (parameters.size() > 2) { return new CalcResult(false, "Error: Too many arguments to perform the operation Pow", INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Pow", INVALID); }
                else { return new CalcResult(true, "" ,  parameters.get(0)^parameters.get(1)); }

            case ABS:
                if (parameters.size() > 1) { return new CalcResult(false, "Error: Too many arguments to perform the operation Abs", INVALID); }
                else if (parameters.size() < 1) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Abs", INVALID); }
                else { return new CalcResult(true, "", Math.abs(parameters.get(0)) ); }

            case FACT:
                if (parameters.size() > 1) { return new CalcResult(false, "Error: Too many arguments to perform the operation Fact", INVALID); }
                else if (parameters.size() < 1) { return new CalcResult(false, "Error: Not enough arguments to perform the operation Fact", INVALID); }
                else if (parameters.get(0) < 0) { return new CalcResult(false, "Error while performing operation Factorial: not supported for the negative number", INVALID);}
                else { return new CalcResult(true, "", factorial(parameters.get(0))); }

            case UNDEFINED:
                return new CalcResult(false, "Error: unknown operation: " + operationStr, INVALID);

        }

        return new CalcResult(false, "Error", INVALID);
    }

    public static int factorial (int n) {
        int i;
        int fact = 1;

        for(i = 1; i <= n ; i++){
            fact = fact*i;
        }

        return fact;
    }
}
