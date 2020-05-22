package com.yanwenl.codingmanager.repository;

import com.yanwenl.codingmanager.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Integer> {

    List<Label> findLabelByField(String field);

    List<Label> findLabelByType(String type);

    //TODO: distinct not needed
    @Query("SELECT DISTINCT field FROM Label")
    List<String> findDistinctFields();
}
