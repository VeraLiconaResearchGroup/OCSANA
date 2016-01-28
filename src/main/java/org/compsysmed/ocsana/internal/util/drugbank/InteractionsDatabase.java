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
import java.util.stream.Collectors;

// JSON imports
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class InteractionsDatabase {
    private static final String drugBankPath = "/drugbank/drugbank.json";
    private static InteractionsDatabase internalDB;

    private List<Gene> genes = new ArrayList<>();
    private Map<String, Gene> geneNameMap = new HashMap<>();

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
                    JSONObject drugObject = drugs.getJSONObject(i);

                    String drugName = drugObject.getString("name");

                    Drug drug = new Drug(drugName);
                    drug.setGroups(drugObject.getJSONArray("groups"));
                    interaction.drugs.add(drug);
                }

                gene.interactions.add(interaction);
            }

            genes.add(gene);
            geneNameMap.put(geneName, gene);
        }
    }

    public static synchronized InteractionsDatabase getDB () {
        if (internalDB == null) {
            internalDB = new InteractionsDatabase();
        }

        return internalDB;
    }

    public Set<String> geneNames () {
        Set<String> result = new HashSet<>();
        for (Gene gene: genes) {
            result.add(gene.name);
        }
        return result;
    }

    public int numGenes () {
        return genes.size();
    }

    private Gene getGene (String geneName) {
        return geneNameMap.get(geneName.toUpperCase());
    }

    public Set<String> drugNamesForGene (String geneName) {
        Gene gene = getGene(geneName);
        if (gene == null) {
            return new HashSet<>();
        } else {
            return gene.drugNames();
        }
    }

    public Map<String, Set<String>> drugInteractionGroupsForGene (String geneName) {
        Gene gene = getGene(geneName);
        if (gene == null) {
            return new HashMap<>();
        } else {
            return gene.drugInteractionGroups();
        }
    }

    // Prevent cloning to avoid goofy corner-case synchronization problems
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private static final Set<String> knownDrugGroups = new HashSet<>(Arrays.asList("approved", "investigational", "experimental", "nutraceutical", "illicit", "withdrawn"));

    private static class Drug {
        public final String name;
        public Set<String> groups = new HashSet<>();

        public Drug (String name) {
            this.name = name;
        }

        public void setGroups (JSONArray groupsArray) {
            for (int i = 0; i < groupsArray.length(); i++) {
                String group = groupsArray.getString(i);
                assert knownDrugGroups.contains(group);
                groups.add(group);
            }
        }

        public Set<String> getGroupNames () {
            return groups.stream().map(group -> group.toString()).collect(Collectors.toSet());
        }

        public String toString () {
            return name;
        }
    }

    private static class Interaction {
        public final String type;
        public List<Drug> drugs = new ArrayList<>();

        public Interaction (String type) {
            this.type = type;
        }

        public String toString () {
            return type;
        }

        public Set<String> drugNames () {
            Set<String> result = new HashSet<>();
            for (Drug drug: drugs) {
                result.add(drug.name);
            }
            return result;
        }
    }

    private static class Gene {
        public final String name;
        public List<Interaction> interactions = new ArrayList<>();

        public Gene (String name) {
            this.name = name;
        }

        public String toString () {
            return name;
        }

        public Map<String, Set<String>> drugInteractionGroups () {
            Map<String, Set<String>> result = new HashMap<>();
            for (Interaction interaction: interactions) {
                result.put(interaction.type, interaction.drugNames());
            }
            return result;
        }

        public Set<String> drugNames () {
            Set<String> result = new HashSet<>();
            for (Interaction interaction: interactions) {
                result.addAll(interaction.drugNames());
            }
            return result;
        }
    }
}
