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
import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.EOS;
import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.ID;
import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.LEFT_BRACE;
import static chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType.RIGHT_BRACE;

public class ArithmeticGrammarProducer implements GrammarProducer {

    private final List<Production> rules = new ArrayList<>(Arrays.asList(

            new Production(E, Arrays.asList(T, E2)), // 0. <E> → <T><E2>
            new Production(E2, Arrays.asList(PLUS, T, E2)), // 1. <E2> → +<T><E2>
            new Production(E2, new ArrayList<>()), // 2. <E2> → ε
            new Production(T, Arrays.asList(F, T2)), // 3. <T> → <F><T2>
            new Production(T2, Arrays.asList(ASTERISK, F, T2)),
            new Production(T2, new ArrayList<>()),
            new Production(F, Arrays.asList(LEFT_BRACE, E, RIGHT_BRACE)),
            new Production(F, Collections.singletonList(ID))
    ));

    private Map<NonTerminal, Map<ITokenType, Production>> parsingTable;

    @Override
    public Map<NonTerminal, Map<ITokenType, Production>> getParsingTable() {

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
                        token_rule -> (ITokenType) token_rule[0],
                        token_rule -> (Production)token_rule[1]))
        ));

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
}
