package chnu.AutomataTheory.SemanticAnalysis;

import chnu.AutomataTheory.LexAnalysis.ITokenType;
import chnu.AutomataTheory.LexAnalysis.SymbolTable;
import chnu.AutomataTheory.LexAnalysis.Token;
import chnu.AutomataTheory.SyntAnalysis.LL_Parser;
import chnu.AutomataTheory.SyntAnalysis.relational_algebra_grammar.REALTokenType;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {

    private final LL_Parser parser;

    private final List<Error> errors;

    private final DataBase db;

    public SemanticAnalyzer() {
        this.parser = new LL_Parser();
        this.errors = new ArrayList<>();
        this.db = new DataBase();
    }

    public void analyzeProgram(String input) {
        if (!parser.parse(input)) {
            System.err.println("Parsing failed.");
            return;
        }
        SymbolTable symbolTable = parser.getSymbolTable();
        ITokenType tokenType;

        for (int i = 1; i < symbolTable.size(); i++) {
            tokenType = symbolTable.getToken(i).getType();
            // Check WHERE semantics
            if (tokenType == REALTokenType.WHERE) {
                String tableName;
                if (symbolTable.getToken(i - 1).getType() == REALTokenType.ID) {
                    tableName = symbolTable.getToken(i - 1).getText();
                    if (!db.hasTable(tableName)) {
                        errors.add(
                                new Error("There is no table '" + tableName + "' in database.")
                        );
                    } else {
                        int j = i + 1;
                        Token nextToken = symbolTable.getToken(j);
                        while (nextToken.getType() != REALTokenType.MINUS
                                && nextToken.getType() != REALTokenType.LEFT_JOIN
                                && nextToken.getType() != REALTokenType.WHERE
                                && nextToken.getType() != REALTokenType.ON
                                && nextToken.getType() != REALTokenType.SEMICOLON
                                && nextToken.getType() != REALTokenType.END_RA
                                && j < symbolTable.size()
                        ) {
                            String columnName;
                            if (nextToken.getType() == REALTokenType.ID) {
                                columnName = nextToken.getText();
                                if (!db.checkColumn(tableName, columnName)) {
                                    errors.add(
                                            new Error("There is no column '" + columnName + "' in table '" + tableName + "'.")
                                    );
                                }
                            }
                            nextToken = symbolTable.getToken(++j);
                        }
                    }
                }
                // Check MINUS semantics
            } else if (tokenType == REALTokenType.MINUS) {
                if (symbolTable.getToken(i - 1).getType() == REALTokenType.ID
                        && symbolTable.getToken(i + 1).getType() == REALTokenType.ID) {
                    String leftTable = symbolTable.getToken(i - 1).getText();
                    String rightTable = symbolTable.getToken(i + 1).getText();
                    if (!db.hasTable(leftTable)) {
                        errors.add(
                                new Error("There is no table '" + leftTable + "' in database.")
                        );
                    } else if (!db.hasTable(rightTable)) {
                        errors.add(
                                new Error("There is no table '" + rightTable + "' in database.")
                        );
                    } else if (!db.checkMinusCompatibility(leftTable, rightTable)) {
                        errors.add(
                                new Error("Tables '" + leftTable + "' and '" + rightTable +
                                        "' are not compatible for minus operation.")
                        );
                    }

                }
                // Check LEFT JOIN semantics
            } else if (tokenType == REALTokenType.LEFT_JOIN) {
                if (symbolTable.getToken(i - 1).getType() == REALTokenType.ID
                        && symbolTable.getToken(i + 1).getType() == REALTokenType.ID) {
                    String leftTable = symbolTable.getToken(i - 1).getText();
                    String rightTable = symbolTable.getToken(i + 1).getText();
                    if (!db.hasTable(leftTable)) {
                        errors.add(
                                new Error("There is no table '" + leftTable + "' in database.")
                        );
                    } else if (!db.hasTable(rightTable)) {
                        errors.add(
                                new Error("There is no table '" + rightTable + "' in database.")
                        );
                    } else {
                        Token joinToken = symbolTable.getToken(i + 3);
                        Token nextToken = symbolTable.getToken(i + 4);
                        if (nextToken.getType() == REALTokenType.DOT) {
                            String leftColumn = symbolTable.getToken(i + 5).getText();
                            String rightColumn = symbolTable.getToken(i + 9).getText();
                            String errorMessage = db.checkJoinCompatibility(leftTable, rightTable, leftColumn, rightColumn);
                            if (errorMessage != null) {
                                errors.add(new Error(errorMessage));
                            }
                        } else {
                            String leftColumn, rightColumn;
                            leftColumn = rightColumn = joinToken.getText();
                            String errorMessage = db.checkJoinCompatibility(leftTable, rightTable, leftColumn, rightColumn);
                            if (errorMessage != null) {
                                errors.add(new Error(errorMessage));
                            }
                        }
                    }
                }
                // Check DROP TABLE semantics
            } else if (tokenType == REALTokenType.DROP_TABLE) {
                String tableName;
                if (symbolTable.getToken(i + 1).getType() == REALTokenType.ID) {
                    tableName = symbolTable.getToken(i + 1).getText();
                    if (!db.hasTable(tableName)) {
                        errors.add(
                                new Error("There is no table '" + tableName + "' in database.")
                        );
                    }
                }
            } else if (tokenType == REALTokenType.UPDATE) {
                String tableName;
                if (symbolTable.getToken(i + 1).getType() == REALTokenType.ID) {
                    tableName = symbolTable.getToken(i + 1).getText();
                    if (!db.hasTable(tableName)) {
                        errors.add(
                                new Error("There is no table '" + tableName + "' in database.")
                        );
                    } else {
                        int j = i + 3;
                        Token nextToken = symbolTable.getToken(j);
                        while (nextToken.getType() != REALTokenType.SEMICOLON && nextToken.getType() != REALTokenType.END_RA
                                && j < symbolTable.size()
                        ) {
                            String columnName;
                            if (nextToken.getType() == REALTokenType.ID) {
                                columnName = nextToken.getText();
                                if (!db.checkColumn(tableName, columnName)) {
                                    errors.add(
                                            new Error("There is no column '" + columnName + "' in table '" + tableName + "'.")
                                    );
                                }
                            }
                            nextToken = symbolTable.getToken(++j);
                        }
                    }
                }
            }
        }
        if (errors.isEmpty()) {
            System.out.println("Program is correct.");
        } else {
            errors.forEach(error -> System.err.println(error.getMessage()));
        }
    }
}
