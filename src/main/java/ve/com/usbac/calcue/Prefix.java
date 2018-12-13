package ve.com.usbac.calcue;

import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author Usbac
 */
public class Prefix {
    
    private final static String EMPTY = "";
    private final static char NEW_LINE = '\n';
    static int index = 0;
    
    
    /**
     * Moves a number to the stack
     * @param main the stack
     * @param string the string where the number is
     */
    public static void processNumberIntoStack(Stack main, String string) {
        String number = EMPTY;
        while (index < string.length() && (Character.isDigit(string.charAt(index)) || string.charAt(index) == '.')) {
            number += string.charAt(index++);
        }
        main.push(number);
    }
    
    
    /**
     * Moves a word to the stack
     * @param main the main stack
     * @param operators the operators stack
     * @param string the string where the word is
     */
    public static void processWordIntoStack(Stack main, Stack operators, String string) {
        String word = EMPTY;
        while(index < string.length() && Character.isLetter(string.charAt(index))) {
            word += string.charAt(index++);
        }
        while (operators.size() > 0 && (hasMoreHierarchy(operators.lastElement(), word) || 
                                        hasSameHierarchy(operators.lastElement(), word))) {
            main.push(operators.pop());
        }
        if (isFunction(word))
            operators.push(word);
        else
            main.push(word);
    }
    
    
    /**
     * Moves everything from the operators stack to the main stack until a closed parenthesis is found
     * @param main the main stack
     * @param operators the operators stack
     * @param string the string where the parenthesis is
     */
    public static void processParenthesisIntoStack(Stack main, Stack operators, String string) {
        while (!operators.isEmpty() && !operators.lastElement().toString().equals("(")) {
            main.push(operators.pop());
        }
        
        if (operators.lastElement().toString().equals("(")) {
            operators.pop();
        }
        
        index++;
    }        

    
    /**
     * Moves everything from the operators stack to the main stack
     * @param origin the stack which values will be move from
     * @param destiny the stack which values will be move to
     */
    public static void moveStackFromTo(Stack origin, Stack destiny) {
        while (!origin.isEmpty()) {
            destiny.push(origin.pop());
        }
    }
    
    
    /**
     * Returns <code>true</code> if the first operator has more hierarchy than the second, <code>false</code> otherwhise
     * @param first the first operator
     * @param second the second operator
     * @return <code>true</code> if the first operator has more hierarchy than the second, <code>false</code> otherwhise
     */
    public static boolean hasMoreHierarchy(Object first, Object second) {
        if (first.toString().equals("(") || second.toString().equals("("))
            return false;
         
        if (isFunction(first) && !isFunction(second))
            return true;

        if (first.toString().equals("^") || first.toString().equals("sqrt") || first.toString().equals("%")) {
            return !(second.toString().equals("^") || second.toString().equals("sqrt") || second.toString().equals("%"));
        }
        
        if (first.toString().equals("/") || first.toString().equals("*")) {
            return (second.toString().equals("+") || second.toString().equals("-"));
        }
        
        if (!(first.toString().equals("+") || first.toString().equals("-"))) {
            return (second.toString().equals("+") || second.toString().equals("-"));
        }
        
        return false;
    }

    
    /**
     * Returns <code>true</code> if the first operator has the same hierarchy than the second, <code>false</code> otherwhise
     * @param first the first operator
     * @param second the second operator
     * @return <code>true</code> if the first operator has the same hierarchy than the second, <code>false</code> otherwhise
     */
    public static boolean hasSameHierarchy(Object first, Object second) {
        if (first.toString().equals("(") && second.toString().equals("("))
            return false;
         
        if (isFunction(first) && isFunction(second))
            return true;

        if (first.toString().equals("^") || first.toString().equals("sqrt") || first.toString().equals("%")) {
            return (second.toString().equals("^") || second.toString().equals("sqrt") || second.toString().equals("%"));
        }
      
        if (first.toString().equals("/") || first.toString().equals("*")) {
            return (second.toString().equals("/") || second.toString().equals("*"));
        }
                
        if (first.toString().equals("+") || first.toString().equals("-")) {
            return (second.toString().equals("+") || second.toString().equals("-"));
        }
        
        return false;
    }
    
    
    /**
     * Removes all the concurrences with the string in the stack
     * @param stack the stack
     * @param element the element which will be compared to
     */
    public static void removeElements(Stack stack, String element) {
        for (int i = 0; i < stack.size(); i++) {
            if (stack.elementAt(i).toString().equals(element))
                stack.remove(i);
        }
    }
    
    
    /**
     * Converts a string in infix notation to prefix notation
     * @param string the string which is in infix notation
     * @return the string converted to prefix notation
     */
    public static Stack convertToPrefix(String string) {
        Stack main = new Stack(), 
              operators = new Stack();
        
        for (index = 0; index < string.length();) {
            //Number
            if (isNumber(string.charAt(index))) {
                processNumberIntoStack(main, string);
                
            //Word (Variable/Function)
            } else if (Character.isLetter(string.charAt(index))) {
                processWordIntoStack(main, operators, string);
                
            //Operator
            } else if (string.charAt(index) != ')') {
                while (!operators.isEmpty() && (hasMoreHierarchy(operators.lastElement(), string.charAt(index)) || 
                                                hasSameHierarchy(operators.lastElement(), string.charAt(index)))) {
                    main.push(operators.pop());
                }
                operators.push(string.charAt(index++));
                
            //Parenthesis
            } else {
                processParenthesisIntoStack(main, operators, string);
            }
        }
        
        moveStackFromTo(operators, main);
        //Remove unwanted parenthesis
        removeElements(main, "(");
        return main;
    }
    
    
    /**
     * Returns <code>true</code> is the object is a valid number, <code>false</code> otherwhise
     * @param object the object to evaluate
     * @return <code>true</code> is the object is a valid number, <code>false</code> otherwhise
     */
    public static boolean isNumber(Object object) {
        try {
            Float.parseFloat(object.toString());
        } catch (Exception e) { 
            return false; 
        }
        return true;
    }
    
    
    /**
     * Returns <code>true</code> is the object is a operand, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a operand, <code>false</code> otherwhise
     */
    public static boolean isOperand(Object o) {
        return (Arrays.asList("+", "-", "*", "/", "^").contains(o.toString()));
    }
    
    
    /**
     * Returns <code>true</code> is the object is a reserved function, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a reserved function, <code>false</code> otherwhise
     */
    public static boolean isFunction(Object o) {
        return (Arrays.asList("sqrt", "sin", "cos", "tan", "asin", "acos", "atan", "log", "floor", "ceil", "abs", "rand", "%")
                      .contains(o.toString()));
    }
    
    
    /**
     * Returns <code>true</code> is the object is a variable, <code>false</code> otherwhise
     * @param o the object to evaluate
     * @return <code>true</code> is the object is a variable, <code>false</code> otherwhise
     */
    public static boolean isVariable(Object o) {
        return (!isNumber(o) && !isOperand(o) && !isFunction(o));
    }
    
    
    /**
     * Solves an arithmetic operation
     * @param operand the operand
     * @param a the first number
     * @param b the second number
     * @return the result of the operation
     */
    public static double solveOperation(Object operand, double a, double b) {
        switch (operand.toString()) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "^": return Math.pow(a, b);
        }
        return 0;
    }
    
    
    /**
     * Solves a function
     * @param operand the operand/function
     * @param a the number
     * @return the result of the operation
     */
    public static double solveFunction(Object operand, double a) {
        switch (operand.toString()) {
            case "abs": return Math.abs(a);
            case "rand": return Math.random()*a;
            case "sqrt": return Math.sqrt(a);
            case "sin": return Math.sin(a);
            case "cos": return Math.cos(a);
            case "tan": return Math.tan(a);
            case "acos": return Math.acos(a);
            case "atan": return Math.atan(a);
            case "asin": return Math.asin(a);
            case "log": return Math.log(a);
            case "floor": return Math.floor(a);
            case "ceil": return Math.ceil(a);
            case "%": return a / 100f;
        }
        return 0;
    }
    
    
    /**
     * Solves a operation in the indicated index of the stack
     * @param stack the stack where the operation is
     * @param i the index where the operation is
     */
    public static void solveOperationInPrefix(Stack stack, int i) {        
        if (stack.size() > 2) {
            double a = Double.parseDouble(stack.remove(i - 2).toString());
            double b = Double.parseDouble(stack.remove(i - 2).toString());
            Object operand = stack.remove(i - 2);
            stack.add(i - 2, solveOperation(operand, a, b));
        } else {
            float b = Float.parseFloat(stack.remove(0).toString());
            Object operand = stack.remove(0);
            stack.push(solveOperation(operand, 0, b));
        }
    }
    
    
    /**
     * Solves a function in the indicated index of the stack
     * @param stack the stack where the function is
     * @param i the index where the function is
     */
    public static void solveFunctionInPrefix(Stack stack, int i) {
        double a = Double.parseDouble(stack.remove(i - 1).toString());
        Object operand = stack.remove(i - 1);
        stack.add(i - 1, solveFunction(operand, a));
    }
    
    
    /**
     * Replaces a variable with its respective value
     * @param stack the stack where the variable is
     * @param values the string with the variable declared
     * @param i the index where the variable is
     */
    public static void replaceVariableWithValue(Stack stack, String values, int i) {
        String variableName = stack.elementAt(i).toString(),
               newValue = EMPTY;
        if (!values.contains(variableName + "=")) {
            stack.set(i, "0");
            return;
        }
        
        int variablePosition = values.lastIndexOf(variableName + "=") + variableName.length() + 1;
        values = values.replaceAll(";", String.valueOf(NEW_LINE));
        while (variablePosition < values.length() && values.charAt(variablePosition) != NEW_LINE) {
            newValue += values.charAt(variablePosition++);
        }
        
        stack.set(i, convertToAndSolvePrefix(newValue, values));
    }

    
    /**
     * Solves a prefix function
     * @param stack the prefix operation
     * @param values the string with variables declarated
     * @return a one size stack with the result
     */
    public static Stack solvePrefix(Stack stack, String values) {
        for (int i = 0; i < stack.size(); i++) {
            if (isOperand(stack.elementAt(i))) {
                solveOperationInPrefix(stack, i);
                break;
            }
            
            if (isFunction(stack.elementAt(i))) {
                solveFunctionInPrefix(stack, i);
                break;
            }
            
            if (isVariable(stack.elementAt(i))) {
                replaceVariableWithValue(stack, values, i);
                break;
            }
        }
        //Recursive until solve all the operations
        if (stack.size() > 1)
            solvePrefix(stack, values);
        return stack;
    }
    
    
    /**
     * Converts a function from Infix to Prefix and solves it
     * @param string the Infix function
     * @param values the string with variables declarated
     * @return the result of the function
     */
    public static String convertToAndSolvePrefix(String string, String values) {
        string = string.replaceAll(" ", EMPTY);
        values = values.replaceAll(" ", EMPTY);
        Stack main = convertToPrefix(string);
        Double result = Double.parseDouble(solvePrefix(main, values)
                              .firstElement()
                              .toString());
        
        //Round
        if (result % 1 == 0)
            return String.valueOf(result.intValue());
        return String.format("%.4f", result);
    }
    
}