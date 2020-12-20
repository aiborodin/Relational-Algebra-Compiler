package chnu.AutomataTheory.SemanticAnalysis;

public class Main {
    public static void main(String[] args) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        // beginra t3 := doctors left join hospitals on doctors.hospital_id = hospitals.id endra
        analyzer.analyzeProgram("beginra update doctors set code = 3 where code > 4 endra");
    }
}
