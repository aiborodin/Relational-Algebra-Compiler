package chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar;

import chnu.AutomataTheory.LexAnalysis.ITokenType;

// Token == Terminal symbol

public enum ArithmeticTokenType implements ITokenType {

    ID("a|b|c"), // NUMBER("-?([0-9]+\\.[0-9]*|[0-9]+)")
    PLUS("\\+|-"),
    ASTERISK("\\*|/"),
    LEFT_PARENTHESIS("\\("),
    RIGHT_PARENTHESIS("\\)"),
    EOS("\\$");

    private final String regex;

    ArithmeticTokenType(String regex) {
        this.regex = regex;
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
