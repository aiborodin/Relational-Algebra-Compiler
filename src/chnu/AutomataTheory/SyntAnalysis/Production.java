package chnu.AutomataTheory.SyntAnalysis;

import java.util.List;

// Context-free grammar production rule

public class Production {

    private final NonTerminal leftSide;

    private final List<Object> rightSide;

    public Production(NonTerminal leftSide, List<Object> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public NonTerminal getLeftSide() {
        return leftSide;
    }

    public List<Object> getRightSide() {
        return rightSide;
    }
}
