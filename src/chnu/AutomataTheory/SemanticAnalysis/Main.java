package chnu.AutomataTheory.SemanticAnalysis;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Input: ");
        String input = new Scanner(System.in).nextLine();
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyzeProgram(input);
    }
}
