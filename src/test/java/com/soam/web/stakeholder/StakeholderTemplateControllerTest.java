package com.soam.web.stakeholder;

import com.soam.config.SoamConfiguration;
import com.soam.model.priority.PriorityType;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.service.stakeholder.StakeholderTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StakeholderTemplateController.class)
@Import(SoamConfiguration.class)
class StakeholderTemplateControllerTest {
    private static final StakeholderTemplate TEST_STAKEHOLDER_TEMPLATE_1 = new StakeholderTemplate();

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_STAKEHOLDER_TEMPLATE_1.setId(100);
        TEST_STAKEHOLDER_TEMPLATE_1.setName("Test Spec 1");
        TEST_STAKEHOLDER_TEMPLATE_1.setDescription("desc");
        TEST_STAKEHOLDER_TEMPLATE_1.setNotes("notes");
        TEST_STAKEHOLDER_TEMPLATE_1.setPriority(lowPriority);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StakeholderTemplateService stakeholderTemplateService;

    @Test
    void tesInitFindForm() throws Exception {
        mockMvc.perform(get("/stakeholder/template/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        List<StakeholderTemplate> stakeholderTemplates = Lists.newArrayList(TEST_STAKEHOLDER_TEMPLATE_1, new StakeholderTemplate());
        given(stakeholderTemplateService.findByPrefix(anyString())).willReturn(stakeholderTemplates);
        mockMvc.perform(post("/stakeholder/template/find").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        List<StakeholderTemplate> stakeholders = Lists.newArrayList(TEST_STAKEHOLDER_TEMPLATE_1);

        given(stakeholderTemplateService.findByPrefix(eq("Test"))).willReturn(stakeholders);
        given(stakeholderTemplateService.findByPrefix(eq("Not Present"))).willReturn(new ArrayList<>());

        mockMvc.perform(post("/stakeholder/template/find").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, TEST_STAKEHOLDER_TEMPLATE_1.getId())));

        mockMvc.perform(post("/stakeholder/template/find").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));

        mockMvc.perform(post("/stakeholder/template/find").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));
    }
}
