package shimkin.eval;

import java.util.Scanner;
import java.util.Stack;

public class ExpressionCalculator {

    /**
     * тип символа
     */
    private enum Sym {
        NUM,
        SIGN,
        SPACE,
        VAR
    }
    
    /**
     * Возвращает приоритет знака
     * @param sign Знак
     * @return Приоритет знака
     */
    private int priority(char sign)
    {
        
        int prior;
        switch (sign) {
            case '+': 
            case '-':
                prior = 1;
                break;
            case '*':
            case '/':
                prior = 2;
                break;
            case '^':
                prior = 3;
                break;
            default:
                prior = 0;
        }
        return prior;
    }
    
    /**
     * Возвращает тип символа
     * @param sign Символ
     * @return Тип
     */
    private Sym symCheck(char sign)
    {
        if (sign=='+' || sign=='-' || sign=='*'|| sign=='/'|| sign==')' || sign=='('|| sign=='^')
            return Sym.SIGN;
        if (sign>='0' && sign<='9' || sign=='.')
            return Sym.NUM;
        if (sign>='a' && sign<='z' || sign>='A' && sign<='Z')
            return Sym.VAR;
        return Sym.SPACE;
    }
    
    /**
     * Вычисляет бинарное выражение
     * @param num1 Первый операнд
     * @param num2 Второй операнд
     * @param sign Оператор
     * @return Результат
     */
    private float simpleOperation(float left, float right, char sign)
    {
        float res = 0.0f;
        switch (sign)
        {
            case '-':
                res = left-right;
                break;
            case '+':
                res =  left+right;
                break;
            case '/':
                res = left/right;
                break;
            case '*':
                res = left*right;
                break;
            case '^':
                res = (float)Math.pow(left, right);
        }
        return res;
    }

    /**
     * Считает число знаков в выражении
     * @param str выражение
     * @return количество знаков
     */
    private int numOfSigns(String str)
    {
        int count = 0;
        for (int i=0; i < str.length(); i++)
        {
            if (symCheck(str.charAt(i)) == Sym.SIGN)
                count++;
        }
        return count;
    }
    
    /**
     * Проверяет расстановку скобок
     * @param exp выражение
     * @return логический результат проверки
     */
    public boolean checkBraces(String exp)
    {
        Stack stack = new Stack();
        for (char x: exp.toCharArray())
        {
            if (x == '(')
                stack.push('(');
            if (x == ')') 
            {
                if (stack.empty())
                    return false;
                stack.pop();
            }
        }
        return stack.empty();
    }
    
    /**
     * Проверка на наличие ошибок в записи выражения
     * @param exp выражение в строковой форме
     * @return ОК (нет ошибок) или ошибки в строковой форме
     */
    public String check(String exp)
    {
        if (!checkBraces(exp)) 
        {
            return "Wrong amount of braces\n";
        }
        return "OK";
    }
    
    /**
     * Удаляет пробелы
     * @param exp выражение
     * @return выражение без пробелов
     */
    private String deleteSpaces(String exp)
    {
        return exp.replace(" ", "");
    }
    
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /**
     * Исправляет ошибки записи выражение
     * @param exp выражение
     * @return исправленное выражение
     */
    private String fixExpression(String exp)
    {
        exp = ' ' + exp + ' ';
        for(int i=0; i<exp.length()-1; i++)
        {
            char x = exp.charAt(i);
            if((x == ' ' || x == '(') && exp.charAt(i+1)=='-')
            {
                int k=i+2;
                while (symCheck(exp.charAt(k))==Sym.NUM)
                    k++;
                exp = exp.substring(0,i+1)+ "(0-" + exp.substring(i+2,k) + ')' + exp.substring(k);
                i=k;
            }

            if(symCheck(x)==Sym.NUM && exp.charAt(i+1)=='(')
            {
                exp=exp.substring(0,i+1) + '*' + exp.substring(i+1);
            }
            if(x == ')' && exp.charAt(i+1)=='(')
            {
                exp=exp.substring(0,i+1) + '*' + exp.substring(i+1);
            }
        }
        return exp.substring(1,exp.length()-1);
    }

