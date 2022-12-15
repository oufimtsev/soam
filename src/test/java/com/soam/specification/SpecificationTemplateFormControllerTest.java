package com.soam.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplate;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.web.specification.SpecificationTemplateFormController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationTemplateFormController.class)
public class SpecificationTemplateFormControllerTest {

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
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/specification/template/new")).andExpect(status().isOk())
                .andExpect(model().attributeExists("specificationTemplate"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("specificationTemplates"))
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/specification/template/new").param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/specification/template/new").param("name", "Test Spec")
                        .param("notes", "spec notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors("specificationTemplate"))
                          .andExpect(model().attributeHasFieldErrors("specificationTemplate", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));

        mockMvc.perform(post("/specification/template/new").param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(model().attributeHasErrors("specificationTemplate"))
                .andExpect(model().attributeHasFieldErrorCode("specificationTemplate", "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));
    }

    @Test
    void testInitUpdateSpecificationForm() throws Exception {
        Mockito.when(this.specificationTemplates.findById(TEST_SPECIFICATION_ID)).thenReturn(Optional.of(testSpecification()));

        mockMvc.perform(get("/specification/template/{specificationId}/edit", TEST_SPECIFICATION_ID)).andExpect(status().isOk())
                .andExpect(model().attributeExists("specificationTemplate"))
                .andExpect(model().attribute("specificationTemplate", hasProperty("name", is("Test Spec"))))
                .andExpect(model().attribute("specificationTemplate", hasProperty("description", is("desc"))))
                .andExpect(model().attribute("specificationTemplate", hasProperty("notes", is("notes"))))
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));

        mockMvc.perform(get("/specification/template/{specificationId}/edit", EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/template/list"));
    }

    @Test
    void testProcessUpdateSpecificationFormSuccess() throws Exception {
        Mockito.when(this.specificationTemplates.findById(EMPTY_SPECIFICATION_ID)).thenReturn(Optional.empty());
        mockMvc.perform(post("/specification/template/{specificationId}/edit", TEST_SPECIFICATION_ID)
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there")
                        )
                    .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/template/list"));


        mockMvc.perform(post("/specification/template/{specificationId}/edit", TEST_SPECIFICATION_ID)
                                .param("name", "Test Spec")
                                .param("notes", "notes here")
                                .param("description", "description there")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/template/list"));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/specification/template/{specificationId}/edit", TEST_SPECIFICATION_ID)
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specificationTemplate"))
                .andExpect(model().attributeHasFieldErrors("specificationTemplate", "description"))
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));

        mockMvc.perform(post("/specification/template/{specificationId}/edit", EMPTY_SPECIFICATION_ID)
                        .param("name", "Test Spec")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specificationTemplate"))
                .andExpect(model().attributeHasFieldErrors("specificationTemplate", "name"))
                .andExpect(view().name("specification/template/addUpdateSpecificationTemplate"));
    }

    @Test
    void testProcessDeleteSpecificationSuccess() throws Exception {
        mockMvc.perform(post("/specification/template/{specificationId}/delete", TEST_SPECIFICATION_ID)
                        .param("name", testSpecification().getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name("redirect:/specification/template/list"));

    }

    @Test
    void testProcessDeleteSpecificationError() throws Exception {
        mockMvc.perform(post("/specification/template/{specificationId}/delete", EMPTY_SPECIFICATION_ID)
                        .param("name", testSpecification().getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name("redirect:/specification/template/list"));

    }
}
