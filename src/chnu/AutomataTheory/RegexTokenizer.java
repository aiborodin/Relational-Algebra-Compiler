package chnu.AutomataTheory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTokenizer implements Enumeration<Token> {

    private final String content;

    private final ITokenType[] tokenTypes;

    private final Matcher matcher;

    private int currentPosition = 0;

    public RegexTokenizer(String content, ITokenType[] tokenTypes) {
        this.content = content;
        this.tokenTypes = tokenTypes;

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
        return new Token(content.substring(start, end), tokenType, start);
    }

    public enum REALTokenType implements ITokenType {
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

        REALTokenType(String regex) {
            this.regex = regex;
        }

        @Override
        public String getRegex() {
            return regex;
        }
    }

    public static void main(String[] args) {
        String input = new Scanner(System.in).nextLine();
        RegexTokenizer tokenizer = new RegexTokenizer(input, REALTokenType.values());
        System.out.printf("%-15s %-20s %-10s %-10s %-10s %n", "Token", "Lexeme", "Start", "Length", "Position");
        Token token;
        ITokenType tokenType;
        String lexeme;
        int lexStart = 1, lexPosition = 1;
        Map<String, Integer> positionTracker = new HashMap<>();
        while(tokenizer.hasMoreElements()) {
            token = tokenizer.nextElement();
            tokenType = token.getType();
            lexeme = token.getText();
            if(tokenType == REALTokenType.SPACE || tokenType == REALTokenType.COMMENT || tokenType == REALTokenType.DELIMITER || tokenType == REALTokenType.OPERATION) {
                if (tokenType == REALTokenType.DELIMITER || tokenType == REALTokenType.OPERATION) {
                    lexStart += lexeme.length();
                }
                continue;
            }
            if (positionTracker.containsKey(lexeme)) {
                System.out.printf("%-15s %-20s %-10s %-10s %-10s %n", token.getType(), lexeme, lexStart, lexeme.length(), positionTracker.get(lexeme));
            } else {
                System.out.printf("%-15s %-20s %-10s %-10s %-10s %n", token.getType(), lexeme, lexStart, lexeme.length(), lexPosition);
                positionTracker.put(lexeme, lexPosition);
            }
            lexStart += lexeme.length();
            lexPosition++;
        }
    }
}


class Token {

    private final String text;

    private final ITokenType type;

    private final int start;

    public Token(String text, ITokenType type, int start) {
        this.text = text;
        this.type = type;
        this.start = start;
    }

    public String getText() {
        return text;
    }

    public ITokenType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }
}

interface ITokenType {
    String getRegex();
}