package chnu.AutomataTheory.SyntAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.LexAnalysis.RegexTokenizer;
import chnu.AutomataTheory.LexAnalysis.SymbolTable;
import chnu.AutomataTheory.LexAnalysis.Token;

import chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar.ArithmeticGrammarProducer;
import chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar.ArithmeticTokenType;

import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.*;

import java.util.*;

// Parser implementation for context-free LL(1) grammars
public class LL_Parser {

    private RegexTokenizer tokenizer;

    private final GrammarProducer grammarProducer;

    private final Map<NonTerminal, Map<ITokenType, Production>> parsingTable;

    // Terminals (Tokens)
    private final ITokenType[] tokenTypes;

    private final NonTerminal startSymbol;

    private final Map<ITokenType, Integer> operatorsPrecedenceMap;

    // TODO: Algorithm to construct a LL(1) parsing table from a Grammar { N, T, P, S }
    public LL_Parser() {

        this.grammarProducer = new ArithmeticGrammarProducer();

        tokenTypes = grammarProducer.getTokenTypes();

        startSymbol = grammarProducer.getStart();

        parsingTable = grammarProducer.getParsingTable();

        operatorsPrecedenceMap = grammarProducer.getOperatorsPrecedenceMap();
    }

    public boolean parse(String input) {
        Token token;
        try {
            this.tokenizer = new RegexTokenizer(input + "$", tokenTypes);
            token = tokenizer.nextElement();
        } catch (IllegalStateException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        Stack<Object> parserStack = new Stack<>();
        parserStack.push(grammarProducer.getEosTokenType()); // $
        parserStack.push(startSymbol); // Starting NonTerminal

        List<Token> postfix_expr = new ArrayList<>();
        Stack<Token> operators_stack = new Stack<>();

        String displayedInput = input + "$";
        //System.out.printf("%-100s %-40s %n", "Stack", "Input");

        while (!parserStack.isEmpty()) {
            // Console output
            //printStack(parserStack, token, displayedInput);
            // If symbol on the top is terminal (ITokenType)
            if (parserStack.peek() instanceof ITokenType) {
                // If token type on the top == input token type
                if (parserStack.peek() == token.getType()) {
                    // Update Input String - Token matched
                    displayedInput = displayedInput.substring(token.getText().length());
                    updatePostfixExpr(postfix_expr, operators_stack, token);
                    parserStack.pop();
                    if (tokenizer.hasMoreElements()) {
                        token = tokenizer.nextElement();
                        while ((token.getType() == SPACE || token.getType() == COMMENT) && tokenizer.hasMoreElements()) {
                            displayedInput = displayedInput.substring(token.getText().length());
                            token = tokenizer.nextElement();
                        }
                    }
                } else {
                    // token type on the top != input token type
                    return false;
                }
            } else {
                // Symbol on the top is NonTerminal
                NonTerminal nonTerminal = (NonTerminal)parserStack.peek();
                Production rule = parsingTable.get(nonTerminal).get(token.getType());
                if (rule != null) {
                    parserStack.pop();
                    List<Object> rightSide = rule.getRightSide();
                    for (int i = rightSide.size()-1; i >= 0; i--) {
                        parserStack.push(rightSide.get(i));
                    }
                } else {
                    // TODO: Syntax Error handler
                    return false;
                }
            }
        }
        postfix_expr.forEach(token1 -> System.out.print(token1.getText()));
        System.out.println();
        return true;
    }

    private void updatePostfixExpr(List<Token> postfix_expr, Stack<Token> operators_stack, Token input) {
        ITokenType inputType = input.getType();
        if (inputType == ArithmeticTokenType.ID) {
            postfix_expr.add(input);
        } else if (inputType == ArithmeticTokenType.LEFT_PARENTHESIS){
            operators_stack.push(input);
        } else if (inputType == ArithmeticTokenType.RIGHT_PARENTHESIS){
            while (operators_stack.peek().getType() != ArithmeticTokenType.LEFT_PARENTHESIS) {
                postfix_expr.add(operators_stack.pop());
            }
            operators_stack.pop();
        } else if (operatorsPrecedenceMap.containsKey(inputType)) { // If Token is Operator
            Integer inputPrecedence = operatorsPrecedenceMap.get(inputType);
            while (!operators_stack.isEmpty()
                    && operators_stack.peek().getType() != ArithmeticTokenType.LEFT_PARENTHESIS
                    && operatorsPrecedenceMap.get(operators_stack.peek().getType()) >= inputPrecedence) {
                postfix_expr.add(operators_stack.pop());
            }
            operators_stack.push(input);
        } else if (inputType == ArithmeticTokenType.EOS) {
            while (!operators_stack.empty()) {
                postfix_expr.add(operators_stack.pop());
            }
        }
    }

    private void printStack(Stack<Object> parserStack, Token token, String displayedInput) {
        StringBuilder stackString = new StringBuilder();
        for (Object symbol : parserStack) {
            if (symbol instanceof NonTerminal) {
                stackString.append("<").append(symbol.toString()).append(">");
            } else {
                if (symbol == ID) {
                    stackString.append("ID");
                } else if (symbol == REL_OP || symbol == NUMBER ||
                        symbol == NULL || symbol == AND || symbol == OR || symbol == LEFT_JOIN ||
                        symbol == ON || symbol == MINUS || symbol == STRING){
                    stackString.append(token.getText());
                }
                else {
                    stackString.append(((ITokenType)symbol).getRegex().replace("\\b", "").replace("\\", ""));
                }
            }
        }
        System.out.printf("%-100s %-40s %n", stackString.toString(), displayedInput);
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

    public SymbolTable getSymbolTable() {
        return tokenizer.getSymbolTable();
    }
}
