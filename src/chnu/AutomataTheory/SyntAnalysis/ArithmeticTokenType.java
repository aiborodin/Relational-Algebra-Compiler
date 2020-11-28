package chnu.AutomataTheory.SyntAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;

// Token == Terminal symbol

public enum ArithmeticTokenType implements ITokenType {

    ID("a|b|c"),
    PLUS("\\+"),
    ASTERISK("\\*"),
    LEFT_BRACE("\\("),
    RIGHT_BRACE("\\)"),
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
