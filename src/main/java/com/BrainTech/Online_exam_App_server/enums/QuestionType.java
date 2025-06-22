package com.BrainTech.Online_exam_App_server.enums;

public enum QuestionType {
    // Les valeurs doivent correspondre aux DiscriminatorValue dans les sous-classes de Question
    CHOIX_MULTIPLE("CHOIX_MULTIPLE"),
    CONVENTIONNELLE("CONVENTIONNELLE"),
    MIXTE("MIXTE");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
