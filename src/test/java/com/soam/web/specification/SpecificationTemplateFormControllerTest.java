package com.soam.web.specification;

import com.soam.config.SoamConfiguration;
import com.soam.model.objective.ObjectiveTemplate;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.stakeholder.StakeholderTemplate;
import com.soam.model.templatelink.TemplateLink;
import com.soam.service.EntityNotFoundException;
import com.soam.service.priority.PriorityService;
import com.soam.service.specification.SpecificationTemplateService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationTemplateFormController.class)
@Import(SoamConfiguration.class)
class SpecificationTemplateFormControllerTest {
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_1 = new SpecificationTemplate();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_2 = new SpecificationTemplate();
    private static final SpecificationTemplate TEST_SPECIFICATION_TEMPLATE_3_COPY = new SpecificationTemplate();
    private static final int EMPTY_SPECIFICATION_TEMPLATE_ID = 200;

    private static final String URL_NEW_TEMPLATE =  "/specification/template/new";
    private static final String URL_EDIT_TEMPLATE =  "/specification/template/{specificationId}/edit";
    private static final String URL_DELETE_TEMPLATE =  "/specification/template/{specificationId}/delete";

    static {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);

        TEST_SPECIFICATION_TEMPLATE_1.setId(100);
        TEST_SPECIFICATION_TEMPLATE_1.setName("Test Spec 1");
        TEST_SPECIFICATION_TEMPLATE_1.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE_1.setNotes("notes");
        TEST_SPECIFICATION_TEMPLATE_1.setPriority(lowPriority);
        
        TEST_SPECIFICATION_TEMPLATE_2.setId(101);
        TEST_SPECIFICATION_TEMPLATE_2.setName("Test Spec 2");
        TEST_SPECIFICATION_TEMPLATE_2.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE_2.setNotes("notes");
        TEST_SPECIFICATION_TEMPLATE_2.setPriority(lowPriority);

        TEST_SPECIFICATION_TEMPLATE_3_COPY.setId(102);
        TEST_SPECIFICATION_TEMPLATE_3_COPY.setName("Test Spec 3 Copy");
        TEST_SPECIFICATION_TEMPLATE_3_COPY.setDescription("desc");
        TEST_SPECIFICATION_TEMPLATE_3_COPY.setNotes("notes");
        TEST_SPECIFICATION_TEMPLATE_3_COPY.setPriority(lowPriority);

        StakeholderTemplate testStakeholderTemplate = new StakeholderTemplate();
        testStakeholderTemplate.setId(1000);
        testStakeholderTemplate.setName("Test Stakeholder Template");
        testStakeholderTemplate.setDescription("Test description");
        testStakeholderTemplate.setNotes("Test notes");
        testStakeholderTemplate.setPriority(lowPriority);

        ObjectiveTemplate testObjectiveTemplate = new ObjectiveTemplate();
        testObjectiveTemplate.setId(10000);
        testObjectiveTemplate.setName("Test Objective Template");
        testObjectiveTemplate.setDescription("Test description");
        testObjectiveTemplate.setNotes("Test notes");
        testObjectiveTemplate.setPriority(lowPriority);

        TemplateLink testTemplateLink = new TemplateLink();
        testTemplateLink.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_2);
        testTemplateLink.setStakeholderTemplate(testStakeholderTemplate);
        testTemplateLink.setObjectiveTemplate(testObjectiveTemplate);
        TEST_SPECIFICATION_TEMPLATE_2.setTemplateLinks(Lists.newArrayList(testTemplateLink));

        TemplateLink testTemplateLinkCopy = new TemplateLink();
        testTemplateLinkCopy.setSpecificationTemplate(TEST_SPECIFICATION_TEMPLATE_3_COPY);
        testTemplateLinkCopy.setStakeholderTemplate(testStakeholderTemplate);
        testTemplateLinkCopy.setObjectiveTemplate(testObjectiveTemplate);
        TEST_SPECIFICATION_TEMPLATE_3_COPY.setTemplateLinks(Lists.newArrayList(testTemplateLinkCopy));
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecificationTemplateService specificationTemplateService;

    @MockBean
    private PriorityService priorityService;

    @BeforeEach
    void setup() {
        given(specificationTemplateService.getById(TEST_SPECIFICATION_TEMPLATE_1.getId())).willReturn(TEST_SPECIFICATION_TEMPLATE_1);
        given(specificationTemplateService.getById(TEST_SPECIFICATION_TEMPLATE_2.getId())).willReturn(TEST_SPECIFICATION_TEMPLATE_2);
        given(specificationTemplateService.getById(EMPTY_SPECIFICATION_TEMPLATE_ID)).willThrow(new EntityNotFoundException("Specification Template", EMPTY_SPECIFICATION_TEMPLATE_ID));
        given(specificationTemplateService.findByName(TEST_SPECIFICATION_TEMPLATE_1.getName())).willReturn(Optional.of(TEST_SPECIFICATION_TEMPLATE_1));

        given(specificationTemplateService.saveDeepCopy(eq(TEST_SPECIFICATION_TEMPLATE_2), any())).willReturn(TEST_SPECIFICATION_TEMPLATE_3_COPY);
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get(URL_NEW_TEMPLATE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeExists(ModelConstants.ATTR_PRIORITIES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATES))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", SpecificationTemplateFormController.CREATE_MODE_FROM_TEMPLATE)
                        .param("collectionItemId", String.valueOf(TEST_SPECIFICATION_TEMPLATE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUCCESS))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessCreationFormError() throws Exception {
        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "")
                        .param("collectionType", "")
                        .param("collectionItemId", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrorCode(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "description", "NotBlank"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_NEW_TEMPLATE).param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description")
                        .param("collectionType", SpecificationTemplateFormController.CREATE_MODE_FROM_TEMPLATE)
                        .param("collectionItemId", String.valueOf(EMPTY_SPECIFICATION_TEMPLATE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("name", is(TEST_SPECIFICATION_TEMPLATE_1.getName()))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("description", is("desc"))))
                .andExpect(model().attribute(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, hasProperty("notes", is("notes"))))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_TEMPLATE, EMPTY_SPECIFICATION_TEMPLATE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                                .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                                .param("notes", "notes here")
                                .param("description", "description there"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessUpdateFormError() throws Exception {
        mockMvc.perform(post(URL_EDIT_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "description"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_TEMPLATE, EMPTY_SPECIFICATION_TEMPLATE_ID)
                        .param("name", TEST_SPECIFICATION_TEMPLATE_1.getName())
                        .param("notes", "notes")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_SPECIFICATION_TEMPLATE, "name"))
                .andExpect(view().name(ViewConstants.VIEW_SPECIFICATION_TEMPLATE_ADD_OR_UPDATE_FORM));
    }

    @Test
    void testProcessDeleteSuccess() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_TEMPLATE_1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }

    @Test
    void testProcessDeleteError() throws Exception {
        mockMvc.perform(post(URL_DELETE_TEMPLATE, EMPTY_SPECIFICATION_TEMPLATE_ID)
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_TEMPLATE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_1.getId())
                        .param("id", String.valueOf(EMPTY_SPECIFICATION_TEMPLATE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));

        mockMvc.perform(post(URL_DELETE_TEMPLATE, TEST_SPECIFICATION_TEMPLATE_2.getId())
                        .param("id", String.valueOf(TEST_SPECIFICATION_TEMPLATE_2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_SUB_MESSAGE))
                .andExpect(view().name(RedirectConstants.REDIRECT_SPECIFICATION_TEMPLATE_LIST));
    }
}
