package com.company;

import java.util.ArrayList;

public class Transition {
    public double from;
    public double to;
    public String reg;

    public Transition(String str) {
        char arr[] = str.toCharArray();
        int i = 0;
        int j = arr.length - 1;
        while (arr[i] != ' ') i++;
        while (arr[j] != ' ') j--;
        from = Double.parseDouble(str.substring(0, i).trim());
        to = Double.parseDouble(str.substring(j, arr.length).trim());
        reg = str.substring(i + 1, j).replaceAll("\\\\n", "\n")
                .replaceAll("\\\\r", "\r")
                .replaceAll("\\\\s", "\s");
        System.out.println(reg);
    }

    public Transition(double from, String reg, double to) {
        this.from = from;
        this.to = to;
        this.reg = reg;
    }

    public static ArrayList<Transition> getAllTransitions(String str) {
        String[] in = str.split(" ");
        ArrayList<Transition> transitions = new ArrayList<>();
        if (in[1].equals("word")) {
            int i = 0;
            double state = Double.parseDouble(in[0]);
            for (; i < in[2].length() - 1; i++) {
                transitions.add(new Transition(
                        state + (double) i / 100,
                        Character.toString(in[2].toCharArray()[i]),
                        state + ((double) i + 1) / 100
                ));
            }
            transitions.add(new Transition(
                    state + ((double) i) / 100,
                    Character.toString(in[2].toCharArray()[i]),
                    Double.parseDouble(in[3])
            ));
        } else {
            transitions.add(new Transition(str));
        }
        return transitions;
    }
}
