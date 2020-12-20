package chnu.AutomataTheory.LexAnalysis;

public enum SQLTokenType implements ITokenType {
    KEYWORD("\\b(?:select|from|where|group|by|order|or|and|not|exists|having|join|left|right|inner|on|minus|intersect|union|" +
            "BeginRA|EndRA|create|alter|table|add|integer|real|text)\\b"),
    ID("[A-Za-z][A-Za-z0-9]*"),
    REAL_NUMBER("[0-9]+\\.[0-9]*"),
    NUMBER("[0-9]+"),
    STRING("(?<br>['\"])((?!\\k<br>).)*\\k<br>"),
    SPACE("\\s+"),
    COMMENT("\\-\\-[^\\n\\r]*"),
    OPERATION("[+\\-\\*/.=\\(\\)]|:="),
    DELIMITER("[;,]"),
    REL_OPERATOR("[<>]|<=|>=|<>|==|&|\\|\\|");

    private final String regex;

    SQLTokenType(String regex) {
        this.regex = regex;
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
