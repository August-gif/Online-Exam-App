package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Option;
import com.BrainTech.Online_exam_App_server.repository.OptionRepository;
import com.BrainTech.Online_exam_App_server.service.OptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;

    public OptionServiceImpl(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }
    @Override
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    @Override
    public Optional<Option> getOptionById(Long id) {
        return optionRepository.findById(id);
    }

    @Override
    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    @Override
    public Option updateOption(Long id, Option option) {
        return optionRepository.findById(id)
                .map(existingOption -> {
                    option.setId(id);
                    return optionRepository.save(option);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with ID: " + id)); // Ou lancez une exception
    }

    @Override
    public void deleteOption(Long id) {
        optionRepository.deleteById(id);

    }

    @Override
    public List<Option> getOptionsByQuestionId(Long questionId) {
        // Supposons que vous ayez une méthode correspondante dans OptionRepository
        return optionRepository.findByQuestionChoixMultipleId(questionId); // Adaptez selon votre modèle
       // À implémenter selon votre modèle de données
    }
}
