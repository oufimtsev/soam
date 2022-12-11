package com.soam.specification;

import com.soam.Util;
import com.soam.model.priority.PriorityRepository;
import com.soam.model.priority.PriorityType;
import com.soam.model.specification.Specification;
import com.soam.model.specification.SpecificationRepository;
import com.soam.model.specification.SpecificationTemplateRepository;
import com.soam.web.SpecificationFormController;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(SpecificationFormController.class)
public class SpecificationFormControllerTest {

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

    private Specification testSpecification() {
        PriorityType lowPriority = new PriorityType();
        lowPriority.setName("Low");
        lowPriority.setId(1);
        lowPriority.setSequence(1);
        Specification testSpec = new Specification();
        testSpec.setId(TEST_SPECIFICATION_ID);
        testSpec.setName("Test Spec");
        testSpec.setDescription("desc");
        testSpec.setNotes("notes");
        testSpec.setPriority(lowPriority);
        return testSpec;
    }

    @BeforeEach
    void setup() {
        Specification testSpec = testSpecification();
        given( this.specifications.findByName(testSpec.getName())).willReturn(Optional.of(testSpec));
        given( this.specifications.findByNameIgnoreCase(testSpec.getName())).willReturn(Optional.of(testSpec));
        given( this.specifications.findById(TEST_SPECIFICATION_ID)).willReturn(Optional.of(testSpec));
        given( this.specifications.findById(EMPTY_SPECIFICATION_ID)).willReturn(Optional.empty());

        given( this.specifications.findByNameStartsWithIgnoreCase(eq("Test"), any(Pageable.class)))
                .willReturn(new PageImpl<Specification>(Lists.newArrayList(testSpec)));

    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/specification/new")).andExpect(status().isOk())
                .andExpect(model().attributeExists("specification"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("specificationTemplates"))
                .andExpect(view().name("specification/addUpdateSpecification"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/specification/new").param("name", "New spec")
                        .param("notes", "spec notes").param("description", "Description"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/specification/new").param("name", testSpecification().getName())
                        .param("notes", "spec notes").param("description", "Description"))
                        .andExpect(model().attributeHasErrors("specification"))
                          .andExpect(model().attributeHasFieldErrors("specification", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/addUpdateSpecification"));

        mockMvc.perform(post("/specification/new").param("name", "New spec")
                        .param("notes", "spec notes").param("description", ""))
                .andExpect(model().attributeHasErrors("specification"))
                .andExpect(model().attributeHasFieldErrorCode("specification", "description", "NotBlank"))
                .andExpect(status().isOk())
                .andExpect(view().name("specification/addUpdateSpecification"));
    }
    @Test
    void testInitUpdateSpecificationForm() throws Exception {
        Mockito.when(this.specifications.findById(TEST_SPECIFICATION_ID)).thenReturn(Optional.of(testSpecification()));

        mockMvc.perform(get("/specification/{specificationId}/edit", TEST_SPECIFICATION_ID)).andExpect(status().isOk())
                .andExpect(model().attributeExists("specification"))
                .andExpect(model().attribute("specification", hasProperty("name", is("Test Spec"))))
                .andExpect(model().attribute("specification", hasProperty("description", is("desc"))))
                .andExpect(model().attribute("specification", hasProperty("notes", is("notes"))))
                .andExpect(view().name("specification/addUpdateSpecification"));

        mockMvc.perform(get("/specification/{specificationId}/edit", EMPTY_SPECIFICATION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/find"));
    }

    @Test
    void testProcessUpdateSpecificationFormSuccess() throws Exception {
        Mockito.when(this.specifications.findById(EMPTY_SPECIFICATION_ID)).thenReturn(Optional.empty());
        mockMvc.perform(post("/specification/{specificationId}/edit", TEST_SPECIFICATION_ID)
                        .param("name", "New Test Specification")
                        .param("notes", "notes here")
                        .param("description", "description there")
//                        .param("priority", "1")
                        )
                    .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/{specificationId}"));

        mockMvc.perform(post("/specification/{specificationId}/edit", TEST_SPECIFICATION_ID)
                                .param("name", "New Test Specification")
                                .param("notes", "notes here")
                                .param("description", "description there")
//                        .param("priority", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/specification/{specificationId}"));
    }

    @Test
    void testProcessUpdateSpecificationFormHasErrors() throws Exception {
        mockMvc.perform(post("/specification/{specificationId}/edit", TEST_SPECIFICATION_ID)
                        .param("name", "New Test Specification")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specification"))
                .andExpect(model().attributeHasFieldErrors("specification", "description"))
                .andExpect(view().name("specification/addUpdateSpecification"));

        mockMvc.perform(post("/specification/{specificationId}/edit", EMPTY_SPECIFICATION_ID)
                        .param("name", "Test Spec")
                        .param("notes", "notes")
                        .param("description", "")
                ).andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("specification"))
                .andExpect(model().attributeHasFieldErrors("specification", "name"))
                .andExpect(view().name("specification/addUpdateSpecification"));
    }

    @Test
    void testProcessDeleteSpecificationSuccess() throws Exception {
        mockMvc.perform(post("/specification/{specificationId}/delete", TEST_SPECIFICATION_ID)
                        .param("name", testSpecification().getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(Util.SUB_FLASH))
                .andExpect(view().name("redirect:/specification/list"));

    }

    @Test
    void testProcessDeleteSpecificationError() throws Exception {
        mockMvc.perform(post("/specification/{specificationId}/delete", EMPTY_SPECIFICATION_ID)
                        .param("name", testSpecification().getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect( flash().attributeExists(Util.DANGER))
                .andExpect(view().name("redirect:/specification/list"));

    }
}
