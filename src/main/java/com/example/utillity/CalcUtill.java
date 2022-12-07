package com.example.utillity;

import java.util.List;

public class CalcUtill {

    public static final int INVALID = -1;

    public static CalcResult performOp (String operationStr, List<Integer> parameters) {

        Operation operation = Operation.getMatchOperation(operationStr);

        switch (operation) {
            case PLUS :
                if (parameters.size() > 2) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)+parameters.get(1));}

            case MINUS :
                if (parameters.size() > 2) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)-parameters.get(1));}

            case TIMES:
                if (parameters.size() > 2) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else { return new CalcResult(true, "", parameters.get(0)*parameters.get(1)); }

            case DIVIDE:
                if (parameters.size() > 2) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.get(1)==0) { return new CalcResult(false, CalcError.DIVISION_BY_ZERO, INVALID);}
                else { return new CalcResult(true, "", parameters.get(0)/parameters.get(1)); }

            case POW :
                if (parameters.size() > 2) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 2) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else { return new CalcResult(true, "" , (int) Math.pow(parameters.get(0), parameters.get(1))); }

            case ABS:
                if (parameters.size() > 1) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 1) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else { return new CalcResult(true, "", Math.abs(parameters.get(0)) ); }

            case FACT:
                if (parameters.size() > 1) { return new CalcResult(false, CalcError.TOO_MANY_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.size() < 1) { return new CalcResult(false, CalcError.NOT_ENOUGH_ARGUMENTS_TO_PERFORM_THE_OPERATION + operationStr, INVALID); }
                else if (parameters.get(0) < 0) { return new CalcResult(false, CalcError.FACTORIAL_WITH_NEGATIVE_NUM, INVALID);}
                else { return new CalcResult(true, "", factorial(parameters.get(0))); }

            case UNDEFINED:
                return new CalcResult(false, CalcError.UNKNOWN_OPERATION + operationStr, INVALID);

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
