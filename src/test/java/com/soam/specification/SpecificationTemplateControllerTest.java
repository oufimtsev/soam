package com.soam.specification;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.web.SpecificationTemplateController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationTemplateController.class)
public class SpecificationTemplateControllerTest {

    private static final int TEST_SPECIFICATION_ID = 100;
    private static final int EMPTY_SPECIFICATION_ID = 200;
    //todo: define more constants!

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specifications;

    @MockBean
    private SpecificationTemplateRepository specificationTemplates;

    @MockBean
    private PriorityRepository priorities;

    private SpecificationTemplate testSpecification() {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);
        SpecificationTemplate testSpec = new SpecificationTemplate();
        testSpec.setId(TEST_SPECIFICATION_ID);
        testSpec.setName("Test Spec");
        testSpec.setDescription("desc");
        testSpec.setNotes("notes");
        testSpec.setPriority(lowPriority);
        return testSpec;
    }

    @BeforeEach
    void setup() {
        SpecificationTemplate testSpec = testSpecification();
        given( this.specificationTemplates.findByName("Test Spec")).willReturn(Optional.of(testSpec));
        given( this.specificationTemplates.findByNameIgnoreCase("Test Spec")).willReturn(Optional.of(testSpec));
        given( this.specificationTemplates.findById(TEST_SPECIFICATION_ID)).willReturn(Optional.of(testSpec));
        given( this.specificationTemplates.findById(EMPTY_SPECIFICATION_ID)).willReturn(Optional.empty());

    }

    @Test
    void tesInitFind() throws Exception {
        mockMvc.perform(get("/specification/template/find")).andExpect(status().isOk())
                .andExpect(view().name("specification/template/findSpecificationTemplate"));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        Page<SpecificationTemplate> specificationTemplates = new PageImpl<>(Lists.newArrayList(testSpecification(), new SpecificationTemplate()));
        Mockito.when(this.specificationTemplates.findByNameStartsWithIgnoreCase(anyString(), any(Pageable.class))).thenReturn(specificationTemplates);
        mockMvc.perform(get("/specification/templates?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/template/specificationTemplateList"));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        Page<SpecificationTemplate> specifications = new PageImpl<>(Lists.newArrayList(testSpecification()));
        //todo: Use constant for "Test"
        Mockito.when(this.specificationTemplates.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class))).thenReturn(specifications);
        Mockito.when(this.specificationTemplates.findByNameStartsWithIgnoreCase(eq("Not Present"), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/specification/templates?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/template/" + TEST_SPECIFICATION_ID+"/edit"));

        mockMvc.perform(get("/specification/templates?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/template/findSpecificationTemplate"));
    }


    @Test
    void testListSpecificationTemplates() throws Exception{
        Page<SpecificationTemplate> specificationTemplatesPage = new PageImpl<>(Lists.newArrayList(testSpecification()));
        Mockito.when(this.specificationTemplates.findByNameStartsWithIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(specificationTemplatesPage);

        mockMvc.perform( get("/specification/template/list"))
                .andExpect(model().attributeExists("listSpecificationTemplates"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/template/specificationTemplateList"));
    }

}
