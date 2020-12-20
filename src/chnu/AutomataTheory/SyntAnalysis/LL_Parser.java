package chnu.AutomataTheory.SyntAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.LexAnalysis.RegexTokenizer;
import chnu.AutomataTheory.LexAnalysis.Token;

import chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALNonTerminal.*;
import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.*;

// Parser implementation for context-free LL(1) grammars
public class LL_Parser {

    private final Map<NonTerminal, Map<ITokenType, Production>> parsingTable;

    // Terminals
    private final ITokenType[] tokenTypes;

    // Production rules
    private final List<Production> rules;

    private final NonTerminal start;

    // TODO: Algorithm to construct a LL(1) parsing table from a Grammar { N, T, P, S }
    public LL_Parser() {

        tokenTypes = REALTokenType.values();

        rules = new ArrayList<>(Arrays.asList(
                new Production(ProgramRA, Arrays.asList(BEGIN_RA, SeqOperatorsRA, END_RA)), // 1. <Прогр_РА> → beginRA <ПослідОпeрРА> endRA
                new Production(SeqOperatorsRA, Arrays.asList(OperRA, OperRA2)), // 2. <ПослідОпeрРА> → <ОпeрРА><ОпeрРА2>
                new Production(OperRA2, Arrays.asList(SEMICOLON, OperRA, OperRA2)), // 3. ...
                new Production(OperRA2, new ArrayList<>()),
                new Production(OperRA, Collections.singletonList(OperDBManagement)),
                new Production(OperRA, Collections.singletonList(OperDBManip)),
                new Production(OperDBManagement, Collections.singletonList(DeleteTable)),
                new Production(DeleteTable, Arrays.asList(DROP_TABLE, ID)),
                new Production(OperDBManagement, Collections.singletonList(UpdateTable)),
                new Production(UpdateTable, Arrays.asList(UPDATE, ID, SET, SetList, WhereClause)),
                new Production(SetList, Arrays.asList( ID, EQUALS, Value, SetList2)),
                new Production(SetList2, Arrays.asList( COMMA, ID, EQUALS, Value, SetList2)),
                new Production(SetList2, new ArrayList<>()),
                new Production(WhereClause, Arrays.asList(WHERE, Condition)),
                new Production(WhereClause, new ArrayList<>()),
                new Production(OperDBManip, Arrays.asList(ID, ASSIGN, Expression_RA)),
                new Production(Expression_RA, Arrays.asList(LEFT_BRACE, Expression_RA, RIGHT_BRACE, Operator)),
                new Production(Expression_RA, Arrays.asList(ID, Operator)),
                new Production(Operator, Arrays.asList(UnaryOperator, Operator)),
                new Production(Operator, Arrays.asList(BinaryOperator, Expression_RA, Operator)),
                new Production(Operator, Arrays.asList(JoinOperator, Expression_RA, ON, JoinFactor, Operator)),
                new Production(Operator, new ArrayList<>()),
                new Production(JoinFactor, Arrays.asList(ID, AttributeJoinFactor)),
                new Production(AttributeJoinFactor, Arrays.asList(DOT, ID, EQUALS, ID, DOT, ID)),
                new Production(AttributeJoinFactor, Collections.emptyList()),
                new Production(UnaryOperator, Arrays.asList(WHERE, Condition)),
                new Production(Condition, Arrays.asList(BooleanTerm, BooleanTerm2)),
                new Production(BooleanTerm2, Arrays.asList(OR, BooleanTerm, BooleanTerm2)),
                new Production(BooleanTerm2, Arrays.asList()),
                new Production(BooleanTerm, Arrays.asList(BooleanFactor, BooleanFactor2)),
                new Production(BooleanFactor2, Arrays.asList(AND, BooleanFactor, BooleanFactor2)), // 31
                new Production(BooleanFactor2, Arrays.asList()),
                new Production(BooleanFactor, Arrays.asList(Predicate)),
                new Production(BooleanFactor, Arrays.asList(LEFT_BRACE, Condition, RIGHT_BRACE)), // 34
                new Production(Predicate, Arrays.asList(ID, Rel_Predicate)), // 35
                new Production(Rel_Predicate, Arrays.asList(REL_OP, Value)), // 36
                new Production(Rel_Predicate, new ArrayList<>()), // 37
                new Production(Value, Arrays.asList(ID)),
                new Production(Value, Arrays.asList(NUMBER)),
                new Production(Value, Arrays.asList(STRING)),
                new Production(Value, Arrays.asList(NULL)),
                new Production(BinaryOperator, Arrays.asList(MINUS)),
                new Production(JoinOperator, Arrays.asList(LEFT_JOIN))
        ));

        start = ProgramRA;

        // { Non-terminal & Input token } pairs
        parsingTable = Stream.of(new Object[][] {
                {
                    // Transitions for <ProgramRA>
                    ProgramRA, new Object[][] {
                        { BEGIN_RA, rules.get(0) } // { <ProgramRA> & BeginRA }: <ProgramRA> → beginRA <SeqOperatorsRA> endRA
                    }
                },
                {
                    // Transitions for <SeqOperatorsRA>
                    SeqOperatorsRA, new Object[][] {
                        { ID, rules.get(1) }, // { <SeqOperatorsRA> & ID }: <SeqOperatorsRA> → <OperRA><OperRA2>
                        { DROP_TABLE, rules.get(1) },
                        { UPDATE, rules.get(1) }
                    }
                },
                {
                    OperRA, new Object[][] {
                        { ID, rules.get(5) },
                        { DROP_TABLE, rules.get(4) },
                        { UPDATE, rules.get(4) },
                    }
                },
                {
                    OperRA2, new Object[][] {
                        { END_RA, rules.get(3) },
                        { SEMICOLON, rules.get(2) },
                    }
                },
                {
                    OperDBManagement, new Object[][] {
                        { DROP_TABLE, rules.get(6) },
                        { UPDATE, rules.get(8) },
                    }
                },
                {
                    OperDBManip, new Object[][] {
                        { ID, rules.get(15) }
                    }
                },
                {
                    DeleteTable, new Object[][] {
                        { DROP_TABLE, rules.get(7) }
                    }
                },
                {
                    UpdateTable, new Object[][] {
                        { UPDATE, rules.get(9) }
                    }
                },
                {
                    SetList, new Object[][] {
                        { ID, rules.get(10) }
                    }
                },
                {
                    SetList2, new Object[][] {
                        { END_RA, rules.get(12) },
                        { SEMICOLON, rules.get(12) },
                        { WHERE, rules.get(12) },
                        { COMMA, rules.get(11) },
                    }
                },
                {
                    WhereClause, new Object[][] {
                        { END_RA, rules.get(14) },
                        { SEMICOLON, rules.get(14) },
                        { WHERE, rules.get(13) }
                    }
                },
                {
                    Condition, new Object[][] {
                        { ID, rules.get(26) },
                        { LEFT_BRACE, rules.get(26) }
                    }
                },
                {
                    Expression_RA, new Object[][] {
                        { ID, rules.get(17) },
                        { LEFT_BRACE, rules.get(16) }
                    }
                },
                {
                    Operator, new Object[][] {
                        { END_RA, rules.get(21) },
                        { SEMICOLON, rules.get(21) },
                        { RIGHT_BRACE, rules.get(21) },
                        { ON, rules.get(21) },
                        { WHERE, rules.get(18) },
                        { LEFT_JOIN, rules.get(20) },
                        { MINUS, rules.get(19) },
                    }
                },
                {
                    UnaryOperator, new Object[][] {
                        { WHERE, rules.get(25) }
                    }
                },
                {
                    BinaryOperator, new Object[][] {
                        { MINUS, rules.get(41) }
                    }
                },
                {
                    JoinFactor, new Object[][] {
                        { ID, rules.get(22) }
                    }
                },
                {
                    AttributeJoinFactor, new Object[][] {
                        { END_RA, rules.get(24) },
                        { SEMICOLON, rules.get(24) },
                        { WHERE, rules.get(24) },
                        { RIGHT_BRACE, rules.get(24) },
                        { DOT, rules.get(23) },
                        { LEFT_JOIN, rules.get(24) },
                        { ON, rules.get(24) },
                        { MINUS, rules.get(24) }
                    }
                },
                {
                    BooleanTerm, new Object[][] {
                        { ID, rules.get(29) },
                        { LEFT_BRACE, rules.get(29) },
                    }
                },
                {
                    BooleanTerm2, new Object[][] {
                        { END_RA, rules.get(28) },
                        { SEMICOLON, rules.get(28) },
                        { WHERE, rules.get(28) },
                        { RIGHT_BRACE, rules.get(28) },
                        { LEFT_JOIN, rules.get(28) },
                        { ON, rules.get(28) },
                        { MINUS, rules.get(28) },
                        { OR, rules.get(27) },
                    }
                },
                {
                    BooleanFactor, new Object[][] {
                        { ID, rules.get(32) },
                        { LEFT_BRACE, rules.get(33) }
                    }
                },
                {
                    BooleanFactor2, new Object[][] {
                        { END_RA, rules.get(31) },
                        { SEMICOLON, rules.get(31) },
                        { RIGHT_BRACE, rules.get(31) },
                        { LEFT_BRACE, rules.get(31) },
                        { OR, rules.get(31) },
                        { LEFT_JOIN, rules.get(31) },
                        { ON, rules.get(31) },
                        { MINUS, rules.get(31) },
                        { AND, rules.get(30) },
                    }
                },
                {
                    Predicate, new Object[][] {
                        { ID, rules.get(34) }
                    }
                },
                {
                    Rel_Predicate, new Object[][] {
                        { END_RA, rules.get(36) },
                        { SEMICOLON, rules.get(36) },
                        { WHERE, rules.get(36) },
                        { RIGHT_BRACE, rules.get(36) },
                        { AND, rules.get(36) },
                        { OR, rules.get(36) },
                        { LEFT_JOIN, rules.get(36) },
                        { ON, rules.get(36) },
                        { MINUS, rules.get(36) },
                        { REL_OP, rules.get(35) },
                    }
                },
                {
                    Value, new Object[][] {
                        { ID, rules.get(37) },
                        { NUMBER, rules.get(38) },
                        { STRING, rules.get(39) },
                        { NULL, rules.get(40) },
                    }
                },
                {
                    JoinOperator, new Object[][] {
                        { LEFT_JOIN, rules.get(42) }
                    }
                }
        }).collect(Collectors.toMap(
                data -> (NonTerminal)data[0],
                data -> Stream.of((Object[][])data[1]).collect(Collectors.toMap(
                        token_rule -> (ITokenType) token_rule[0],
                        token_rule -> (Production)token_rule[1]))
                ));
    }

