import java.util.*;

public class Main {
    private static Map<String, Integer> variables = new HashMap<>();
    private static List<String> lines = new ArrayList<>();
    private static int currentLine = 0;
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Read all lines until we find the variable names line
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            lines.add(line);
        }
        
        // Last two lines contain variable names and values
        int n = lines.size();
        String[] varNames = lines.get(n - 2).split(" ");
        String[] varValues = lines.get(n - 1).split(" ");
        
        // Initialize variables
        for (int i = 0; i < varNames.length; i++) {
            variables.put(varNames[i], Integer.parseInt(varValues[i]));
        }
        
        // Remove last two lines from code
        lines.remove(n - 1);
        lines.remove(n - 2);
        
        // Execute the code
        currentLine = 0;
        execute(0, lines.size());
        
        sc.close();
    }
    
    private static void execute(int start, int end) {
        currentLine = start;
        
        while (currentLine < end) {
            String line = lines.get(currentLine).trim();
            
            if (line.startsWith("for ")) {
                executeFor();
            } else if (line.startsWith("if ")) {
                executeIf();
            } else if (line.startsWith("print ")) {
                executePrint(line);
                currentLine++;
            } else if (line.equals("Yes") || line.equals("No") || line.equals("end")) {
                // These are handled by their parent constructs
                currentLine++;
            } else {
                currentLine++;
            }
        }
    }
    
    private static void executeFor() {
        String line = lines.get(currentLine).trim();
        String[] parts = line.split(" ");
        
        String iterVar = parts[1];
        int startVal = getValue(parts[2]);
        int endVal = getValue(parts[3]);
        
        int forStart = currentLine + 1;
        int forEnd = findMatchingEnd(currentLine);
        
        // Save original value if variable exists
        Integer originalValue = variables.get(iterVar);
        
        // Execute loop
        for (int i = startVal; i <= endVal; i++) {
            variables.put(iterVar, i);
            execute(forStart, forEnd);
        }
        
        // Restore original value or remove if it didn't exist
        if (originalValue != null) {
            variables.put(iterVar, originalValue);
        } else {
            variables.remove(iterVar);
        }
        
        currentLine = forEnd + 1;
    }
    
    private static void executeIf() {
        String line = lines.get(currentLine).trim();
        String condition = line.substring(3).trim();
        
        boolean result = evaluateCondition(condition);
        
        int ifLine = currentLine;
        int yesLine = ifLine + 1;
        int endLine = findMatchingEnd(ifLine);
        
        // Check if there's a No block
        int noLine = findNoBlock(ifLine, endLine);
        
        if (result) {
            // Execute Yes block
            if (noLine != -1) {
                execute(yesLine + 1, noLine);
            } else {
                execute(yesLine + 1, endLine);
            }
        } else {
            // Execute No block if it exists
            if (noLine != -1) {
                execute(noLine + 1, endLine);
            }
        }
        
        currentLine = endLine + 1;
    }
    
    private static void executePrint(String line) {
        String[] parts = line.split(" ", 2);
        String toPrint = parts[1].trim();
        
        if (variables.containsKey(toPrint)) {
            System.out.println(variables.get(toPrint));
        } else {
            System.out.println(toPrint);
        }
    }
    
    private static boolean evaluateCondition(String condition) {
        if (condition.contains("==")) {
            String[] parts = condition.split("==");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left == right;
        } else if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left != right;
        } else if (condition.contains("<=")) {
            String[] parts = condition.split("<=");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left <= right;
        } else if (condition.contains(">=")) {
            String[] parts = condition.split(">=");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left >= right;
        } else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left < right;
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            int left = getValue(parts[0].trim());
            int right = getValue(parts[1].trim());
            return left > right;
        }
        return false;
    }
    
    private static int getValue(String token) {
        token = token.trim();
        if (variables.containsKey(token)) {
            return variables.get(token);
        }
        return Integer.parseInt(token);
    }
    
    private static int findMatchingEnd(int startLine) {
        int depth = 1;
        int i = startLine + 1;
        
        while (i < lines.size() && depth > 0) {
            String line = lines.get(i).trim();
            
            if (line.startsWith("for ") || line.startsWith("if ")) {
                depth++;
            } else if (line.equals("end")) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
            i++;
        }
        
        return i - 1;
    }
    
    private static int findNoBlock(int ifLine, int endLine) {
        int depth = 0;
        
        for (int i = ifLine + 1; i < endLine; i++) {
            String line = lines.get(i).trim();
            
            if (line.startsWith("for ") || line.startsWith("if ")) {
                depth++;
            } else if (line.equals("end")) {
                depth--;
            } else if (line.equals("No") && depth == 0) {
                return i;
            }
        }
        
        return -1;
    }
}
