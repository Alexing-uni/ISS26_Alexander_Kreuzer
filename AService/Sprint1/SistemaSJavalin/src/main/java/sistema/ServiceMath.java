package main.java.sistema;

public class ServiceMath {
    
    // Método que recibe la operación y los dos números, y devuelve el resultado
    public static double doCalc(String op, double a, double b) throws IllegalArgumentException {
        switch (op.toLowerCase()) {
            case "add": 
            case "suma": return a + b;
            
            case "sub": 
            case "resta": return a - b;
            
            case "mul": 
            case "mult": return a * b;
            
            case "div": 
                if (b == 0) throw new IllegalArgumentException("División por cero no permitida");
                return a / b;
                
            default:
                throw new IllegalArgumentException("Operación desconocida: " + op);
        }
    }
}