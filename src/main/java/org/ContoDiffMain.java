/*
 *
 *  * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * This program is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org;

import org.apache.commons.cli.*;
import org.gomma.diff.DiffComputation;
import org.gomma.diff.DiffExecutor;
import org.io.OntologyReader;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.webdifftool.client.model.DiffEvolutionMapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.DoubleToIntFunction;

public class ContoDiffMain {

    static Options options;

    static {
        options = new Options();
        Option inputFirstOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_A,
                "first ontology", true, "path of the first ontology");
        Option inputSecondOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_B, "path of the second ontology", true,
                "second ontology");
        Option outputFile =  new Option(ConsoleConstants.OUTPUT_FILE, "file with the compact diff representation", true,
                "output file");
        Option diff = new Option(ConsoleConstants.DIFF, "compute Diff", false,
                "compute diff");

        options.addOption(inputFirstOnt);
        options.addOption(inputSecondOnt);
        options.addOption(outputFile);
        options.addOption(diff);
    }

    public static void main(String[] args) throws IOException {
        CommandLine cmd = parseCommand(args);
        OntologyReader reader = new OntologyReader();
        OWLOntology firstOnt = null;
        OWLOntology secontOnt = null;
        FileWriter output = null;
        try {
            System.out.println(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_A));
            System.out.println(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_B));
            File first = new File(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_A));
            File second = new File(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_B));
            output = new FileWriter(cmd.getOptionValue(ConsoleConstants.OUTPUT_FILE));
            firstOnt = reader.loadOntology(first);
            secontOnt = reader.loadOntology(second);
        } catch (NullPointerException e) {

            System.err.println("first or second ontology not found");
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("load ontology "+ firstOnt.getClassesInSignature().size());
        if (cmd.hasOption(ConsoleConstants.DIFF)) {
            DiffExecutor.getSingleton().setupRepository();
            DiffComputation computation = new DiffComputation();
            DiffEvolutionMapping mapping = computation.computeDiff(firstOnt, secontOnt);
            if (output != null){
                output.write(mapping.getFulltextOfCompactDiff());
            }
            output.close();
        }

    }


    private static CommandLine parseCommand(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }
}
