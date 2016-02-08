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
    private static final Set<String> KNOWN_DRUG_GROUPS = new HashSet<>(Arrays.asList("approved", "investigational", "experimental", "nutraceutical", "illicit", "withdrawn"));

    private static final String DRUGBANK_PATH = "/drugbank/drugbank.json";
    private static InteractionsDatabase internalDB;

    private List<Gene> genes = new ArrayList<>();
    private Map<String, Gene> geneNameMap = new HashMap<>();

    public Set<String> interactionTypes = new HashSet<>();

    private InteractionsDatabase () {
        JSONObject drugBankJSON;
        try (InputStream jsonFileStream = getClass().getResourceAsStream(DRUGBANK_PATH)) {
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
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private static class Drug {
        public final String name;
        public final Set<String> groups = new HashSet<>();

        public Drug (String name) {
            this.name = name;
        }

        public void setGroups (JSONArray groupsArray) {
            for (int i = 0; i < groupsArray.length(); i++) {
                String group = groupsArray.getString(i);
                assert KNOWN_DRUG_GROUPS.contains(group);
                groups.add(group);
            }
        }

        public Set<String> getGroupNames () {
            return groups.stream().map(group -> group.toString()).collect(Collectors.toSet());
        }

        @Override
        public String toString () {
            return name;
        }
    }

    private static class Interaction {
        public final String type;
        public final List<Drug> drugs = new ArrayList<>();
        public final DrugActionSign sign;

        public Interaction (String type) {
            this.type = type;
            if (ACTION_SIGN.containsKey(type)) {
                sign = ACTION_SIGN.get(type);
            } else {
                throw new IllegalStateException(String.format("Interaction %s is not in ontology", type));
            }
        }

        @Override
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
        public final List<Interaction> interactions = new ArrayList<>();

        public Gene (String name) {
            this.name = name;
        }

        @Override
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

    private static final Map<String, DrugActionSign> ACTION_SIGN
        = Arrays.asList(
                        new AbstractMap.SimpleImmutableEntry<>("acetylation", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("activator", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("adduct", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("agonist", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("allosteric modulator", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("antagonist", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("antibody", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("blocker", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("binder", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("binding", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("chaperone", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("chelator", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("cleavage", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("cofactor", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("cross-linking/alkylation", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("desensitize the target", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("incorporation into and destabilization", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("inducer", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("inhibitor", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("inhibitor, competitive", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("inhibitory allosteric modulator", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("intercalation", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("inverse agonist", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("ligand", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("modulator", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("multitarget", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("negative modulator", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("neutralizer", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("other", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("other/unknown", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("partial agonist", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("partial antagonist", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("positive allosteric modulator", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("positive modulator", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("potentiator", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("product of", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("reducer", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("stimulator", DrugActionSign.POSITIVE),
                        new AbstractMap.SimpleImmutableEntry<>("suppressor", DrugActionSign.NEGATIVE),
                        new AbstractMap.SimpleImmutableEntry<>("unknown", DrugActionSign.UNSIGNED),
                        new AbstractMap.SimpleImmutableEntry<>("UNKNOWN", DrugActionSign.UNSIGNED)
                        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
