import java.util.*;

public class Main {
    private static Map<String, Integer> variables = new HashMap<>();
    private static List<String> code = new ArrayList<>();
    private static int currentLine = 0;
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        
        // Read all input
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            lines.add(line);
        }
        
        // Parse input
        int n = lines.size();
        String[] varNames = lines.get(n - 2).split(" ");
        String[] varValues = lines.get(n - 1).split(" ");
        
        // Initialize variables
        for (int i = 0; i < varNames.length; i++) {
            variables.put(varNames[i], Integer.parseInt(varValues[i]));
        }
        
        // Store code lines
        for (int i = 0; i < n - 2; i++) {
            code.add(lines.get(i));
        }
        
        // Execute code
        currentLine = 0;
        execute(0, code.size());
        
        sc.close();
    }
    
    private static void execute(int start, int end) {
        currentLine = start;
        
        while (currentLine < end) {
            String line = code.get(currentLine).trim();
            
            if (line.startsWith("if ")) {
                handleIf();
            } else if (line.startsWith("for ")) {
                handleFor();
            } else if (line.startsWith("print ")) {
                handlePrint(line);
                currentLine++;
            } else {
                currentLine++;
            }
        }
    }
    
    private static void handleIf() {
        String line = code.get(currentLine).trim();
        String condition = line.substring(3).trim();
        boolean result = evaluateCondition(condition);
        
        currentLine++; // Move past "if" line
        
        // Find Yes, No (if exists), and end
        int yesLine = currentLine;
        int noLine = -1;
        int endLine = findMatchingEnd(currentLine - 1);
        
        // Find "No" if it exists
        for (int i = yesLine; i < endLine; i++) {
            String l = code.get(i).trim();
            if (l.equals("No")) {
                noLine = i;
                break;
            }
        }
        
        if (result) {
            // Execute Yes block
            currentLine++; // Skip "Yes"
            if (noLine != -1) {
                execute(currentLine, noLine);
                currentLine = endLine + 1;
            } else {
                execute(currentLine, endLine);
                currentLine = endLine + 1;
            }
        } else {
            // Execute No block if exists
            if (noLine != -1) {
                currentLine = noLine + 1; // Skip "No"
                execute(currentLine, endLine);
                currentLine = endLine + 1;
            } else {
                currentLine = endLine + 1;
            }
        }
    }
    
    private static void handleFor() {
        String line = code.get(currentLine).trim();
        String[] parts = line.substring(4).trim().split(" ");
        String iterVar = parts[0];
        int start = getValue(parts[1]);
        int end = getValue(parts[2]);
        
        int forStartLine = currentLine;
        int endLine = findMatchingEnd(forStartLine);
        
        // Save original value if variable exists
        Integer originalValue = variables.get(iterVar);
        
        // Execute loop
        for (int i = start; i <= end; i++) {
            variables.put(iterVar, i);
            currentLine = forStartLine + 1;
            execute(currentLine, endLine);
        }
        
        // Restore original value or remove
        if (originalValue != null) {
            variables.put(iterVar, originalValue);
        } else {
            variables.remove(iterVar);
        }
        
        currentLine = endLine + 1;
    }
    
    private static void handlePrint(String line) {
        String value = line.substring(6).trim();
        System.out.println(getValue(value));
    }
    
    private static boolean evaluateCondition(String condition) {
        if (condition.contains("==")) {
            String[] parts = condition.split("==");
            return getValue(parts[0].trim()) == getValue(parts[1].trim());
        } else if (condition.contains("!=")) {
            String[] parts = condition.split("!=");
            return getValue(parts[0].trim()) != getValue(parts[1].trim());
        } else if (condition.contains("<=")) {
            String[] parts = condition.split("<=");
            return getValue(parts[0].trim()) <= getValue(parts[1].trim());
        } else if (condition.contains(">=")) {
            String[] parts = condition.split(">=");
            return getValue(parts[0].trim()) >= getValue(parts[1].trim());
        } else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            return getValue(parts[0].trim()) < getValue(parts[1].trim());
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            return getValue(parts[0].trim()) > getValue(parts[1].trim());
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
        for (int i = startLine + 1; i < code.size(); i++) {
            String line = code.get(i).trim();
            if (line.startsWith("if ") || line.startsWith("for ")) {
                depth++;
            } else if (line.equals("end")) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return code.size();
    }
}
