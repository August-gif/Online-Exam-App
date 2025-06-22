package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.QuestionChoixMultiple;
import com.BrainTech.Online_exam_App_server.repository.QuestionChoixMultipleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionChoixMultipleServiceImpl implements QuestionChoixMultipleService {

    private final QuestionChoixMultipleRepository questionChoixMultipleRepository;

    public QuestionChoixMultipleServiceImpl(QuestionChoixMultipleRepository questionChoixMultipleRepository) {
        this.questionChoixMultipleRepository = questionChoixMultipleRepository;
    }

    @Override
    public List<QuestionChoixMultiple> getAllQuestionChoixMultiples() {
        return questionChoixMultipleRepository.findAll();
    }

    @Override
    public Optional<QuestionChoixMultiple> getQuestionChoixMultipleById(Long id) {
        return questionChoixMultipleRepository.findById(id);
    }

    @Override
    public QuestionChoixMultiple createQuestionChoixMultiple(QuestionChoixMultiple questionChoixMultiple) {
        return questionChoixMultipleRepository.save(questionChoixMultiple);
    }

    @Override
    public QuestionChoixMultiple updateQuestionChoixMultiple(Long id, QuestionChoixMultiple questionChoixMultiple) {
        return questionChoixMultipleRepository.findById(id)
                .map(existingQuestionCmulti-> {
                    questionChoixMultiple.setId(id);
                    return questionChoixMultipleRepository.save(questionChoixMultiple);
                })
                .orElseThrow(() -> new ResourceNotFoundException("QuestionChoix multi not found with ID: " + id));
    }

    @Override
    public void deleteQuestionChoixMultiple(Long id) {
        // Vérifie si l'examen existe avant de tenter de le supprimer
        if (!questionChoixMultipleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with ID: " + id);
        }
        questionChoixMultipleRepository.deleteById(id);

    }
}
