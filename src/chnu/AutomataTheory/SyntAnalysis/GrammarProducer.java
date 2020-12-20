package chnu.AutomataTheory.SyntAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.SyntAnalysis.NonTerminal;
import chnu.AutomataTheory.SyntAnalysis.Production;

import java.util.List;
import java.util.Map;

public interface GrammarProducer {

   Map<NonTerminal, Map<ITokenType, Production>> getParsingTable();

   List<Production> getRules();

   NonTerminal getStart();

}
