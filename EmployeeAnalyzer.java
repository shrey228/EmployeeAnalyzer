import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class EmployeeShift {
    String positionId;
    Date timeIn;
    Date timeOut;
    Double shiftHours;

    public EmployeeShift(String positionId, Date timeIn, Date timeOut) {
        this.positionId = positionId;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        if (timeIn != null && timeOut != null) {
            this.shiftHours = (double) (timeOut.getTime() - timeIn.getTime()) / (1000 * 60 * 60);
        } else {
            this.shiftHours = null; // Indicate that shiftHours is not applicable for one or both null dates
        }
    }
}

public class EmployeeAnalyzer {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    public static Date parseTime(String timeStr) throws ParseException {
        if (timeStr.isEmpty()) {
            return null;
        }
        return dateFormat.parse(timeStr);
    }

    // Method to write data to console
    private static void writeDataToConsole(Map<String, List<EmployeeShift>> employeeData) {
        System.out.println("Employee Data:");
        for (Map.Entry<String, List<EmployeeShift>> entry : employeeData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Task 1: Employees who worked for 7 consecutive days
        System.out.println("Employees who worked for 7 consecutive days:");
        for (Map.Entry<String, List<EmployeeShift>> entry : employeeData.entrySet()) {
            Set<Date> uniqueDates = new HashSet<>();
            for (EmployeeShift shift : entry.getValue()) {
                uniqueDates.add(shift.timeIn);
            }

            if (uniqueDates.size() >= 7) {
                System.out.println(entry.getKey() + " (" + entry.getValue().get(0).positionId + ")");
            }
        }

        // Task 2: Employees with less than 10 hours between shifts but greater than 1 hour
        System.out.println("\nEmployees with less than 10 hours between shifts but greater than 1 hour:");
        for (Map.Entry<String, List<EmployeeShift>> entry : employeeData.entrySet()) {
            List<EmployeeShift> shifts = entry.getValue();
            for (int i = 1; i < shifts.size(); i++) {
                long hoursBetween = 0;
                if (shifts.get(i).timeIn != null && shifts.get(i - 1).timeOut != null) {
                    hoursBetween = (shifts.get(i).timeIn.getTime() - shifts.get(i - 1).timeOut.getTime()) / (1000 * 60 * 60);
                }

                if (hoursBetween > 1 && hoursBetween < 10) {
                    System.out.println(entry.getKey() + " (" + shifts.get(i).positionId + ")");
                    break;  // Only need to print once for each employee
                }
            }
        }

        // Task 3: Employees who worked for more than 14 hours in a single shift
        System.out.println("\nEmployees who worked for more than 14 hours in a single shift:");
        for (Map.Entry<String, List<EmployeeShift>> entry : employeeData.entrySet()) {
            for (EmployeeShift shift : entry.getValue()) {
                if (shift.timeIn != null && shift.timeOut != null && shift.shiftHours != null && shift.shiftHours > 14) {
                    System.out.println(entry.getKey() + " (" + shift.positionId + ")");
                    break;  // Only need to print once for each employee
                }
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\kundu\\OneDrive\\Documents\\Desktop\\Employee1\\input.csv.csv";  // Provide the actual file path
        String outputFilePath = "C:\\Users\\kundu\\OneDrive\\Documents\\Desktop\\Employee1\\output.txt";  // Provide the desired output file path

        // Map to store employee data
        Map<String, List<EmployeeShift>> employeeData = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();  // Skip the header line

            while ((line = br.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length >= 8) {
                        String employeeName = fields[7].trim();
                        String positionId = fields[0].trim();
                        Date timeIn = parseTime(fields[2].trim());
                        Date timeOut = parseTime(fields[3].trim());

                        employeeData.computeIfAbsent(employeeName, k -> new ArrayList<>())
                                .add(new EmployeeShift(positionId, timeIn, timeOut));
                    }
                } catch (ParseException e) {
                    System.out.println("Error parsing date in line: " + line);
                    e.printStackTrace();
                }
            }

            // Check if employeeData is empty
            if (employeeData.isEmpty()) {
                System.out.println("No data found in the input file.");
            } else {
                // Writing the console output to a file (Task 4)
                try (FileWriter writer = new FileWriter(outputFilePath)) {
                    writeDataToConsole(employeeData);  // Reuse the method for console output

                    // Task 4: Write data to the file
                    writer.write("Employee Data:\n");
                    for (Map.Entry<String, List<EmployeeShift>> entry : employeeData.entrySet()) {
                        writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                    }

                    writeDataToConsole(employeeData);  // Reuse the method for console output
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
