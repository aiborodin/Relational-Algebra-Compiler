package chnu.AutomataTheory.LexAnalysis;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {

    private final List<Token> tokens = new ArrayList<>();

    public void addToken(Token token) {
        tokens.add(token);
    }

    public int size() {
        return tokens.size();
    }

    public Token getToken(int i) {
        return tokens.get(i);
    }
}

