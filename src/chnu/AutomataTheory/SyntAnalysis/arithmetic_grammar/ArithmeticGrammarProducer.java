package chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.SyntAnalysis.NonTerminal;
import chnu.AutomataTheory.SyntAnalysis.GrammarProducer;
import chnu.AutomataTheory.SyntAnalysis.Production;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar.ArithmeticNonTerminal.*;
import static chnu.AutomataTheory.SyntAnalysis.arithmetic_grammar.ArithmeticTokenType.*;

public class ArithmeticGrammarProducer implements GrammarProducer {

    private static final List<Production> rules;

    private static final Map<NonTerminal, Map<ITokenType, Production>> parsingTable;

    public static final Map<ITokenType, Integer> operatorsPrecedenceMap;

    static {
        rules = new ArrayList<>(Arrays.asList(

                new Production(E, Arrays.asList(T, E2)), // 0. <E> → <T><E2>
                new Production(E2, Arrays.asList(PLUS, T, E2)), // 1. <E2> → +<T><E2>
                new Production(E2, new ArrayList<>()), // 2. <E2> → ε
                new Production(T, Arrays.asList(F, T2)), // 3. <T> → <F><T2>
                new Production(T2, Arrays.asList(ASTERISK, F, T2)),
                new Production(T2, new ArrayList<>()),
                new Production(F, Arrays.asList(LEFT_PARENTHESIS, E, RIGHT_PARENTHESIS)),
                new Production(F, Collections.singletonList(ID))
        ));

        parsingTable = Stream.of(new Object[][] {
                {
                        // Transitions for <E>
                        E, new Object[][] {
                        { ID, rules.get(0) }, // { <E> & ID }: <E> → <T><E2>
                        {LEFT_PARENTHESIS, rules.get(0) } // { <E> & LEFT_BRACE }: <E> → <T><E2>
                }
                },
                {
                        // Transitions for <E2>
                        E2, new Object[][] {
                        { PLUS, rules.get(1) }, // { <E2> & PLUS }: <E2> → +<T><E2>
                        {RIGHT_PARENTHESIS, rules.get(2) }, // { <E2> & RIGHT_BRACE }: <E2> → ε
                        { EOS, rules.get(2) } // { <E2> & EOS }: <E2> → ε
                }
                },
                {
                        T, new Object[][] {
                        { ID, rules.get(3) },
                        {LEFT_PARENTHESIS, rules.get(3) }
                }
                },
                {
                        T2, new Object[][] {
                        { PLUS, rules.get(5) },
                        { ASTERISK, rules.get(4) },
                        {RIGHT_PARENTHESIS, rules.get(5) },
                        { EOS, rules.get(5) }
                }
                },
                {
                        F, new Object[][] {
                        { ID, rules.get(7) },
                        {LEFT_PARENTHESIS, rules.get(6) }
                }
                }
        }).collect(Collectors.toMap(
                data -> (NonTerminal)data[0],
                data -> Stream.of((Object[][])data[1]).collect(Collectors.toMap(
                        token_rule -> (ITokenType) token_rule[0],
                        token_rule -> (Production)token_rule[1]))
        ));

        operatorsPrecedenceMap = Stream.of(new Object[][]{
                {PLUS, 10},
                {ASTERISK, 20}
        }).collect(Collectors.toMap(
                data -> (ITokenType)data[0],
                data -> (Integer)data[1])
        );
    }

    @Override
    public Map<NonTerminal, Map<ITokenType, Production>> getParsingTable() {
        return parsingTable;
    }

    @Override
    public List<Production> getRules() {
        return rules;
    }

    @Override
    public NonTerminal getStart() {
        return E;
    }

    @Override
    public ITokenType getEosTokenType() {
        return EOS;
    }

    @Override
    public ITokenType[] getTokenTypes() {
        return ArithmeticTokenType.values();
    }

    @Override
    public Map<ITokenType, Integer> getOperatorsPrecedenceMap() {
        return operatorsPrecedenceMap;
    }
}
