package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Option;

import java.util.List;
import java.util.Optional;

public interface OptionService {
    List<Option> getAllOptions();
    Optional<Option> getOptionById(Long id);
    Option createOption(Option option);
    Option updateOption(Long id, Option option);
    void deleteOption(Long id);
    List<Option> getOptionsByQuestionId(Long questionId);
    // Sujet aux ajouts d'autres méthodes spécifiques à la logique métier
}
