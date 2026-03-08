package com.hackerrank.sample.service;

import com.hackerrank.sample.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ModelService {
    void deleteAllModels();
    void deleteModelById(Long id);

    void createModel(Model model);
    
    void updateModel(Model model);

    Model getModelById(Long id);

    List<Model> getAllModels();

    Page<Model> getAllModelsPaginated(Pageable pageable);
}
