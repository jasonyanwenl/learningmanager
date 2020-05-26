package com.yanwenl.codingmanager.controller;

import com.yanwenl.codingmanager.model.Label;
import com.yanwenl.codingmanager.model.Record;
import com.yanwenl.codingmanager.model.Tag;
import com.yanwenl.codingmanager.service.LabelService;
import com.yanwenl.codingmanager.service.RecordService;
import com.yanwenl.codingmanager.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/record")
@Slf4j
public class RecordController extends RecordBaseController {

    @Autowired
    public RecordController(RecordService theRecordService,
                            LabelService theLabelService,
                            TagService tagService) {
        super(theRecordService, theLabelService, tagService);
    }

    @GetMapping("")
    public String get(Model model,
                             @RequestParam(required = false,
                                     defaultValue = "-1") int id,
                             @RequestParam(required = false,
                                     defaultValue = "-1") int number) {
        List<Record> records = getRecordsConditional(id, number);
        return doGet(records, model);
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        Record record = new Record();

        List<Label> levelLabels = labelService.findByField("level");

        model.addAttribute("record", record);
        model.addAttribute("levelLabels", levelLabels);

        log.debug("Model: " + model);

        return "record-form";
    }

    @PostMapping("/showFormForUpdate")
    public String showFormForUpdate(Model model, @RequestParam("recordId") int recordId) {
        Record record = recordService.findById(recordId);

        List<Label> levelLabels = labelService.findByField("level");

        model.addAttribute("record", record);
        model.addAttribute("levelLabels", levelLabels);

        return "record-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("recordx") Record record) {
        log.debug("Try to save record: " + record);

        if (record.getId() == 0) {
            recordService.add(record);
        } else {
            recordService.update(record);
        }

        return "redirect:/record";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("recordId") int id) {
        recordService.deleteById(id);

        return "redirect:/record";
    }

    // TODO: Currently only support search by label type
    @PostMapping("/search")
    public String search(Model model,
                         @RequestParam("keyword") String keyword) {
        log.debug("Search by keyword: " + keyword);

        List<Label> labels = labelService.findByType(keyword);

        List<Record> records = new ArrayList<>();

        for (Label label : labels) {
            int labelId = label.getId();
            List<Tag> tags = tagService.findByLabelId(labelId);
            for (Tag tag : tags) {
                int recordId = tag.getRecordId();
                records.add(recordService.findById(recordId));
            }
        }

        return doGet(records, model);
    }

    private String doGet(List<Record> records, Model model) {
        Map<Record, Map<String, List<Label>>>
                labelGroupByFieldGroupByRecord = findLabelGroupByRecord();
        List<Label> levelLabels = labelService.findByField("level");

        model.addAttribute("records", records);
        model.addAttribute("labelGroupByFieldGroupByRecord",
                labelGroupByFieldGroupByRecord);
        model.addAttribute("levelLabels", levelLabels);
        model.addAttribute("newRecord", new Record()); // Used for update

        return "list-records";
    }
}
