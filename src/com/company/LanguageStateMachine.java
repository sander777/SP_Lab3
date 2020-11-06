package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public interface LanguageStateMachine {
    public ArrayList<Transition> getTransitions();
    public State[] getFinalStates();
    public HashMap<String, Double> getKeywords();
}

