package com.soam.web.specification;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.service.EntityNotFoundException;
import com.soam.service.specification.SpecificationService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationController.class)
@Import(SoamConfiguration.class)
class SpecificationControllerTest {
    private static final Specification TEST_SPECIFICATION_1 = new Specification();
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
    private SpecificationService specificationService;

    @BeforeEach
    void setup() {
        given(specificationService.getById(TEST_SPECIFICATION_1.getId())).willReturn(TEST_SPECIFICATION_1);
        given(specificationService.getById(EMPTY_SPECIFICATION_ID)).willThrow(EntityNotFoundException.class);
    }

    @Test
    void tesInitFindForm() throws Exception {
        mockMvc.perform(get("/specification/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        List<Specification> specifications = Lists.newArrayList(TEST_SPECIFICATION_1, new Specification());
        Mockito.when(specificationService.findByPrefix(anyString())).thenReturn(specifications);
        mockMvc.perform(get("/specifications?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_LIST));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        List<Specification> specifications = Lists.newArrayList(TEST_SPECIFICATION_1);

        Mockito.when(specificationService.findByPrefix(eq("Test"))).thenReturn(specifications);
        Mockito.when(specificationService.findByPrefix(eq("Not Present"))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/specifications?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_DETAILS, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(get("/specifications?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION));

        mockMvc.perform(get("/specifications?page=1").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION, "name"))
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION));
    }

    @Test
    void testListAll() throws Exception {
        Page<Specification> specifications = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));
        Mockito.when(specificationService.findAll(anyInt())).thenReturn(specifications);
        mockMvc.perform(get("/specification/list").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PAGINATED))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_LIST));
    }

    @Test
    void testViewDetails() throws Exception {
        mockMvc.perform(get("/specification/{specificationId}", TEST_SPECIFICATION_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION, hasProperty("name", is("Test Spec 1"))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_DETAILS));

        mockMvc.perform(get("/specification/{specificationId}", EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_FIND_SPECIFICATION));
    }
}
