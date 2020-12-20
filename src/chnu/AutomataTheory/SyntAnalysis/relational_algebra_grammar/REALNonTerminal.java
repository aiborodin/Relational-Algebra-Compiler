package chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar;

import chnu.AutomataTheory.SyntAnalysis.NonTerminal;

public enum REALNonTerminal implements NonTerminal {
    ProgramRA,
    SeqOperatorsRA,
    OperRA,
    OperRA2,
    OperDBManagement,
    OperDBManip,
    DeleteTable,
    UpdateTable,
    SetList,
    SetList2,
    Value,
    WhereClause,
    Condition,
    Expression_RA,
    Operator,
    UnaryOperator, // 1-місний оператор
    BinaryOperator, // 2-місний оператор
    JoinOperator,
    JoinFactor,
    AttributeJoinFactor,
    BooleanTerm,
    BooleanTerm2,
    BooleanFactor,
    BooleanFactor2,
    Predicate,
    Rel_Predicate,
}
