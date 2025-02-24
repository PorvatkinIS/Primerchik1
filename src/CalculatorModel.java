import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CalculatorModel {
    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList("+", "-", "*", "/", "^", "//"));
    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();

    static {
        PRECEDENCE.put("^", 4);
        PRECEDENCE.put("*", 3);
        PRECEDENCE.put("/", 3);
        PRECEDENCE.put("//", 3);
        PRECEDENCE.put("+", 2);
        PRECEDENCE.put("-", 2);
    }

    public String validate(String expression) {
        if (expression == null || expression.trim().isEmpty())
            return "Пустое выражение";
        if (!expression.matches("^[\\d\\-+*/^()\\s.]+$"))
            return "Недопустимые символы";
        if (expression.matches("^[+*/^).]|.*[+\\-*/^(]$"))
            return "Выражение должно начинаться и заканчиваться числом";

        // Проверка скобок
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') stack.push(c);
            else if (c == ')') {
                if (stack.isEmpty()) return "Несбалансированные скобки";
                stack.pop();
            }
        }
        if (!stack.isEmpty()) return "Несбалансированные скобки";

        // Проверка токенов
        List<String> tokens = tokenize(expression);
        if (tokens.size() > 100) return "Слишком много элементов (максимум 100)";
        if (!isNumber(tokens.get(0))) return "Начало выражения не число";
        if (!isNumber(tokens.get(tokens.size()-1))) return "Конец выражения не число";

        // Проверка операторов
        String prev = null;
        for (String token : tokens) {
            if (OPERATORS.contains(token)) {
                if (prev != null && OPERATORS.contains(prev))
                    return "Два оператора подряд";
            }
            prev = token;
        }
        return "";
    }

    public List<String> tokenize(String expr) {
        expr = expr.replaceAll("\\s+", "");
        Matcher m = Pattern.compile("//|\\d+\\.?\\d*|\\.\\d+|[-+*/^()]").matcher(expr);
        List<String> tokens = new ArrayList<>();
        while (m.find()) tokens.add(m.group());

        // Обработка унарных операторов
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("-") && (i == 0 || tokens.get(i-1).matches("[-+*/^(]"))) {
                processed.add(tokens.get(i) + tokens.get(i+1));
                i++;
            } else {
                processed.add(token);
            }
        }
        return processed;
    }

    public double evaluate(String expression) throws Exception {
        List<String> tokens = tokenize(expression);
        List<String> rpn = shuntingYard(tokens);
        return evaluateRPN(rpn);
    }

    private List<String> shuntingYard(List<String> tokens) {
        Deque<String> stack = new ArrayDeque<>();
        List<String> output = new ArrayList<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.peek().equals("("))
                    output.add(stack.pop());
                stack.pop();
            } else {
                while (!stack.isEmpty() &&
                        PRECEDENCE.getOrDefault(stack.peek(), 0) >= PRECEDENCE.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }
        while (!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    private double evaluateRPN(List<String> rpn) throws Exception {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("Деление на ноль");
                        stack.push(a / b); break;
                    case "//":
                        if (b == 0) throw new ArithmeticException("Деление на ноль");
                        stack.push((double)((int)a / (int)b)); break;
                    case "^": stack.push(Math.pow(a, b)); break;
                    default: throw new Exception("Неизвестный оператор");
                }
            }
        }
        return stack.pop();
    }

    private boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }
}