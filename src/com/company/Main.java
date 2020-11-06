package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
public class Main {


    public static class StateMachine {
        public double currentState;
        public double initState;
        public HashSet<State> finalStates;
        public HashSet<Transition> transitions;
        public String wordCount = "";
        HashMap<String, Double> keywords;

        public StateMachine() {
            PythonRules rules = new PythonRules();
            transitions = new HashSet<>();
            transitions.addAll(rules.getTransitions());
            finalStates = new HashSet<>();
            finalStates.addAll(Arrays.asList(rules.getFinalStates()));
            keywords = rules.getKeywords();
        }

        public boolean Process(String c) {
            boolean processed = false;
            for (Transition tr :
                    transitions) {
                if (tr.from == currentState && c.matches(tr.reg)) {
                    currentState = tr.to;
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                currentState = -1;
                wordCount += c;
            }
            if(wordCount.length() > 0) {
                try {
                    Double s = keywords.get(wordCount);
                    if (s != null && currentState == -1) {
                        currentState = s;
                    }
                } catch (Exception e) {

                }

            }
            if (currentState == 0) {
                wordCount = "";
            }
            return processed;
        }

        public boolean isInFinal() {
            boolean b = false;
            for (State s: finalStates) {
                if (s.number == currentState) {
                    b = true;
                }
            }
            return b;
        }

        public void reload() {
            currentState = initState;
        }
    }

    public static void main(String[] args) throws IOException {
        FileWriter out = new FileWriter("out.html");
        StateMachine stateMachine = new StateMachine();
        boolean reloaded = false;
        String code = Files.readString(Path.of("input.py")).replaceAll("\r", "") + " ";
        boolean fin = false;
        StringBuilder token = new StringBuilder();
        out.write("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Document</title>
                    <link rel="stylesheet" href="style.css">
                </head>
                <body><pre>
                """);
        for (int i = 0; i < code.length(); ) {
            double s = stateMachine.currentState;
            fin = stateMachine.isInFinal();

            String toString = Character.toString(code.toCharArray()[i]);
            stateMachine.Process(toString);
            if (stateMachine.currentState == 0 & fin) {
                State[] type = stateMachine.finalStates.stream().filter(state -> state.number == s).toArray(State[]::new);
                out.write("<span class=\"" + type[0].name +"\">" +token.toString().trim() + "</span>");
                token = new StringBuilder();
                i--;
            } else {
                if (toString.equals(" ") || toString.equals("\t") || toString.equals("\n")) {
                    out.write(toString);
                }
                token.append(code.toCharArray()[i]);
            }

            i++;
        }
        out.write(
                "</pre></body>\n" +
                "</html>");
        out.close();
    }
}
