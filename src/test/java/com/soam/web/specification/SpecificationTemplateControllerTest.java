package com.soam.web.specification;

import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationTemplateController.class)
class SpecificationTemplateControllerTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_1 = new SpecificationTemplate();

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
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationTemplateService specificationTemplateService;

    @Test
    void tesInitFindForm() throws Exception {
        mockMvc.perform(get("/specification/template/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        Page<SpecificationTemplate> specificationTemplates = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1, new SpecificationTemplate()));
        given(specificationTemplateService.findByPrefix(anyString(), anyInt())).willReturn(specificationTemplates);
        mockMvc.perform(get("/specification/templates?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        Page<SpecificationTemplate> specifications = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));

        given(specificationTemplateService.findByPrefix(eq("Test"), anyInt())).willReturn(specifications);
        given(specificationTemplateService.findByPrefix(eq("Not Present"), anyInt())).willReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/specification/templates?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_EDIT, TEST_SPECIFICATION_1.getId())));

        mockMvc.perform(get("/specification/templates?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE));

        mockMvc.perform(get("/specification/templates?page=1").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_FIND_SPECIFICATION_TEMPLATE));
    }

    @Test
    void testListAll() throws Exception {
        Page<SpecificationTemplate> specificationTemplatesPage = new PageImpl<>(Lists.newArrayList(TEST_SPECIFICATION_1));
        given(specificationTemplateService.findByPrefix(any(String.class), anyInt())).willReturn(specificationTemplatesPage);

        mockMvc.perform(get("/specification/template/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_LIST));
    }
}
