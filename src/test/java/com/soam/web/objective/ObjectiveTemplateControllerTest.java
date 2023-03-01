package com.soam.web.objective;

import com.soam.config.SoamConfiguration;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.service.objective.ObjectiveTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ObjectiveTemplateController.class)
@Import(SoamConfiguration.class)
class ObjectiveTemplateControllerTest {
    private static final ObjectiveTemplate TEST_OBJECTIVE_TEMPLATE_1 = new ObjectiveTemplate();

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_OBJECTIVE_TEMPLATE_1.setId(100);
        TEST_OBJECTIVE_TEMPLATE_1.setName("Test Spec 1");
        TEST_OBJECTIVE_TEMPLATE_1.setDescription("desc");
        TEST_OBJECTIVE_TEMPLATE_1.setNotes("notes");
        TEST_OBJECTIVE_TEMPLATE_1.setPriority(lowPriority);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectiveTemplateService objectiveTemplateService;

    @Test
    void tesInitFindForm() throws Exception {
        mockMvc.perform(get("/objective/template/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        Page<ObjectiveTemplate> objectiveTemplates = new PageImpl<>(Lists.newArrayList(TEST_OBJECTIVE_TEMPLATE_1, new ObjectiveTemplate()));
        given(objectiveTemplateService.findByPrefix(anyString(), anyInt())).willReturn(objectiveTemplates);
        mockMvc.perform(get("/objective/templates?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_OBJECTIVE_TEMPLATE_LIST));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        Page<ObjectiveTemplate> objectives = new PageImpl<>(Lists.newArrayList(TEST_OBJECTIVE_TEMPLATE_1));

        given(objectiveTemplateService.findByPrefix(eq("Test"), anyInt())).willReturn(objectives);
        given(objectiveTemplateService.findByPrefix(eq("Not Present"), anyInt())).willReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/objective/templates?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_OBJECTIVE_TEMPLATE_EDIT, TEST_OBJECTIVE_TEMPLATE_1.getId())));

        mockMvc.perform(get("/objective/templates?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE));

        mockMvc.perform(get("/objective/templates?page=1").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_OBJECTIVE_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_OBJECTIVE_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_FIND_OBJECTIVE_TEMPLATE));
    }

    @Test
    void testListAll() throws Exception{
        Page<ObjectiveTemplate> objectiveTemplatesPage = new PageImpl<>(Lists.newArrayList(TEST_OBJECTIVE_TEMPLATE_1));
        given(objectiveTemplateService.findByPrefix(any(String.class), anyInt())).willReturn(objectiveTemplatesPage);

        mockMvc.perform(get("/objective/template/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_OBJECTIVE_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_OBJECTIVE_TEMPLATE_LIST));
    }
}
