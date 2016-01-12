/**
 * Helper class for drug-gene interactions database
 *
 * Copyright Vera-Licona Research Group (C) 2015
 *
 * This software is licensed under the Artistic License 2.0, see the
 * LICENSE file or
 * http://www.opensource.org/licenses/artistic-license-2.0.php for
 * details
 **/

package org.compsysmed.ocsana.internal.util.drugbank;

// Java imports
import java.io.*;
import java.util.*;

// JSON imports
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class InteractionsDatabase {
    private static final String drugBankPath = "/drugbank/drugbank.json";
    private static InteractionsDatabase internalDB;

    public List<Gene> genes = new ArrayList<>();

    public Set<String> interactionTypes = new HashSet<>();

    private InteractionsDatabase () {
        JSONObject drugBankJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(drugBankPath)) {
            drugBankJSON = new JSONObject(new JSONTokener(jsonFileStream));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not find or read DrugBank JSON file");
        }

        Iterator<String> geneNamesIterator = drugBankJSON.keys();
        while (geneNamesIterator.hasNext()) {
            String geneName = geneNamesIterator.next();
            Gene gene = new Gene(geneName);

            JSONObject geneInteractions = drugBankJSON.getJSONObject(geneName);
            Iterator<String> interactionsIterator = geneInteractions.keys();
            while (interactionsIterator.hasNext()) {
                String interactionType = interactionsIterator.next();
                Interaction interaction = new Interaction(interactionType);
                interactionTypes.add(interactionType);

                JSONArray drugs = geneInteractions.getJSONArray(interactionType);
                for (int i = 0; i < drugs.length(); i++) {
                    String drugName = drugs.getString(i);
                    Drug drug = new Drug(drugName);
                    interaction.drugs.add(drug);
                }

                gene.interactions.add(interaction);
            }

            genes.add(gene);
        }
    }

    public static synchronized InteractionsDatabase getDB () {
        if (internalDB == null) {
            internalDB = new InteractionsDatabase();
        }

        return internalDB;
    }

    // Prevent cloning to avoid goofy corner-case synchronization problems
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private class Drug {
        public final String name;

        public Drug (String name) {
            this.name = name;
        }

        public String toString () {
            return name;
        }
    }

    private class Interaction {
        public final String type;
        public List<Drug> drugs = new ArrayList<>();

        public Interaction (String type) {
            this.type = type;
        }

        public String toString () {
            return type;
        }
    }

    private class Gene {
        public final String name;
        public List<Interaction> interactions = new ArrayList<>();

        public Gene (String name) {
            this.name = name;
        }

        public String toString () {
            return name;
        }
    }
}