    /**
     * Проверяет, встречается ли переменная
     *      возвращает её индекс или -1, если не найдена
     * @param vars Список переменных
     * @param count Количество встреченных переменнных
     * @param name Нужная переменная
     * @return Индекс, иначе -1
     */
    public int findVar(String vars[], int count, String name)
    {
        for(int k=0;k<count;k++)
        {
            if(vars[k].equals(name))
            {
                return k;
            }
        }
        return -1;
    }
    
    /**
     * Подготавливает вычисления, определение переменных
     * @param exp выражение
     * @param scanner сканнер
     * @return Вычисленное выражение
     */
    public float calculation(String exp, Scanner scanner)
    {
        exp = ' ' + fixExpression(deleteSpaces(exp)) + ' ';

        int varsCount = 0;
        String vars[] = new String[countVars(exp)];
        float varNum[] = new float[countVars(exp)];

        int i=0;
        while(i<exp.length())
        {
            if (symCheck(exp.charAt(i))==Sym.VAR)
            {
                float var;
                int j=i;
                while(symCheck(exp.charAt(j))==Sym.VAR)
                    j++;

                String name = nextVar(exp,i);
                int k = findVar(vars, varsCount, name);

                if(k == -1) {
                    System.out.print(name + " = ");
                    var = scanner.nextFloat();
                    vars[varsCount]=name;
                    varNum[varsCount]=var;
                    varsCount++;
                }
                else
                {
                    var = varNum[k];
                }
                String str = Float.toString(var);

                exp=exp.substring(0,i) + str + exp.substring(j);
                i=i+str.length();
            }
            i++;
        }
        return toPolish(exp.substring(1,exp.length()-1));
    }

    /**
     * Строит обратную польскую запись 
     * @param exp выражение
     * @return Вычисленное выражение
     */
    public float toPolish(String exp)
    {
        float res = 0.0f;
        int countNum = 0;
        int signCount = 0;
        float[] nums = new float[countNumbers(' '+exp+' ')];
        char[] signs = new char[numOfSigns( ' '+exp+' ')];
        String polish="";

        int i=0;
        while(exp.length()>i)
        {
            //char x = exp.charAt(i);
            if(symCheck(exp.charAt(i))==Sym.SIGN)
            {
                if(signCount==0 || exp.charAt(i)=='(')
                {
                    signs[signCount] = exp.charAt(i);
                    signCount++;
                }
                else if(priority(signs[signCount-1])<priority(exp.charAt(i)))
                {
                    signs[signCount]=exp.charAt(i);
                    signCount++;
                }
                else if(exp.charAt(i) == ')')
                {
                    signCount--;
                    while(signCount>=0 && signs[signCount]!='(')
                    {
                        polish += signs[signCount];
                        signs[signCount]=' ';
                        signCount--;
                    }
                    signs[signCount]=' ';
                }
                else
                {
                    signCount--;
                    while (signCount>=0 && priority(signs[signCount])>=priority(exp.charAt(i)))
                    {
                        polish += signs[signCount];
                        signs[signCount]=' ';
                        signCount--;
                    }
                    signCount++;
                    signs[signCount]=exp.charAt(i);
                    signCount++;
                }
                i++;
            }
            else {

                nums[countNum] = nextFloat(exp, i);
                polish += '#' + Integer.toString(countNum);
                countNum++;
                while(i<exp.length() && symCheck(exp.charAt(i))==Sym.NUM)
                    i++;
            }
        }
        for(int j=signCount-1;j>=0;j--)
        {
            polish += signs[j];
        }
        return calculateReversePolish(nums, countNum, polish, polish.length()-1);
    }

