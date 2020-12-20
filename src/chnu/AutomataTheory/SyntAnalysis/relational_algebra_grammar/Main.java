package chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.LexAnalysis.RegexTokenizer;
import chnu.AutomataTheory.LexAnalysis.Token;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.printf("%-15s %-20s %-10s %-10s %-10s %n", "Token", "Lexeme", "Start", "Length", "Position");
        Token token;
        ITokenType tokenType;
        String lexeme;
        int lexStart = 1, lexPosition = 1;
        Map<String, Integer> positionTracker = new HashMap<>();
        RegexTokenizer tokenizer = new RegexTokenizer("beginra T1 := T2 where a2 > 30 endra", REALTokenType.values());
        while(tokenizer.hasMoreElements()) {
            token = tokenizer.nextElement();
            tokenType = token.getType();
            lexeme = token.getText();
            if(tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.SPACE || tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.COMMENT || tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.DELIMITER || tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.OPERATION) {
                if (tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.DELIMITER || tokenType == chnu.AutomataTheory.LexAnalysis.REALTokenType.OPERATION) {
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
