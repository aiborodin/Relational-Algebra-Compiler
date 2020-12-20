package chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar;

import chnu.AutomataTheory.LexAnalysis.ITokenType;

public enum REALTokenType implements ITokenType {
    BEGIN_RA("\\bBeginRA\\b"),
    END_RA("\\bEndRA\\b"),
    DROP_TABLE("\\bDrop\\s+table\\b"),
    UPDATE("\\bUpdate\\b"),
    SET("\\bSet\\b"),
    WHERE("\\bWhere\\b"),
    NULL("\\bNULL\\b"),
    LEFT_JOIN("\\bLeft\\s+join\\b"),
    ON("\\bOn\\b"),
    MINUS("\\bMinus\\b"),
    ID("[A-Za-z][A-Za-z0-9]*"),
    SEMICOLON(";"),
    COMMA(","),
    LEFT_BRACE("\\("),
    RIGHT_BRACE("\\)"),
    DOT("\\."),
    NUMBER("[0-9]+|[0-9]+\\.[0-9]*"),
    STRING("(?<br>['\"])((?!\\k<br>).)*\\k<br>"),
    REL_OP("[<>]|<=|>=|<>|==|&|\\|\\|"),
    AND("\\bAND\\b|&"),
    OR("\\bOR\\b|\\|"),
    SPACE("\\s+"),
    COMMENT("\\-\\-[^\\n\\r]*"),
    EQUALS("="),
    ASSIGN(":="),
    OPERATION("[+\\-\\*/]"),
    EOS("\\$");

    private final String regex;

    REALTokenType(String regex) {
        this.regex = regex;
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
