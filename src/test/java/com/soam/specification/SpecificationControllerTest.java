package com.soam.specification;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.web.ModelConstants;
import com.soam.web.specification.SpecificationController;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationController.class)
public class SpecificationControllerTest {

    private static Specification TEST_SPECIFICATION_1 = new Specification();
    private static final int EMPTY_SPECIFICATION_ID = 999;

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);


        TEST_SPECIFICATION_1.setId(100);
        TEST_SPECIFICATION_1.setName("Test Spec 1");
        TEST_SPECIFICATION_1.setDescription("desc");
        TEST_SPECIFICATION_1.setNotes("notes");
        TEST_SPECIFICATION_1.setPriority(lowPriority);
        TEST_SPECIFICATION_1.setStakeholders(new ArrayList<>());
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationRepository specificationRepository;

    @MockBean
    private SpecificationTemplateRepository specificationTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @BeforeEach
    void setup() {
        given( this.specificationRepository.findByName(TEST_SPECIFICATION_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given( this.specificationRepository.findByNameIgnoreCase(TEST_SPECIFICATION_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given( this.specificationRepository.findById(TEST_SPECIFICATION_1.getId())).willReturn(Optional.of(TEST_SPECIFICATION_1));
        given( this.specificationRepository.findById(EMPTY_SPECIFICATION_ID)).willReturn(Optional.empty());

        given( this.specificationRepository.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class)))
                .willReturn(new PageImpl<Specification>(Lists.newArrayList(TEST_SPECIFICATION_1)));

    }

    @Test
    void tesInitFind() throws Exception {
        mockMvc.perform(get("/specification/find")).andExpect(status().isOk())
                .andExpect(view().name("specification/findSpecification"));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        Page<Specification> specifications = new PageImpl<Specification>(Lists.newArrayList(TEST_SPECIFICATION_1, new Specification()));
        Mockito.when(this.specificationRepository.findByNameStartsWithIgnoreCase(anyString(), any(Pageable.class))).thenReturn(specifications);
        mockMvc.perform(get("/specifications?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/specificationList"));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        Page<Specification> specifications = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));
        //todo: Use constant for "Test"
        Mockito.when(this.specificationRepository.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class))).thenReturn(specifications);
        Mockito.when(this.specificationRepository.findByNameStartsWithIgnoreCase(eq("Not Present"), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/specifications?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/" + TEST_SPECIFICATION_1.getId()));


        mockMvc.perform(get("/specifications?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/findSpecification"));

        mockMvc.perform(get("/specifications?page=1").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "name"))
                .andExpect(view().name("specification/findSpecification"));
    }

    @Test
    void testListAll() throws Exception {
        Page<Specification> specifications = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));
        Mockito.when(this.specificationRepository.findByNameStartsWithIgnoreCase(eq(""), any(Pageable.class))).thenReturn(specifications);
        mockMvc.perform(get("/specification/list").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PAGINATED))
                .andExpect(view().name("specification/specificationList"));

    }

    @Test
    void testViewSpecificationDetails() throws Exception {
        mockMvc.perform(get("/specification/{specificationId}", TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("name", is("Test Spec 1"))))
                .andExpect(view().name("specification/specificationDetails"));

        mockMvc.perform(get("/specification/{specificationId}", EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/find"));
    }
}