    /**
     * Рекурсивная функция вычисления подстроки обратной Польской записи заданного математического выражения
     *      Начиная с символа i вычисляет значения выражения
     * @param numbers Массив чисел использующихся в выражении
     * @param countNum Количество чисел в массиве
     * @param polNote Математическое выражение в обратной Польской записи
     * @param i Начало вычислений
     * @return Вычисленное значение подстроки выражения
     */
    private float calculateReversePolish(float[] nums, int numCount, String polish, int i)
    {
        float num[] = new float[2];
        if(symCheck(polish.charAt(i))==Sym.SIGN) {
            char x = polish.charAt(i);
            i--;

            for(int k=1; k>=0; k--)
            {
                if (symCheck(polish.charAt(i)) == Sym.SIGN) {
                    num[k] = calculateReversePolish(nums, numCount, polish, i);
                    i = skipPosition(polish, i);
                } else {
                    int t = nextIntReverse(polish, i);
                    num[k] = nums[t];
                    while(t > 0) {
                        t /=10;
                        i--;
                    }
                    i--;
                }
            }

            return simpleOperation(num[0], num[1], x);
        }
        else
        {
            return nums[nextIntReverse(polish, i)];
        }
    }

    /**
     * Ищет индекс 2го операнда в Польской записи
     * @param pol Польская запись выражения
     * @param i Начало отсчета
     * @return Позиция 2го операнда
     */
    private int skipPosition(String pol, int i)
    {
        int sign =0;
        int num =0;
        while(num - sign != 1)
        {
            char x = pol.charAt(i);
            if(symCheck(pol.charAt(i))==Sym.NUM)
            {
                num++;
                while(symCheck(pol.charAt(i))==Sym.NUM)
                {
                    i--;
                }
                i--;
            }
            else if(symCheck(pol.charAt(i))==Sym.SIGN)
            {
                sign++;
                i--;
            }
            else
            {
                i--;
            }
        }
        return i;
    }
    
    /**
     * Вычисляет количество чисел в выражении
     * @param str Выражение
     * @return Количество чисел
     */
    private int countNumbers(String str)
    {
        int count=0;
        for(int i=0;i<str.length()-1;i++)
        {
            if(symCheck(str.charAt(i))==Sym.NUM && (symCheck(str.charAt(i+1))!=Sym.NUM))
                count++;
        }
        count++;
        return count;
    }

    /**
     * Вычисляет количество переменных в выражении
     * @param str Выражение
     * @return Количество переменных
     */
    private int countVars(String str)
    {
        int count=0;
        for(int i=0;i<str.length()-1;i++)
        {
            if(symCheck(str.charAt(i))==Sym.VAR && (symCheck(str.charAt(i+1))!=Sym.VAR))
                count++;
        }
        count++;
        return count;
    }

    /**
     * Возвращает первое с конца целое число с позиции i
     * @param exp выражение
     * @param i Начало отсчета
     * @return Целое 
     */
    private int nextIntReverse(String exp, int i)
    {
        String num="";
        while(i>=0 && symCheck(exp.charAt(i))==Sym.NUM)
        {
            num = exp.charAt(i) + num;
            i--;
        }
        return Integer.parseInt(num);
    }
    
    /**
     * Считает первое вещественное с позиции i
     * @param exp Выражение
     * @param i Начало отсчета
     * @return число
     */
    private float nextFloat(String exp, int i)
    {
        String num="";
        while(i<exp.length() && symCheck(exp.charAt(i))==Sym.NUM)
        {
            num += exp.charAt(i);
            i++;
        }
        return Float.parseFloat(num);
    }

    /**
     * Возвращает имя переменной начиная с позиции i
     * @param str выражение
     * @param i Начало отсчета
     * @return Переменная
     */
    private String nextVar(String str,int i)
    {
        String ret = "";
        while (i<str.length() && symCheck(str.charAt(i))==Sym.VAR)
        {
            ret += str.charAt(i);
            i++;
        }
        return ret;
    }

}
