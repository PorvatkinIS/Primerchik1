import java.util.Scanner;

class CalculatorView {
    private Scanner scanner = new Scanner(System.in);

    public String getInput() {
        System.out.print("Введите уравнение: ");
        return scanner.nextLine();
    }

    public void displayResult(double result) {
        System.out.println("Результат: " + result);
    }

    public void displayError(String message) {
        System.err.println("Ошибка: " + message);
    }
}