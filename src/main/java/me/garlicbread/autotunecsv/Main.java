package me.garlicbread.autotunecsv;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        Option input = new Option("in", "input", true, "autotune log file");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("out", "output", true, "output CSV file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("autotunecsv", options);
            System.exit(1);
            return;
        }

        try {
            new AutotuneParser(new File(cmd.getOptionValue("in")), new File(cmd.getOptionValue("out"))).search();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