    public boolean parse(String input) {
        RegexTokenizer tokenizer;
        Token token;
        try {
            tokenizer = new RegexTokenizer(input + "$", tokenTypes);
            token = tokenizer.nextElement();
        } catch (IllegalStateException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        Stack<Object> symbolStack = new Stack<>();
        symbolStack.push(EOS); // $
        symbolStack.push(start); // <ProgramRA>

        String displayedInput = input + "$";
        System.out.printf("%-100s %-40s %n", "Stack", "Input");

        while (!symbolStack.isEmpty()) {
            // Console output
            printStack(symbolStack, token, displayedInput);
            // If symbol on the top is terminal (ITokenType)
            if (symbolStack.peek() instanceof ITokenType) {
                // If token type on the top == input token type
                if (symbolStack.peek() == token.getType()) {
                    // Update Input String - Token matched
                    displayedInput = displayedInput.substring(token.getText().length());
                    symbolStack.pop();
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
            if (symbol instanceof NonTerminal) {
                stackString.append("<").append(symbol.toString()).append(">");
            } else {
                if (symbol == ID) {
                    stackString.append("ID");
                } else if (symbol == REL_OP || symbol == NUMBER ||
                        symbol == NULL || symbol == AND || symbol == OR || symbol == LEFT_JOIN ||
                        symbol == ON || symbol == MINUS) {
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
}
