package shimkin.eval;

import java.util.Scanner;

/**
 *
 * @author User
 */
public class Eval {
    
    public static void calculate(String exp) 
    {
        ExpressionCalculator calc = new ExpressionCalculator();
        Scanner scan = new Scanner(System.in);
        
        String errors = calc.check(exp);
        if (errors.equals("OK")) {
            float res = 0;
            boolean success = true;
            try {
                res = calc.calculation(exp, scan);
            }
            catch (Exception ex) {
                System.out.println("Внутренняя ошибка вычисления: " + ex.getMessage());
                success = false;
            }
            if (success) {
                System.out.println("COUNTED");
                System.out.println(exp + " = " + res);
            }
        }
        else {
            System.out.println("ERROR");
            System.out.println(errors);
        }
    }

    public static void main(String[] args) {
        
        
        String[] exps = new String[] {
            "100 + 200",
            "14 + 2*(-5) + (8-9)*14",
            "16 + 2*x^3",
        };
        //System.out.println(exps.length);
        for (String exp : exps) {
            calculate(exp);
        }
                
        Scanner scan = new Scanner(System.in);
        String exp = scan.nextLine();
        while (!exp.equals("quit"))
        {
            calculate(exp);
        }
    }
}