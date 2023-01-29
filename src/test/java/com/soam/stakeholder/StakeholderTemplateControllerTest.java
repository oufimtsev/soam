package com.soam.stakeholder;

import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.stakeholder.StakeholderTemplateRepository;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.ViewConstants;
import com.soam.web.stakeholder.StakeholderTemplateController;
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

@WebMvcTest(StakeholderTemplateController.class)
class StakeholderTemplateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private StakeholderTemplateRepository stakeholderTemplateRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    private static StakeholderTemplate TEST_STAKEHOLDER_1 = new StakeholderTemplate();
    private static final int EMPTY_STAKEHOLDER_ID = 999;

    private static String URL_NEW_STAKEHOLDER = "/stakeholder/new";
    private static String URL_EDIT_STAKEHOLDER = "/stakeholder/{stakeholderId}/edit";
    private static String URL_DELETE_STAKEHOLDER = "/stakeholder/{stakeholderId}/delete";
    
    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_STAKEHOLDER_1.setId(100);
        TEST_STAKEHOLDER_1.setName("Test Spec 1");
        TEST_STAKEHOLDER_1.setDescription("desc");
        TEST_STAKEHOLDER_1.setNotes("notes");
        TEST_STAKEHOLDER_1.setPriority(lowPriority);
    }

    @BeforeEach
    void setup() {
        given( this.stakeholderTemplateRepository.findByName(TEST_STAKEHOLDER_1.getName())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderTemplateRepository.findByNameIgnoreCase("Test Spec")).willReturn(Optional.of(TEST_STAKEHOLDER_1));
        given( this.stakeholderTemplateRepository.findById(TEST_STAKEHOLDER_1.getId())).willReturn(Optional.of(TEST_STAKEHOLDER_1));
    }

    @Test
    void tesInitFindForm() throws Exception {
        mockMvc.perform(get("/stakeholder/template/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        Page<StakeholderTemplate> stakeholderTemplates = new PageImpl<>(Lists.newArrayList(TEST_STAKEHOLDER_1, new StakeholderTemplate()));
        Mockito.when(this.stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(anyString(), any(Pageable.class))).thenReturn(stakeholderTemplates);
        mockMvc.perform(get("/stakeholder/templates?page=1").param("name", "Te"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST));
    }

    @Test
    void testProcessFindFormByName() throws Exception {
        Page<StakeholderTemplate> stakeholders = new PageImpl<>(Lists.newArrayList(TEST_STAKEHOLDER_1));
        //todo: Use constant for "Test"
        Mockito.when(this.stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class))).thenReturn(stakeholders);
        Mockito.when(this.stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(eq("Not Present"), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        mockMvc.perform(get("/stakeholder/templates?page=1").param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format(RedirectConstants.REDIRECT_STAKEHOLDER_TEMPLATE_EDIT, TEST_STAKEHOLDER_1.getId())));

        mockMvc.perform(get("/stakeholder/templates?page=1").param("name", "Not Present"))
                .andExpect(status().isOk())
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));

        mockMvc.perform(get("/stakeholder/templates?page=1").param("name", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_STAKEHOLDER_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_FIND_STAKEHOLDER_TEMPLATE));
    }

    @Test
    void testListAll() throws Exception {
        Page<StakeholderTemplate> stakeholderTemplatesPage = new PageImpl<>(Lists.newArrayList(TEST_STAKEHOLDER_1));
        Mockito.when(this.stakeholderTemplateRepository.findByNameStartsWithIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(stakeholderTemplatesPage);

        mockMvc.perform( get("/stakeholder/template/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_STAKEHOLDER_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_STAKEHOLDER_TEMPLATE_LIST));
    }
}
