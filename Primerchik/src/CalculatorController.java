class CalculatorController {
    private CalculatorModel model = new CalculatorModel();
    private CalculatorView view = new CalculatorView();

    public void run() {
        while (true) {
            String input = view.getInput();
            if (input.equalsIgnoreCase("exit")) break;

            String error = model.validate(input);
            if (!error.isEmpty()) {
                view.displayError(error);
                continue;
            }

            try {
                double result = model.evaluate(input);
                view.displayResult(result);
            } catch (Exception e) {
                view.displayError(e.getMessage());
            }
        }
    }
}