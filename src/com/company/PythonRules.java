package com.company;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PythonRules implements LanguageStateMachine {
    ArrayList<Transition> transitions;
    State[] finalStates;
    HashMap<String, Double> keywords;

    PythonRules() {
        String operators = "[\\+\\-\\*/%=<>]";
        String delimiters = "[\\[\\]()\\{\\},:]";

        String[][] kw = {
                {"and", "8"},
                {"or", "8"},
                {"not", "8"},
                {"True", "10"},
                {"False", "10"},
                {"if", "9"},
                {"while", "9"},
                {"in", "9"},
                {"def", "9"},
                {"import", "9"},
                {"from", "9"},
                {"as", "9"},
        };
        keywords = new HashMap<>();
        for (String[] kv: kw) {
            keywords.put(kv[0], Double.parseDouble(kv[1]));
        }
        finalStates = new State[]{
                new State(1, "integer"),
                new State(2, "double"),
                new State(1.1, "integer"),
                new State(1.3, "integer"),
                new State(3, "hex"),
                new State(4, "delimiters"),
                new State(5, "comment"),
                new State(6, "string"),
                new State(7, "operators"),
                new State(8, "operators_word"),
                new State(9, "keywords"),
                new State(10, "bool"),
                new State(-2, "error"),
                new State(-1, "name")
        };

        transitions = new ArrayList<>(Arrays.asList(
                new Transition(-1, "[ \\n\\r]", 0),
                //
                new Transition(0, "[ \\n\\r]", 0),
                new Transition(-2, "[a-zA-Z0-9]", -2),
                // numbers
                new Transition(0, "[0]", 1.3),
                new Transition(0, "[1-9]", 1),
                new Transition(1.3, "[0-9]", 1),
                new Transition(1, "[0-9]", 1),
                new Transition(1, "[.]", 1.1),
                new Transition(1, "[a-zA-z]", -2),
                new Transition(1.1, "[0-9]", 2),
                new Transition(1.1, "[.]", -2),
                new Transition(2, "[0-9]", 2),
                new Transition(2, "[.]", -2),
                new Transition(0, "[.]", 1.2),
                new Transition(1.2, "[0-9]", 2),
                new Transition(1.3, "[x]", 1.31),
                new Transition(1.31, "[0-9]", 3),
                new Transition(3, "[0-9]", 3),
                //---------

                // delimiters
                new Transition(0, delimiters, 4),
                new Transition(4, "[^\n]", 0),
                //-----------

                // comments
                new Transition(0, "[#]", 5),
                new Transition(5, "[^\n]", 5),
                new Transition(5, "[\n]", 0),
                //---------

                //strings
                new Transition(0, "[\"]", 6.1),
                new Transition(6.1, "[^\"]", 6.1),
                new Transition(6.1, "[\"]", 6),

                new Transition(0, "[']", 6.2),
                new Transition(6.2, "[^']", 6.2),
                new Transition(6.2, "[']", 6),
                new Transition(6, "[^\n]", 0),
                //---------


                // operators
                new Transition(0, operators, 7),
                new Transition(7, "[^\n]", 0),
                //---------

                new Transition(8, "[ \n\r]", 0),
                new Transition(8, delimiters, 0),
                new Transition(8, operators, 0),

                new Transition(9, "[ \n\r]", 0),
                new Transition(9, delimiters, 0),
                new Transition(9, operators, 0),

                new Transition(10, "[ \n\r]", 0),
                new Transition(10, delimiters, 0),
                new Transition(10, operators, 0),

                new Transition(-1, "[ \n\r]", 0),
                new Transition(-1, delimiters, 0),
                new Transition(-1, operators, 0)
        ));

        HashSet<Double> allStates = new HashSet<>();
        for (Transition t : transitions) {
            allStates.add(t.to);
        }
        allStates.remove(0.0);
        allStates.remove(5.0);
        allStates.remove(6.1);
        allStates.remove(6.2);
        for (Double s : allStates) {
            transitions.add(new Transition(s, delimiters, 0)); //delimiters
            transitions.add(new Transition(s, operators, 0)); //delimiters
            transitions.add(new Transition(s, "[ \\n\\r]", 0)); //spaces
            transitions.add(new Transition(s, "#", 0)); //comment char '#'
            transitions.add(new Transition(s, "['\"]", 0)); //start string char
        }
    }

    public ArrayList getTransitions() {
        return transitions;
    }

    private ArrayList<Transition> generateTransactionsForWord(double from, String word, double to) {
        ArrayList<Transition> res = new ArrayList<>();
        double temp = from;
        for (int i = 0; i < word.length() - 1; i++) {
            char c = word.toCharArray()[i];
            char c_ = word.toCharArray()[i + 1];
            double temp_ = to + (double) (i) / 1000.0 + (double) (c_) / 100000.0;
            res.add(new Transition(temp,
                    Character.toString(c),
                    temp_
            ));
            temp = temp_;
        }
        res.add(new Transition(temp,
                Character.toString(word.toCharArray()[word.length() - 1]),
                to
        ));
        return res;
    }

    public State[] getFinalStates() {
        return finalStates;
    }
    public HashMap<String, Double> getKeywords() { return keywords; }
}
