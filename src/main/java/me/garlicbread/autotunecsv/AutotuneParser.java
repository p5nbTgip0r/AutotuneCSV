package me.garlicbread.autotunecsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AutotuneParser {
    private File logFile, csvFile;
    private static final String AUTOTUNE_START_LINE = "=== Running Autotune at ";
    private static final String ISF_LINE = "ISF [mg/dL/U]";
    private static final String CR_LINE = "Carb Ratio[g/U]";

    /**
     * The parser for Autotune log files which will extract our data
     *
     * @param in log file to parse
     * @param out output CSV file
     */
    public AutotuneParser(File in, File out) {
        this.logFile = in;
        this.csvFile = out;
    }

    public void search() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Date", "ISF", "CR"));

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            /*
            0 = seeking
            1 = found recommendations started
            2 = found ISF
            3 = found CR
             */
            int stage = 0;
            String line;
            String startLine = null, isfLine = null, crLine = null;
            while ((line = br.readLine()) != null) {
                if(line.startsWith(AUTOTUNE_START_LINE) && stage == 0) {
                    startLine = line;
                    stage = 1;
                }

                if(line.startsWith(ISF_LINE) && stage == 1) {
                    isfLine = line;
                    stage = 2;
                }

                if(line.startsWith(CR_LINE)&& stage == 2) {
                    crLine = line;
                    stage = 3;
                }

                if(stage == 3) {
                    csvPrinter.printRecord(generateRecord(startLine, isfLine, crLine));
                    stage = 0;
                }
            }
        }

        csvPrinter.flush();
    }

    private List<String> generateRecord(String startLine, String isfLine, String crLine) {
        System.out.println("Generating record for '" + startLine + "', '" + isfLine + "', '" + crLine + "'");
        List<String> data = new ArrayList<>();
        data.add(startLine.split("===")[1].replaceFirst("Running Autotune at", "").trim());
        data.add(isfLine.split("\\|")[2].trim());
        data.add(crLine.split("\\|")[2].trim());
        return data;
    }
}
