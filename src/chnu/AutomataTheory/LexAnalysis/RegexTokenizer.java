package chnu.AutomataTheory.LexAnalysis;

import chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTokenizer implements Enumeration<Token> {

    private final String content;

    private final ITokenType[] tokenTypes;

    private final Matcher matcher;

    private int currentPosition = 0;

    private final SymbolTable symbolTable;

    public RegexTokenizer(String content, ITokenType[] tokenTypes) {
        this.content = content;
        this.tokenTypes = tokenTypes;
        this.symbolTable = new SymbolTable();

        List<String> regexList = new ArrayList<>();
        for (int i = 0; i < tokenTypes.length; i++) {
            ITokenType tokenType = tokenTypes[i];
            regexList.add("(?<g" + i + ">" + tokenType.getRegex() + ")");
        }
        String regex = String.join("|", regexList);

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        matcher = pattern.matcher(content);
        matcher.find();
    }

    @Override
    public boolean hasMoreElements() {
        return currentPosition < content.length();
    }

    @Override
    public Token nextElement() {
        boolean found = currentPosition <= matcher.start() || matcher.find();

        int start = found ? matcher.start() : content.length();
        int end = found ? matcher.end() : content.length();

        if(found && currentPosition == start) {
            currentPosition = end;
            for (int i = 0; i < tokenTypes.length; i++) {
                String groupName = "g" + i;
                if (matcher.start(groupName) == start && matcher.end(groupName) == end) {
                    return createToken(content, tokenTypes[i], start, end);
                }
            }
        }
        throw new IllegalStateException("Unexpected token at position " + currentPosition);
    }

    protected Token createToken(String content, ITokenType tokenType, int start, int end) {
        Token token = new Token(content.substring(start, end), tokenType, start);
        if (tokenType != REALTokenType.SPACE && tokenType != REALTokenType.COMMENT) {
            symbolTable.addToken(token);
        }
        return token;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}