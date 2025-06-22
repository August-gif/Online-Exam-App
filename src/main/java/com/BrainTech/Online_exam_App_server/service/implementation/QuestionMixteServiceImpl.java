package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.QuestionMixte;
import com.BrainTech.Online_exam_App_server.repository.QuestionMixteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class QuestionMixteServiceImpl implements QuestionMixteService {

    private final QuestionMixteRepository questionMixteRepository;

    public QuestionMixteServiceImpl(QuestionMixteRepository questionMixteRepository) {
        this.questionMixteRepository = questionMixteRepository;
    }

    @Override
    public List<QuestionMixte> getAllQuestionMixtes() {
        return questionMixteRepository.findAll();
    }

    @Override
    public Optional<QuestionMixte> getQuestionMixteById(Long id) {
        return questionMixteRepository.findById(id);
    }

    @Override
    public QuestionMixte createQuestionMixte(QuestionMixte questionMixte) {
        return questionMixteRepository.save(questionMixte);
    }

    @Override
    public QuestionMixte updateQuestionMixte(Long id, QuestionMixte questionMixte) {
        return questionMixteRepository.findById(id)
                .map(existingQuestionCmulti-> {
                    questionMixte.setId(id);
                    return questionMixteRepository.save(questionMixte);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Question Mixte not found with ID: " + id));
    }

    @Override
    public void deleteQuestionMixte(Long id) {
        questionMixteRepository.deleteById(id);

    }
}
