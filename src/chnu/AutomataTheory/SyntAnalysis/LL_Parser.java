package chnu.AutomataTheory.SyntAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.LexAnalysis.RegexTokenizer;
import chnu.AutomataTheory.LexAnalysis.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static chnu.AutomataTheory.SyntAnalysis.NonTerminal.*;
import static chnu.AutomataTheory.SyntAnalysis.ArithmeticTokenType.*;

// Parser implementation for context-free LL(1) grammars
public class LL_Parser {

    private final Map<NonTerminal, Map<ITokenType, Production>> parsingTable;

    private final NonTerminal[] nonTerminals;

    // Terminals
    private final ITokenType[] tokenTypes;

    // Production rules
    private final List<Production> rules;

    private final NonTerminal start;

    // TODO: Algorithm to construct a LL(1) parsing table from a Grammar { N, T, P, S }
    public LL_Parser() {

        nonTerminals = NonTerminal.values();

        tokenTypes = ArithmeticTokenType.values();

        rules = new ArrayList<>(Arrays.asList(
                new Production(E, Arrays.asList(T, E2)), // 0. <E> → <T><E2>
                new Production(E2, Arrays.asList(PLUS, T, E2)), // 1. <E2> → +<T><E2>
                new Production(E2, new ArrayList<>()), // 2. <E2> → ε
                new Production(T, Arrays.asList(F, T2)), // 3. <T> → <F><T2>
                new Production(T2, Arrays.asList(ASTERISK, F, T2)),
                new Production(T2, new ArrayList<>()),
                new Production(F, Arrays.asList(LEFT_BRACE, E, RIGHT_BRACE)),
                new Production(F, Collections.singletonList(ID))
        ));

        start = E;

        // { Non-terminal & Input token } pairs
        parsingTable = Stream.of(new Object[][] {
                {
                    // Transitions for <E>
                    E, new Object[][] {
                        { ID, rules.get(0) }, // { <E> & ID }: <E> → <T><E2>
                        { LEFT_BRACE, rules.get(0) } // { <E> & LEFT_BRACE }: <E> → <T><E2>
                    }
                },
                {
                    // Transitions for <E2>
                    E2, new Object[][] {
                        { PLUS, rules.get(1) }, // { <E2> & PLUS }: <E2> → +<T><E2>
                        { RIGHT_BRACE, rules.get(2) }, // { <E2> & RIGHT_BRACE }: <E2> → ε
                        { EOS, rules.get(2) } // { <E2> & EOS }: <E2> → ε
                    }
                },
                {
                    T, new Object[][] {
                        { ID, rules.get(3) },
                        { LEFT_BRACE, rules.get(3) }
                    }
                },
                {
                    T2, new Object[][] {
                        { PLUS, rules.get(5) },
                        { ASTERISK, rules.get(4) },
                        { RIGHT_BRACE, rules.get(5) },
                        { EOS, rules.get(5) }
                    }
                },
                {
                    F, new Object[][] {
                        { ID, rules.get(7) },
                        { LEFT_BRACE, rules.get(6) }
                    }
                }
        }).collect(Collectors.toMap(
                data -> (NonTerminal)data[0],
                data -> Stream.of((Object[][])data[1]).collect(Collectors.toMap(
                        token_rule -> (ArithmeticTokenType)token_rule[0],
                        token_rule -> (Production)token_rule[1]))
                ));
    }

    public boolean parse(String input) {

        RegexTokenizer tokenizer = new RegexTokenizer(input + "$", tokenTypes);
        Token token = tokenizer.nextElement();

        Stack<Object> symbolStack = new Stack<>();
        symbolStack.push(EOS); // $
        symbolStack.push(start); // <E>

        String displayedInput = input + "$";
        System.out.printf("%-25s %-20s %n", "Stack", "Input");

        while (!symbolStack.isEmpty()) {
            printStack(symbolStack, token, displayedInput); // Console output
            // If token on the top == input token (terminal)
            if (symbolStack.peek() == token.getType()) {
                displayedInput = displayedInput.substring(token.getText().length()); // Displayed Input String
                symbolStack.pop();
                if (tokenizer.hasMoreElements()) {
                    token = tokenizer.nextElement();
                }
            } else {
                // If token on the top != input token
                if (symbolStack.peek().getClass() == token.getType().getClass()) {
                    return false;
                }
                NonTerminal nonTerminal = (NonTerminal)symbolStack.peek();
                Production rule = parsingTable.get(nonTerminal).get(token.getType());
                if (rule != null) {
                    symbolStack.pop();
                    List<Object> rightSide = rule.getRightSide();
                    for (int i = rightSide.size()-1; i >= 0; i--) {
                        symbolStack.push(rightSide.get(i));
                    }
                } else {
                    // TODO: Syntax Error handler
                    return false;
                }
            }
        }
        return true;
    }

    void printStack(Stack<Object> symbolStack, Token token, String displayedInput) {
        StringBuilder stackString = new StringBuilder();
        for (Object symbol : symbolStack) {
            if (symbol.getClass() == NonTerminal.class) {
                stackString.append("<").append(symbol.toString()).append(">");
            } else {
                if (symbol == ID) {
                    stackString.append(token.getText());
                }
                else {
                    stackString.append(((ITokenType)symbol).getRegex().replace("\\", ""));
                }
            }
        }
        System.out.printf("%-25s %-20s %n", stackString.toString(), displayedInput);
    }

    public static void main(String[] args) {
        LL_Parser parser = new LL_Parser();

        System.out.print("Input string: ");
        String input = new Scanner(System.in).nextLine();

        if (parser.parse(input)) {
            System.out.println("Parsing is successful.");
        } else {
            System.err.println("Parsing failed.");
        }
    }
}
