package chnu.AutomataTheory.LexAnalysis;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String input = "beginra t4 := t2 where a4 > 30 and a4 < 30 endra";
        RegexTokenizer tokenizer = new RegexTokenizer(input, SQLTokenType.values());
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
            if(tokenType == SQLTokenType.SPACE || tokenType == SQLTokenType.COMMENT || tokenType == SQLTokenType.DELIMITER || tokenType == SQLTokenType.OPERATION) {
                if (tokenType == SQLTokenType.DELIMITER || tokenType == SQLTokenType.OPERATION) {
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
